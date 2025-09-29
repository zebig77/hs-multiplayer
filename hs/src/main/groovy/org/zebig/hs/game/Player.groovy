package org.zebig.hs.game

import org.zebig.hs.mechanics.Trigger
import org.zebig.hs.mechanics.buffs.Buff
import org.zebig.hs.mechanics.events.AnyMinionIsPlayed
import org.zebig.hs.mechanics.events.AnyPowerDamageIsEvaluated
import org.zebig.hs.mechanics.events.AnySecretIsRevealed
import org.zebig.hs.mechanics.events.AnySpellDamageIsEvaluated
import org.zebig.hs.mechanics.events.AnySpellIsPlayed
import org.zebig.hs.mechanics.events.BeforeItsControllerPlaysACard
import org.zebig.hs.mechanics.events.BeforePlay
import org.zebig.hs.mechanics.events.ItComesInPlay
import org.zebig.hs.mechanics.events.ItIsPlayed
import org.zebig.hs.mechanics.events.ItsControllerPlaysACard
import org.zebig.hs.mechanics.events.SpellTargetSelected
import org.zebig.hs.mechanics.events.ThisPowerIsUsed
import org.zebig.hs.state.ListState
import org.zebig.hs.state.MapState

class Player extends ScriptObject {

	MapState ps

	String name
	Hand hand
	Deck deck
	ListState<Card> minions
	ListState<Card> secrets
	PlayerArtefact artefact // container for player's triggers

	// simulates player's answers for tests
	def next_choices = []

	Player(Game game, String name, Hero hero, Deck deck) {
        super(game)
		ps = new MapState()
		this.name = name
		this.hero = hero
		this.deck = deck
        this.minions = new ListState<Card>(game)
        this.secrets = new ListState<Card>(game)
		this.hand = new Hand(this)
		this.overload = 0
		this.nb_cards_played_this_turn = 0
		this.available_mana = 0
		this.max_mana = 0
		this.fatigue = 0
		this.artefact = new PlayerArtefact(game, "artefact of $name")
	}

	Hero getHero() { ps.hero }
	void setHero(Hero h) { ps.hero = h; h.controller = this }

	int getOverload() { ps.overload }
	void setOverload(int o) { ps.overload = o }

	int getNb_cards_played_this_turn() { ps.nb_cards_played_this_turn }
	void setNb_cards_played_this_turn(int n) { ps.nb_cards_played_this_turn = n }

	int getAvailable_mana() { ps.available_mana }
	void setAvailable_mana(int am) { ps.available_mana = am }

	int getMax_mana() { return ps.max_mana }
	void setMax_mana(int max_mana) {	ps.max_mana = max_mana }

	int getFatigue() { return ps.fatigue	}
	void setFatigue(int fatigue) { ps.fatigue = fatigue }

	String stats() {
		StringWriter sw = new StringWriter()
		sw << "${name}/${hero.name}"
		sw << " Mana=${available_mana}"
		sw << " Hlt=${hero.health}"
		sw << " Att=${hero.attack}"
		sw << " Arm=${hero.armor}"
		sw << " Hnd=${hand.size()}"
		sw << " Dck=${deck.size()}"
		sw << " Min=${minions.size()}"
		sw << " Sec=${secrets.size()}"
		return sw.toString()
	}

	def add_available_mana(int amount) {
		available_mana += amount
		if (available_mana > 10) {
			available_mana = 10
		}
		println "      . available mana for $this = $available_mana"
	}

	def add_max_mana(int amount) {
		max_mana += amount
		if (max_mana > 10) { max_mana = 10 }
		if (max_mana < 0) { max_mana = 0 }
		println "      . max mana for $this = $max_mana"
	}

	def add_overload(int amount) {
		this.overload += amount
		println "      . $this's overload = ${overload}"
	}

	def choose(List<String> choices, List<Closure> scripts) {
		//println " - $this has to choose between '${choices[0]}' and '${choices[1]}'"
		if (next_choices.isEmpty()) {
			throw new IllegalActionException("no choice made !")
		}
		def choice = next_choices.remove(0)
		if (! choices.contains(choice)) {
			throw new IllegalActionException("'$choice' is not a valid answer")
		}
		println "      . $this chooses '$choice'"
		if (choice == choices[0]) {
			scripts[0].call()
		} else {
			scripts[1].call()
		}
	}

