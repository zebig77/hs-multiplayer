package org.zebig.hs.game

import org.zebig.hs.logger.Log
import org.zebig.hs.mechanics.Trigger
import org.zebig.hs.mechanics.buffs.Buff
import org.zebig.hs.mechanics.events.AnyCostIsEvaluated
import org.zebig.hs.mechanics.events.BeforePlay
import org.zebig.hs.mechanics.events.ItsCostIsEvaluated

class Card extends Target {

	List<String> druid_choices // druid
	List<Closure> get_targets

	Card(CardDefinition cd) {
		super(cd.name, cd.type, cd.max_health, cd.game)
		card_definition = cd
		place = 0
		just_summoned = false
		is_enraged = false
		is_a_secret = false
		play_order = 0
		is_in_play = false
		init()
	}
	
	CardDefinition getCard_definition() { ps.card_definition }
	void setCard_definition(CardDefinition cd) { ps.card_definition = cd }
	
	String getType() { ps.type }
	void setType(String t) { ps.type = t }
	
	String getCreature_type() { ps.creature_type }
	void setCreature_type(String ct) { ps.creature_type = ct }
	
	int getCost() { ps.cost }
	void setCost(int c) { ps.cost = c }
	
	String getText() { ps.text }
	void setText(String t) { ps.text = t }
	
	void addText(String s) {
		if (text == null || text == '') {
			ps.text = s
		}
		else {
			if (!text.contains(s)) {
				if (text[text.size()-1] == '.') {
					ps.text = ps.text+' '+s
				}
				else {
					ps.text = ps.text+'. '+s
				}
			}
		}
		Log.info "      . $this text = ${ps.text}"
	}
	
	int getPlace() { ps.place }
	void setPlace(int p) { ps.place = p }
	
	boolean getJust_summoned() { ps.just_summoned }
	void setJust_summoned(boolean js) { ps.just_summoned = js }
	
	boolean getIs_enraged() { ps.is_enraged }
	void setIs_enraged(boolean ie) { ps.is_enraged = ie }
	
	boolean getIs_a_secret() { ps.is_a_secret }
	void setIs_a_secret(boolean ias) { ps.is_a_secret = ias }
	
	int getPlay_order() { ps.play_order }
	void setPlay_order(int po) { ps.play_order = po }
	
	boolean getIs_in_play() { ps.is_in_play }
	void setIs_in_play(boolean iip) { ps.is_in_play = iip }
	
	boolean has_battlecry() {
		text.contains("Battlecry: ")
	}
	
	boolean has_deathrattle() {
		text.contains("Deathrattle: ")
	}
	
	def init() {
		name = card_definition.name
		type = card_definition.type
		creature_type = card_definition.creature_type
		cost = card_definition.cost
		attack = card_definition.attack
		health = card_definition.max_health
		max_health = card_definition.max_health
		text = card_definition.text
		triggers.clear()
		card_definition.triggers.each{ Trigger t ->
			triggers.add( new Trigger(t.event_class, t.script, this, t.comment) )
		}
		buffs.clear()
		is_enraged = false
		is_a_secret = card_definition.is_a_secret
		play_order = 0
		is_in_play = false
		druid_choices = card_definition.druid_choices
		get_targets = card_definition.get_targets
	}
	
	def activate_if(boolean condition, Closure c) {
		if (! condition) {
			return
		}
		if (this.controller == game.active_player) {
			return // secrets are active only during opponent's turn
		}
		controller.reveal(this)
		c.call()
	}
	
	boolean can_be_played() {
		if (get_cost() > game.active_player.available_mana) {
			return false
		}
		try {
			new BeforePlay(this).check()
		}
		catch (IllegalActionException e) {
			// pre-conditions non satisfied
			Log.info e.toString()
			return false
		}
		if (this.is_a_minion() && game.active_player.minions.size() == 7 ) {
			return false
		}
		return true
	}

	def deal_spell_damage(int amount, List targets) {
		int damage = controller.get_spell_damage(amount)
		deal_damage(damage, targets)
	}

	def deal_spell_damage(int amount, Target t) {
		int damage = controller.get_spell_damage(amount)
		deal_damage(damage, t)
	}
	
	int evaluate_cost(int c) {
		// check if any minion has a cost modifier effect
		AnyCostIsEvaluated global_e = new AnyCostIsEvaluated(this).check() as AnyCostIsEvaluated
		// check if the card itself has a cost modifier
		ItsCostIsEvaluated local_e = new ItsCostIsEvaluated(this).check() as ItsCostIsEvaluated
		def buff_cost_increase = 0
		def event_cost_increase = global_e.cost_increase + local_e.cost_increase
		get_buffs().each {
			buff_cost_increase += it.cost_increase
		}
		if (global_e.cost_change != -1) {
			c = global_e.cost_change // set the new cost to a different value
		}
		def result = c + buff_cost_increase + event_cost_increase
		if (global_e.lowest_cost != -1) {
			if (result < global_e.lowest_cost) {
				result = global_e.lowest_cost
			}
		}
		//Log.info "      . evaluated cost = $result"
		return result
	}
	
	Card get_copy() {
		Card result = game.new_card(this.name)
		copy(this, result)
		return result
	}

	int get_cost() {
		def c = evaluate_cost(this.cost)
		if (c < 0) {
			c = 0
		}
		return c
	}

	boolean is_a_beast() {
		return (creature_type == "beast")
	}

	boolean is_a_demon() {
		return (creature_type == "demon")
	}

	boolean is_a_murloc() {
		return (creature_type == "murloc")
	}

	boolean is_a_pirate() {
		return (creature_type == "pirate")
	}
	
	boolean is_a_totem() {
		return (creature_type == "totem")
	}
	
	boolean is_revealed() { // for a secret
		assert this.is_a_spell()
		return !controller.secrets.contains(this)
	}

	Card left_neighbor() {
		return controller.minions.find{ it.place == this.place-1 }
	}
	
	
	List<Card> neighbors() {
		def r = right_neighbor()
		def l = left_neighbor()
		def result = []
		if (r != null) { result.add(r) }
		if (l != null) { result.add(l) }		
		return result
	}

	def return_to_hand() {
		def ctl = this.controller
		leave_play()
		init()  // reset to card definition attributes
		ctl.hand.add(this)
	}
	
	Card right_neighbor() {
		return controller.minions.find{ it.place == this.place+1 }
	}
	
	static copy(Card from, Card to) {
		if (from == null) {
			return
		}
		assert to != null
		to.name = from.name
		to.attack = from.attack
		to.attack_counter = from.attack_counter
		to.buffs.clear()
		from.buffs.each { Buff buff ->
			to.buffs.add(buff)
		}
		to.buffs.each { Buff b -> 
			if (b.target == from) {
				b.target = to
			} 
		}
		to.card_definition = from.card_definition
		to.controller = null
		to.cost = from.cost
		to.creature_type = from.creature_type
		to.health = from.health
		to.is_a_secret = from.is_a_secret
		to.is_attacking	= false
		to.is_being_played 	= false
		to.is_destroyed	= false
		to.is_enraged = from.is_enraged
		to.is_in_play = from.is_in_play
		to.just_summoned = from.just_summoned
		to.max_health = from.max_health
		to.play_order = 0
		to.target_type = from.target_type
		to.text	= from.text
		to.triggers.clear()
		from.triggers.each {
			to.triggers.add(it as Trigger)
		}
		to.type	= from.type
	}

}
