package org.zebig.hs.mechanics.buffs

import org.zebig.hs.game.InvalidDefinitionException
import org.zebig.hs.game.ScriptObject
import org.zebig.hs.game.Target
import org.zebig.hs.logger.Log
import org.zebig.hs.mechanics.Trigger
import org.zebig.hs.state.ListState

class Buff extends ScriptObject {
	
	final BuffType buff_type
	Target target
	
	int attack_increase = 0
	int attack_change = -1
	int cost_increase = 0
	int health_increase = 0
	int health_change = -1
	int spell_damage_increase = 0
	
	ListState<Trigger> triggers

	Buff(BuffType buff_type, Target target) {
        super(target.game)
		this.buff_type = buff_type
		this.target = target
        this.triggers = new ListState<Trigger>(target.game)
        Log.info "      . added '$this' buff to '$target'"
	}
	
	Buff(String buff_string, Target target) {
		this(BuffType.getInstance(buff_string), target)
	}

	def remove_effect() {
		Log.info "      . '$this' buff is removed from $target"
	}

	String toString() {
		return buff_type.name
	}

	def until_end_of_turn() {
		this.target.when_its_controller_turn_ends {
			this.target.remove_buff(this)
		}.run_once()
	}
	
	def until_your_next_turn() {
		this.target.when_its_controller_turn_starts {
			this.target.remove_buff(this)
		}.run_once()
	}
	
	/* for complex buffs like '+2 attack', '+3/+3', etc. */
	static Buff create_buff(String buff_string, Target t) {
		
		def bs = buff_string.trim().toLowerCase()
		
		if (bs =~ AddAttackBuff.BUFF_PATTERN) { // +x attack
			return new AddAttackBuff(bs,t)
		}

		if (bs =~ AddHealthBuff.BUFF_PATTERN) { // +x health
			return new AddHealthBuff(bs,t)
		}

		if (bs =~ ChangeAttackBuff.BUFF_PATTERN) { // /change attack to x
			return new ChangeAttackBuff(bs,t)
		}
		
		if (bs =~ ChangeHealthBuff.BUFF_PATTERN) { // /change health to x
			return new ChangeHealthBuff(bs,t)
		}
		
		if (bs =~ AddAttackHealthBuff.BUFF_PATTERN) { // +x/+x
			return new AddAttackHealthBuff(bs,t)
		}
		
		if (bs =~ AddSpellDamageBuff.BUFF_PATTERN) { // +x Spell Damage
			return new AddSpellDamageBuff(bs,t)
		}
		
		if (bs =~ AddCostBuff.BUFF_PATTERN) { // cost (x) more
			return new AddCostBuff(bs,t)
		}
		
		if (bs =~ SubCostBuff.BUFF_PATTERN) { // cost (x) less
			return new SubCostBuff(bs,t)
		}
		
		throw new InvalidDefinitionException( "buff non reconnu: $buff_string ($bs)" )		
	}

	static Buff create_buff(BuffType bt, Target t) {
		
		if (bt in BuffType.STATELESS_BUFFS) {
			def found = t.buffs.find{it.buff_type == bt}
			if (found != null) {
				// no need to add one
				return found
			}
			return new Buff(bt,t)
		}

		if (bt == BuffType.FROZEN) {
			return new FrozenBuff(t)
		}

		throw new InvalidDefinitionException( "Unknown buff: $bt" )
	}

	@Override
	Trigger add_trigger(Class event_class, Closure c) {
		Trigger t = new Trigger( event_class, c, this )
		triggers.add(t)
		return t
	}

	@Override
	Trigger add_trigger(Class event_class, Closure c, String comment) {
		Trigger t = new Trigger( event_class, c, this, comment )
		triggers.add(t)
		return t
	}

}