	Card create_secret(Card c) {
		println "      . adding $c to ${this}'s secrets"
		secrets.add(c)
		new ItComesInPlay(c).check()
		return c
	}

	def update_minions_place() {
		// re-compute the minions places
		def place=0
		this.minions().sort{it.place}.each  {
			if ((it as Card).place != place) {
				println "      . $it moved to x=$place"
				(it as Card).place = place
			}
			place++
		}
	}

	def reveal(Card c) {
		// 11 March 2014 Patch: "Secrets can now only activate on your opponent�s turn."
		assert c.controller != game.active_player
		println "      . secret '$c.name' is revealed"
		new AnySecretIsRevealed(c).check()
		secrets.remove(c)
	}

	def draw(int n_cards) {
		if (n_cards <= 0) {
			return
		}
		println "      . $this draws $n_cards card" + (n_cards > 1 ? "s" : "")
		n_cards.times {
			Card c = deck.draw()
			if (c != null) {
				c.controller = this
				hand.add(c)
			}
			else {
				println "      . $this cannot draw !"
				fatigue += 1
				println "      . fatigue = $fatigue !"
				hero.receive_damage(fatigue)
				game.check_end_of_game()
			}
		}
	}

	// previous controller loses control
	def take_control(Card c) {
		assert c != null
		assert c.is_a_minion()
		assert c.controller != null
		// take minion out of the other player control
		println "      . ${c.controller} loses control of $c"
		c.controller.minions.remove(c)
		c.controller.update_minions_place()
		gain_control(c)
	}

	def gain_control(Card c) {
		controls(c, minions.size())
	}

	def controls(Card c, int place) {
		assert c != null
		assert c.is_a_minion()
		c.controller = this
		println "      . ${this} gains control of $c"
		if (c.place < minions.size()) { // insert c
			minions().findAll{it.place >= place}.each {
				(it as Card).place++
				println "      . $it moved to x=$it.place"
			}
		}
		c.place = place
		this.minions.add(c)
	}

	int get_power_damage(int amount) {
		def e = new AnyPowerDamageIsEvaluated(this, amount)
		e.check()
		return amount + e.power_damage_increase
	}

	int get_spell_damage(int amount) {
		AnySpellDamageIsEvaluated e = new AnySpellDamageIsEvaluated(this, amount)
		def buff_spell_damage_increase = 0
		minions.each{ minion ->
			minion.get_buffs().findAll{ it.spell_damage_increase != 0 }.each { Buff buff ->
				buff_spell_damage_increase += buff.spell_damage_increase
				println "      . spell damage modified by $buff"
			}
		}
		e.spell_damage_increase = buff_spell_damage_increase
		e.check()
		return amount + e.spell_damage_increase
	}

	boolean has_combo() {
		return (nb_cards_played_this_turn > 0)
	}

	boolean have_combo() {
		return has_combo()
	}

	def init_turn() {
		add_max_mana(1)
		available_mana = max_mana - overload
		overload = 0
		nb_cards_played_this_turn = 0
		minions.each {Card minion ->
			minion.attack_counter = 0
			minion.just_summoned = false
		}
		hero.attack_counter = 0
		hero.power.use_counter = 0
	}

	// create a copy to avoid java.util.ConcurrentModificationException
	ArrayList<Card> minions() {
		// if a minion is being played it is not yet considered part of the minions
		return new ArrayList<Card>(minions.findAll{ Card c ->
            !c.is_being_played && !c.is_destroyed
		}).sort{it.play_order}
	}

