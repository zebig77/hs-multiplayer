package org.zebig.hs.game

import org.zebig.hs.mechanics.events.AnyBuffIsEvaluated

import static org.zebig.hs.mechanics.buffs.BuffType.*
import org.zebig.hs.mechanics.buffs.Buff
import org.zebig.hs.mechanics.buffs.BuffType
import org.zebig.hs.mechanics.events.AnyAttackIsEvaluated
import org.zebig.hs.mechanics.events.AnyBuffListIsEvaluated
import org.zebig.hs.mechanics.events.AnyCharacterIsHealed
import org.zebig.hs.mechanics.events.AnyHealthIsEvaluated
import org.zebig.hs.mechanics.events.AnyHeroTakesDamage
import org.zebig.hs.mechanics.events.AnyMinionDealsDamage
import org.zebig.hs.mechanics.events.AnyMinionDies
import org.zebig.hs.mechanics.events.AnyMinionTakesDamage
import org.zebig.hs.mechanics.events.AnyPowerHealingIsEvaluated
import org.zebig.hs.mechanics.events.AnySpellHealingIsEvaluated
import org.zebig.hs.mechanics.events.EnragedOff
import org.zebig.hs.mechanics.events.EnragedOn
import org.zebig.hs.mechanics.events.ItDealsDamage
import org.zebig.hs.mechanics.events.ItIsDestroyed
import org.zebig.hs.mechanics.events.ItTakesDamage
import org.zebig.hs.mechanics.events.ItsControllerHeals
import org.zebig.hs.state.ListState
import org.zebig.hs.state.MapState

class Target extends GameObject {
	
	MapState ps
	
	ListState<Buff> buffs

	final int id
	
	Target(String name, String target_type, int max_health, Game game) {
        super(game)
		this.id = game.next_id++
		this.ps = new MapState(game)
        this.buffs = new ListState<Buff>(game)
		this.name = name
		this.target_type = target_type
		this.health = max_health
		this.max_health = max_health
		this.attack = 0
		this.attack_counter = 0
		this.is_being_played = false
		this.is_attacking = false
		this.is_destroyed = false
	}
	
	List<Buff> get_buffs() {
		def result = new ArrayList<Buff>(buffs.storage)
		def e = new AnyBuffListIsEvaluated(this)
		e.check()
		if (e.additional_buffs != []) {
			println "      . additional buffs added: ${e.additional_buffs}"
		}
		return result + e.additional_buffs
	}
	
	Buff gains(BuffType bt) {
		def buff = Buff.create_buff(bt, this)
		this.buffs.add(buff)
		return buff
	}

	Buff gains(String bs) {
		def buff = Buff.create_buff(bs, this)
		this.buffs.add(buff)
		return buff
	}

	Buff have(String bs) {
		return gains(bs) // just a synonym
	}

	Buff have(BuffType bt) {
		return gains(bt) // just a synonym
	}

	def remove_first_buff(BuffType bt) {
		remove_buff( buffs.find{Buff b -> b.buff_type == bt } )
	}

	def remove_first_buff(String buff_type_name) {
		def btn = normalized(buff_type_name)
		remove_buff( buffs.find{Buff b -> b.buff_type.name == btn} )
	}

	def remove_all_buff(BuffType bt) {
		def to_remove = buffs.findAll{it.buff_type == bt}
		to_remove.each { remove_buff(it) }
	}

	def remove_buff(Buff b) {
		if (b != null) {
			b.remove_effect()
			this.buffs.remove(b)
		}
	}
	
	int getAttack() { ps.attack }
	void setAttack(int a) { ps.attack = a }
	
	int getAttack_counter() { ps.attack_counter }
	void setAttack_counter(int ac) { ps.attack_counter = ac }
	
	Player getController() { ps.controller }
	void setController(Player c) { ps.controller = c }
	
	String getName() { ps.name }
	void setName(String n) { ps.name = n }
	
	String getTarget_type() { ps.target_type }
	void setTarget_type(String tt) { ps.target_type = tt }
	
	String getText() { ps.text }
	void setText(String text) { ps.text = text }
	
	int getHealth() { ps.health }
	void setHealth(int h) { ps.health = h }
	
	int getMax_health() { ps.max_health }
	void setMax_health(int maxh) { ps.max_health = maxh }
	
	boolean getIs_being_played() { ps.is_being_played }
	void setIs_being_played(boolean bp) { ps.is_being_played = bp }
	 
