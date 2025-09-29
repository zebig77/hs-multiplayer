package org.zebig.hs.mechanics.buffs

import org.zebig.hs.game.Card
import org.zebig.hs.game.Target

class ChangeHealthBuff extends Buff {
	
	static final BUFF_PATTERN = /change health to\s*(\d+)\s*/
	
	/**
	 * change health to x
	 * This buff changes the current health to x, and max_health to x (see equality).
	 * @param buff_string
	 * @param t
	 */
	ChangeHealthBuff(String buff_string, Target t) {
		super(buff_string, t)
		def matcher = buff_string =~ BUFF_PATTERN
		this.health_change = matcher[0][1].toInteger()
	}
	
	def remove_effect() {
		super.remove_effect()
		if (target.is_a_minion()) {
			println "      . original health is restored for $target"
			target.health = (target as Card).card_definition.max_health
			target.max_health = (target as Card).card_definition.max_health
			(target as Card).is_enraged = false
		}
	}
}