	/** play a card from hand, with place specified */
	def play(Card c, int place) {

		// check that card can be played
		println "\n- $this plays $c"
		if (minions.size() >= 7 && c.is_a_minion()) {
			throw new IllegalActionException("no room in battlefield to play a minion")
		}

		// Chance for cost reduction effects that cannot be used with
		// CostIsEvaluated events.
		new BeforeItsControllerPlaysACard(this, c).check()

		def mana_to_pay = c.get_cost()
		if (mana_to_pay > available_mana) {
			throw new IllegalActionException("cost cannot be paid")
		}

		// if it is a spell with target, check at least one valid exists
		if (c.is_a_spell() && c.get_targets != null) {
			c.get_targets.each { Closure gt ->
				if (gt != null) {
					List<Target> possible_targets = gt.call()
					check(possible_targets.size() > 0, "no valid target")
				}
			}
		}

		// last chance to stop it
		new BeforePlay(c).check()

		// ok, pay the cost and remove it from hand
		add_available_mana( -mana_to_pay )
		hand.remove(c)

		if (c.is_a_spell()) {

			// but it can be countered
			AnySpellIsPlayed e = new AnySpellIsPlayed(c)
            e.check()
			if (e.stop_action) {
				println "      . $c is not played"
				return
			}
		}

		// place a minion in the battlefield or equip a weapon
		if (c.is_a_minion()) {
			game.summon(this, c, place)
		} else 	if (c.is_a_weapon()) {
			hero.equip_weapon(new Weapon( c.card_definition ) )
		}

		// play battlecry or spell effect
		c.is_being_played = true // excluded from selection lists
		new ItIsPlayed(c).check()
		if (c.is_a_minion()) {
			new AnyMinionIsPlayed(c).check()
		}
		if (c.controller == this) {
			new ItsControllerPlaysACard(this, c).check()
		}
		c.is_being_played = false

		// for combo test
		nb_cards_played_this_turn++

		return c
	}

	def play(Card c) {
		def played
		if (c.is_a_minion()) {
			played = play(c, this.minions.size()) // rightmost position
		} else {
			played = play(c, 0)
		}
		// in case a minion has its health = 0 but remains in battlefield
		game.remove_dead_from_battlefield()
		game.check_end_of_game()
		return played
	}

	Card select_card(List<Card> choices) {
		return select(1, choices)
	}

	Target select_target(List<Target> choices) {
		return select(1, choices)
	}

	Target select_spell_target(List<Target> choices) {
		def choice = select(1, choices)
		// target can be changed by effect
		def e = new SpellTargetSelected(this, choice)
		e.check()
		return e.choice
	}

	Target select(int howmany, List<Target> choices) {
		if (choices.isEmpty()) {
			println "      . list of choices is empty, no selection"
			return null
		}
		if (howmany > choices.size()) {
			throw new IllegalActionException("Nombre de choix disponibles insuffisant pour $name")
		}
		if (howmany > next_choices.size()) {
			// TODO pick random
			throw new IllegalActionException("Nombre de choix disponibles insuffisant pour $name")
		}
		if (! choices.contains(next_choices[0])) {
			throw new IllegalActionException("${next_choices[0]} is not a valid choice (${choices})")
		}
		if (howmany == 1) {
			println "      . selected: ${next_choices.getAt(0)}"
			return next_choices.remove(0) // single value
		}
		def result = []
		howmany.times{
			next_choices.remove(0)
			result.add( choices )
		}
		println "      . selected: $result"
		return result // multiple values
	}

	String toString() {
		return name
	}

	def use_hero_power() {
		StringBuilder reason = new StringBuilder()
		if (hero.can_use_power(reason) == false) {
			throw new IllegalActionException("Cannot use power (${reason.toString()})")
		}
		add_available_mana(-hero.power.cost)
		println "\n- $this uses ${hero}'s power: ${hero.power.name}"
		new ThisPowerIsUsed(hero.power).check()
		hero.power.use_counter++
		// in case a minion has its health = 0 but remains in battlefield
		game.remove_dead_from_battlefield()
		game.check_end_of_game()
	}

	@Override
	public Trigger add_trigger(Class event_class, Closure c) {
		return artefact.add_trigger(event_class, c)
	}

	@Override
	public Trigger add_trigger(Class event_class, Closure c, String comment) {
		return artefact.add_trigger(event_class, c, comment)
	}

}