	boolean getIs_attacking() { ps.is_attacking }
	void setIs_attacking(boolean at) { ps.is_attacking = at }
	 
	boolean getIs_destroyed() { ps.is_destroyed }
	void setIs_destroyed(boolean at) { ps.is_destroyed = at }

    boolean has_buff(BuffType buff_type) {
        def e = new AnyBuffIsEvaluated(buff_type, this)
        // check if a permanent effect enforces having or not having a buff
        for (t in e.get_scope()) {
            e.origin = t
            t.check_event(e)
            if (e.stop_action) {	// do not check if the target has really the buff
                return e.has_buff 	// return
            }
        }
        return get_buffs().find{ (it.buff_type == buff_type) } != null
    }

    boolean has_buff(String buff_name) {
        List<Buff> all_buffs = get_buffs()
        for(int i=0; i < all_buffs.size(); i++) {
            Buff b = all_buffs.get(i)
            String bn = b.toString().toLowerCase()
            if (bn == buff_name.toLowerCase()) {
                return true
            }
        }
        return false
    }

    boolean can_be_targeted() {
		if (this.has_stealth()) {
			return false
		}
		if (this.has_buff(CANNOT_BE_TARGETED_BY_SPELL_OR_POWER)) {
			return false
		}
		return true
	}
	
	boolean can_attack(StringBuilder fail_reason=new StringBuilder()) {
		if (this.has_buff(CANNOT_ATTACK)) {
			fail_reason << "${target_type} '${name}' [${id}] cannot attack."
			return false
		}
		if (this.is_a_minion()) {
			Card c = this as Card
			if (c.just_summoned && !c.has_charge()) {
				fail_reason << "$this cannot attack : it was just summoned"
				return false
			}
			if (c.get_attack() <= 0) {
				fail_reason << "$this cannot attack: it has no attack power"
				return false
			}
		}
		if (this.is_a_hero()) {
			Hero h = this as Hero
			if (h.get_attack() <= 0) {
				fail_reason << "$this cannot attack: it has no attack power"
				return false
			}
		}
		if (this.attack_counter > 0) {
			if (this.is_a_minion() && !this.has_buff(WINDFURY)) {
				fail_reason << "$this cannot attack : it has already attacked"
				return false
			}
			if (this.is_a_hero()) {
				Hero h = this as Hero
				if (h.weapon == null || !h.weapon.has_buff(WINDFURY)) {
					fail_reason << "$this cannot attack : it has already attacked"
					return false
				}
			}
			if (this.attack_counter > 1) {
				fail_reason << "$this cannot attack : it has already attacked twice"
				return false
			}
		}
		if (this.is_frozen()) {
			fail_reason << "$this cannot attack : it is frozen"
			return false
		}
		return true
	}

	void check_can_attack() {
		StringBuilder fail_reason = new StringBuilder()
		if (!this.can_attack(fail_reason)) {
			throw new IllegalActionException(fail_reason.toString())
		}
	}
	
	boolean can_be_attacked() {
		if (this.has_stealth()) {
			return false
		}
		return true
	}

	void check_can_be_attacked() {
		if (!this.can_be_attacked()) {
			throw new IllegalActionException("${target_type} '${name}' [${id}] cannot be attacked (has stealth).")
		}
	}

	def check_enrage_status() {
		if (!this.is_a_minion() || this.is_dead()) {
			return
		}
		Card c = this as Card
		if (c.is_enraged && get_health() == get_max_health()) {
			c.is_enraged = false
			println "      . $this is not enraged anymore"
			new EnragedOff(c).check()
		}
		if (!c.is_enraged && get_health() < get_max_health()) {
			c.is_enraged = true
			println "      . $this is enraged"
			new EnragedOn(c).check()
		}
	}

	def deal_damage(int amount, Target t) {
		if (t == null || amount <= 0) {
			return
		}
		if (t.is_dead()) {
			println "      . $t receives no damage since it is dead"
			return
		}
		if (! t.has_buff(IMMUNE)) {
			if ( t.has_buff(DIVINE_SHIELD)) {
				t.remove_all_buff(DIVINE_SHIELD)
				return
			}
			new ItDealsDamage(this, t).check()
			if (this.is_a_minion()) {
				new AnyMinionDealsDamage(this, t).check()
			}
		}
		if (! t.is_dead()) {
			println "      . $this deals $amount damage to $t"
			t.receive_damage(amount)
		}
	}

	def deal_damage(int amount, List<Target> targets) {
		if (targets.size() == 0) {
			return
		}
		println "      . dealing $amount damage to $targets"
		targets.each{ Target t -> 
			deal_damage(amount, t) 
		}
	}
	
