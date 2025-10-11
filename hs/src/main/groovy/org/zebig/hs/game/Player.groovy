package org.zebig.hs.game

import org.zebig.hs.logger.Log
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
import java.beans.PropertyChangeEvent

import static org.zebig.hs.state.GameChange.Type.*

class Player extends ScriptObject {

	def state = [:] as ObservableMap
	String name
	Hand hand
    Board board
	Deck deck
	Secrets secrets
	PlayerArtefact artefact // container for player's triggers

	// simulates player's answers for tests
	def next_choices = []

	Player(Game game, String name) {
        super(game)
		this.name = name
		this.hand = new Hand(this)
        this.board = new Board(this)
        this.secrets = new Secrets(this)
		this.overload = 0
		this.nb_cards_played_this_turn = 0
		this.available_mana = 0
		this.max_mana = 0
		this.fatigue = 0
		this.artefact = new PlayerArtefact(game, "artefact of $name")
        this.state.addPropertyChangeListener {
            process_state_change(it)
        }
	}

    void process_state_change(PropertyChangeEvent event) {
        game.transaction?.process_state_change(state, event)
    }

    void setDeck(Deck deck) {
        this.deck = deck
    }

    Hero getHero() { state.hero }
	void setHero(Hero h) { state.hero = h; h.controller = this }

	int getOverload() { default0(state.overload) }
	void setOverload(int o) { state.overload = o }

	int getNb_cards_played_this_turn() { state.nb_cards_played_this_turn }
	void setNb_cards_played_this_turn(int n) { state.nb_cards_played_this_turn = n }

	int getAvailable_mana() { default0(state.available_mana) }
	void setAvailable_mana(int am) {
        state.available_mana = am
        game.transaction?.record(ManaStatusChanged, this.name, [
                player_name:this.name,
                max_mana:max_mana,
                available_mana:available_mana,
                overload:overload])
    }

    static int default0(Object x) {
        if (x == null) { return 0 }
        return x as int
    }

	int getMax_mana() { return default0(state.max_mana) }
	void setMax_mana(int max_mana) { state.max_mana = max_mana }

	int getFatigue() {return default0(state.fatigue) }
	void setFatigue(int fatigue) { state.fatigue = fatigue }

	String stats() {
		StringWriter sw = new StringWriter()
		sw << "${name}/${hero.name}"
		sw << " Mana=${available_mana}"
		sw << " Hlt=${hero.health}"
		sw << " Att=${hero.attack}"
		sw << " Arm=${hero.armor}"
		sw << " Hnd=${hand.size()}"
		sw << " Dck=${deck.size()}"
		sw << " Brd=${board.size()}"
		sw << " Sec=${secrets.size()}"
		return sw.toString()
	}

	def add_available_mana(int amount) {
		available_mana += amount
		if (available_mana > 10) {
			available_mana = 10
		}
		Log.info "      . available mana for $this = $available_mana"
	}

	def add_max_mana(int amount) {
		max_mana += amount
		if (max_mana > 10) { max_mana = 10 }
		if (max_mana < 0) { max_mana = 0 }
		Log.info "      . max mana for $this = $max_mana"
	}

	def add_overload(int amount) {
		this.overload += amount
		Log.info "      . $this's overload = ${overload}"
	}

	def choose(List<String> choices, List<Closure> scripts) {
		//Log.info " - $this has to choose between '${choices[0]}' and '${choices[1]}'"
		if (next_choices.isEmpty()) {
			throw new IllegalActionException("no choice made !")
		}
		def choice = next_choices.remove(0)
		if (! choices.contains(choice)) {
			throw new IllegalActionException("'$choice' is not a valid answer")
		}
		Log.info "      . $this chooses '$choice'"
		if (choice == choices[0]) {
			scripts[0].call()
		} else {
			scripts[1].call()
		}
	}

	Card create_secret(Card c) {
		Log.info "      . adding $c to ${this}'s secrets"
		secrets.add(c)
		new ItComesInPlay(c).check()
		return c
	}

	def update_minions_place() {
		// re-compute the minions places
		def place=0
		this.minions().sort{it.place}.each  {
			if ((it as Card).place != place) {
				Log.info "      . $it moved to x=$place"
				(it as Card).place = place
			}
			place++
		}
	}

	def reveal(Card c) {
		// 11 March 2014 Patch: "Secrets can now only activate on your opponent's turn."
		assert c.controller != game.active_player
		Log.info "      . secret '$c.name' is revealed"
		new AnySecretIsRevealed(c).check()
		secrets.remove(c)
	}

	List<Card> draw(int n_cards) {
		if (n_cards <= 0) {
			return []
		}
		Log.info "      . $this draws $n_cards card" + (n_cards > 1 ? "s" : "")
        def result = []
		n_cards.times {
			Card c = deck.draw()
			if (c != null) {
				c.controller = this
				hand.add(c)
                result << c
                if (c.is_a_spell()) {
                    game.transaction?.record(CardDrawn, c.id as String, [
                            player_name: this.name,
                            card_id:c.id,
                            name: c.card_definition.name,
                            type: c.card_definition.type,
                            cost: c.card_definition.cost,
                            text: c.card_definition.text
                    ], false)
                }
                else { // minion or weapon
                    game.transaction?.record(CardDrawn, c.id as String, [
                            player_name: this.name,
                            card_id:c.id,
                            name: c.card_definition.name,
                            type: c.card_definition.type,
                            cost: c.card_definition.cost,
                            text: c.card_definition.text,
                            attack: c.card_definition.attack,
                            max_health: c.card_definition.max_health
                    ], false)
                }
			}
			else {
				Log.info "      . $this cannot draw !"
				fatigue += 1
				Log.info "      . fatigue = $fatigue !"
				hero.receive_damage(fatigue)
				game.check_end_of_game()
			}
		}
        return result
	}

