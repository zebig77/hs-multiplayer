package org.zebig.hs.mechanics.buffs

import org.zebig.hs.game.Card
import org.zebig.hs.game.Target

class ChangeAttackBuff extends Buff {
	
	static final BUFF_PATTERN = /change attack to\s*(\d+)\s*/
	
	ChangeAttackBuff(String buff_string, Target t) {
		super(buff_string, t)
		def matcher = buff_string =~ BUFF_PATTERN
		def new_attack = matcher[0][1].toInteger()
		this.attack_change = new_attack
	}
	
	def remove_effect() {
		Object.remove_effect()
		if (target.is_a_minion()) {
			println "      . original attack is restored for $target"
			target.attack = (target as Card).card_definition.attack
		}
	}
}