	def destroy( List<Target> targets ) {
		targets.each{ 
			destroy(it)
		}
	}

	def destroy(Target t) {
		if (t == null) {
			return
		}
		println "      . $this destroys $t"
		if (t.is_a_minion() || t.is_a_hero()) {
			t.dies()
		}
		else if (t.is_a_weapon()) {
			t.controller.hero.weapon.demolish()
		}
	}

	def dies() {
		if (this.is_destroyed) {
			return
		}
		println "      . $this dies..."
		def will_return = has_buff(RETURN_TO_BATTLEFIELD_WHEN_DESTROYED)

		this.is_destroyed = true
		new ItIsDestroyed(this).check()
		if (this.is_a_minion()) {
			new AnyMinionDies(this).check()
			if (!this.is_destroyed) { // saved somehow...
				return
			}
			leave_play()
			if (will_return) {
				game.summon(controller, this as Card)
			}
		}
	}

    static void freeze(Target t ) {
		t?.gains(FROZEN)
	}

	boolean is_frozen() {
		return this.has_buff(FROZEN)
	}
	
	int evaluate_attack(int a) {
		// check attack change/increase effects created by triggers
		def event = new AnyAttackIsEvaluated(this)
		event.check()
		def event_attack_increase = event.attack_increase
		def event_attack_change = event.attack_change
		if (event_attack_change != -1) {
			a = event_attack_change
			event_attack_increase = 0
		}
		// check attack change/increase effects created by buffs
		def buff_attack_increase = 0
		get_buffs().each {
			buff_attack_increase += it.attack_increase
			if (it.attack_change != -1) {
				a = it.attack_change
				buff_attack_increase = 0
				event_attack_increase = 0
			}
		}
		return a + buff_attack_increase + event_attack_increase
	}
	
	int get_attack() {
		return evaluate_attack(attack)
	}

	void set_attack(int new_value) {
		if (new_value < 0) {
			throw new InvalidDefinitionException("Invalid value for attack : $new_value")
		}
		println "      . setting attack for $this = $new_value"
		this.attack = new_value
	}

	int evaluate_health(int h) {
		def event = new AnyHealthIsEvaluated(this)
		event.check()
		def buff_health_increase = 0
		get_buffs().each {
			buff_health_increase += it.health_increase
			if (it.health_change != -1) {
				h = it.health_change
			}
		}
		return h + event.health_increase + buff_health_increase
	}
	
	int evaluate_max_health(int max_h) {
		def event = new AnyHealthIsEvaluated(this)
		event.check()
		def buff_health_increase = 0
		def event_health_increase = event.health_increase
		get_buffs().each {
			buff_health_increase += it.health_increase
			if (it.health_change != -1) {
				max_h = it.health_change // last applied is kept
			}
		}
		return max_h + event_health_increase + buff_health_increase
	}
	
	int get_health() {
		return evaluate_health(health)
	}
	
	int get_max_health() {
		return evaluate_max_health(max_health)
	}
	
	void set_max_health(int new_value) {
		println "      . setting max health for $this = $new_value"
		max_health = new_value
		if (health > max_health) {
			println "      . setting health for $this = $max_health"
			health = max_health
		}
		check_enrage_status()
	}

	def heal(int amount) {
		if (amount <= 0) {
			return
		}
		if (get_health() == get_max_health()) {
			return
		}
		new AnyCharacterIsHealed(this).check()
		add_health(amount)
	}
	
	boolean has_charge() {
		return has_buff(CHARGE)
	}

	boolean has_divine_shield() {
		return has_buff(DIVINE_SHIELD)
	}
	
	boolean has_stealth() {
		return has_buff(STEALTH)
	}

	boolean has_taunt() {
		return has_buff(TAUNT)
	}
	
	boolean is_a_demon() {
		if (this.is_a_hero()) {
			return (this as Hero).is_a_demon
		}
		else if (this.is_a_minion()) {
			return (this as Card).is_a_demon()
		}
		return false
	}

	boolean is_a_hero() {
		return (target_type == "hero")
	}

	boolean is_a_minion() {
		return (target_type == "minion")
	}

	boolean is_a_spell() {
		return (target_type == "spell")
	}

	boolean is_a_weapon() {
		return (target_type == "weapon")
	}

	boolean is_in_play() {
		if  (! this.is_a_minion()) {
			return false
		}
		if (this.controller == null) {
			return false
		}
		return (this as Card).is_in_play
	}