	// previous controller loses control
	def take_control(Target t) {
		assert t != null
		assert t.is_a_minion()
		assert t.controller != null
		// take minion out of the other player control
		Log.info "      . ${t.controller} loses control of $t"
		t.controller.board.remove(t as Card)
		t.controller.update_minions_place()
		gain_control(t)
	}

	def gain_control(Target t) {
		controls(t, board.size())
	}

	def controls(Target t, int place) {
		assert t != null
		assert t.is_a_minion()
		t.controller = this
		Log.info "      . ${this} gains control of $t"
		if (t.place < board.size()) { // insert t
			minions().findAll{it.place >= place}.each {
				it.place++
				Log.info "      . $it moved to x=$it.place"
			}
		}
		t.place = place
		this.board.add(t as Card)
	}

	int get_power_damage(int amount) {
		def e = new AnyPowerDamageIsEvaluated(this, amount)
		e.check()
		return amount + e.power_damage_increase
	}

	int get_spell_damage(int amount) {
		AnySpellDamageIsEvaluated e = new AnySpellDamageIsEvaluated(this, amount)
		def buff_spell_damage_increase = 0
		minions().each{ minion ->
			minion.get_buffs().findAll{ it.spell_damage_increase != 0 }.each { Buff buff ->
				buff_spell_damage_increase += buff.spell_damage_increase
				Log.info "      . spell damage modified by $buff"
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

	void init_turn() {
		add_max_mana(1)
		available_mana = max_mana - overload
		overload = 0
		nb_cards_played_this_turn = 0
		minions().each {Card minion ->
			minion.attack_counter = 0
			minion.just_summoned = false
		}
		hero.attack_counter = 0
		hero.power.use_counter = 0
	}

	// create a copy to avoid java.util.ConcurrentModificationException
	ArrayList<Card> minions() {
		// if a minion is being played it is not yet considered part of the minions
        List<Card> result = []
        board.minions().each {
            if (!it.is_being_played && !it.is_destroyed) {
                result << it
            }
        }
        return result.sort {it.play_order}
	}

	/** play a card from hand, with place specified */
	Card play(Card c, int place) {

		// check that card can be played
		Log.info "\n- $this plays $c"

		if (board.size() >= 7 && c.is_a_minion()) {
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
				Log.info "      . $c is not played"
				return null
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

        if (c.is_a_spell()) {
            game.transaction?.record(CardPlayed, c.id as String, [
                    player_name: this.name,
                    card_id    : c.id as String,
                    position   : place as String,
                    name       : c.name,
                    type       : c.type,
                    cost       : c.cost,
                    text       : c.text,
            ])
        }
        else {
            assert c.is_a_minion() || c.is_a_weapon()
            game.transaction?.record(CardPlayed, c.id as String, [
                    player_name: this.name,
                    card_id    : c.id as String,
                    position   : place as String,
                    name       : c.name,
                    type       : c.type,
                    cost       : c.cost,
                    text       : c.text,
                    attack     : c.attack,
                    max_health : c.max_health
            ])
        }

		// for combo test
		nb_cards_played_this_turn++

		return c
	}

	def play(Card c) {
		def played
		if (c.is_a_minion()) {
			played = play(c, this.board.size()) // rightmost position
		} else {
			played = play(c, 0)
		}
		// in case a minion has its health = 0 but remains in battlefield
		game.remove_dead_from_battlefield()
		game.check_end_of_game()
		return played
	}

	Card select_card(List<Card> choices) {
		return select(1, choices) as Card
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
			Log.info "      . list of choices is empty, no selection"
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
			Log.info "      . selected: ${next_choices.getAt(0)}"
			return next_choices.remove(0) // single value
		}
		def result = []
		howmany.times{
			next_choices.remove(0)
			result.add( choices )
		}
		Log.info "      . selected: $result"
		return result as Target // multiple values
	}

	String toString() {
		return name
	}

	def use_hero_power() {
		StringBuilder reason = new StringBuilder()
		if (!hero.can_use_power(reason)) {
			throw new IllegalActionException("Cannot use power (${reason.toString()})")
		}
		add_available_mana(-hero.power.cost)
        Log.info "\n- $this uses ${hero}'s power: ${hero.power.name}"
		new ThisPowerIsUsed(hero.power).check()
		hero.power.use_counter++
		// in case a minion has its health = 0 but remains in battlefield
		game.remove_dead_from_battlefield()
		game.check_end_of_game()
	}

	@Override
	Trigger add_trigger(Class event_class, Closure c) {
		return artefact.add_trigger(event_class, c)
	}

	@Override
	Trigger add_trigger(Class event_class, Closure c, String comment) {
		return artefact.add_trigger(event_class, c, comment)
	}

}