	boolean is_dead() {
		return get_health() <= 0 || is_destroyed
	}

	def leave_play() {
		println "      . $this leaves play"
		get_buffs().each { Buff b ->
			remove_buff(b)
		}
		triggers.clear()
		if (this.is_a_minion()) {
			(this as Card).init()
			this.controller.minions.remove(this as Card)
			this.controller.update_minions_place()
		} else if (this.is_a_spell()) { // secret
			this.controller.secrets.remove(this as Card)
		} else if (this.is_a_weapon()) {
			this.controller.hero.weapon = null
		}
	}

	def plus(List<Target> l) {
		return [this]+ l
	}

	def plus(Target t) {
		if (t == null) {
			return [this]
		}
		return [this, t]
	}

	def receive_damage(int amount) {
		if (amount <= 0) {
			return
		}
		if (this.has_buff(IMMUNE)) {
			println "      . $this takes no damage because it is immune"
			return // no damage taken
		}
		
		if (this.is_a_minion()) {
			new AnyMinionTakesDamage(this as Card).check()
		}
		if (this.is_a_hero()) {
			new AnyHeroTakesDamage(this as Hero, amount).check()
		}
		new ItTakesDamage(this).check()
		
		add_health( -amount )
	}
	
	int receive_combat_damage(int amount) {
		if (this.is_a_hero()) {
			// check if armor absorbs some damage
			Hero hero = this as Hero
			if (hero.armor >= amount) {	// armor > amount: reduce armor, no damage
				hero.armor -= amount
				println "      . $this armor absorbed all damage($amount), new armor=$hero.armor"
				amount = 0
			}
			else {
				if (hero.armor > 0 && hero.armor < amount) {
			// armor > 0 && < amount	// armor < amount: reduce amount, remove armor 
					amount -= hero.armor
					hero.armor = 0
					println "      . $this armor absorbed some damage, armor is destroyed"
				}
			}
		}
		this.health -= amount
		println "      . $this receives $amount combat damage, new health=${this.health}"
		return amount
	}

	def restore_health(int amount, Target target) {
		if (amount <= 0 || target == null) {
			return
		}
		
		// chance to modify the spell or power healing amount
		if (this.is_a_spell()) {
			def e2 = new AnySpellHealingIsEvaluated(controller, amount)
			e2.check()
			amount += e2.spell_healing_increase
		}
		else if (this.is_a_hero()) { // power used
			def e2 = new AnyPowerHealingIsEvaluated(controller, amount)
			e2.check()
			amount += e2.power_healing_increase
		}

		// chance to perform another action instead of healing
		boolean stop_action = false
		def e = new ItsControllerHeals(this.controller, this, target, amount)
		e.get_scope().each{ GameObject o ->
			o.check_event(e)
			if (e.stop_action) {
				stop_action = true
			}
		}
		if (stop_action) {
			println "      . healing action stopped"
			return
		}
			
		// normal healing
		println "      . $target is restored $amount health by $this"
		target.heal(amount)
		check_enrage_status()
	}

	def restore_health(int amount, List<Target> targets) {
		targets.each{ Target t -> restore_health(amount, t) }
	}

	def add_health(int amount) {
		if (get_health() <= 0) {
			this.dies() // too late
			return
		}
		println "      . adding $amount health for $this"
		if (get_health() + amount <= 0) {
			// if it would kill it, but 
			if (this.has_buff(CANNOT_BE_REDUCED_BELOW_1_HEALTH)) {
				// reduce damage amount
				amount = -(get_health() - 1)
				println "      . damage reduced to $amount because $this has '$CANNOT_BE_REDUCED_BELOW_1_HEALTH'"
			} else {
				health = 0
				this.dies()
				return
			}
		}
		if (get_health() + amount > get_max_health()) {
			amount = get_max_health() - get_health()
		}
		health += amount
		if (get_health() <= 0) {
			this.dies()
			return
		}
		println "      . new health for $this = ${get_health()}"
		check_enrage_status()
	}
	
	def set_health(int amount) {
		health = amount
		println "      . new health for $this = ${get_health()}"
		check_enrage_status()
	}

	def silence(Card c) {
		if (c == null) {
			return
		}
		println "      . $this silences $c"
		c.text = ''
		c.get_buffs().each { Buff b ->
			c.remove_buff(b)
		}
		c.triggers.clear()
	}

	String toString() {
		return "$target_type '$name'($id)"
	}

}
