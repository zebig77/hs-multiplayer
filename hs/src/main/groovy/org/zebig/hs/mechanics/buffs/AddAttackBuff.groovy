package org.zebig.hs.mechanics.buffs

import org.zebig.hs.game.Target

class AddAttackBuff extends Buff {

	static final BUFF_PATTERN = /\+(\d*)\sattack/

	AddAttackBuff(String buff_string, Target t) {
		super(buff_string, t)
		def matcher = buff_string =~ BUFF_PATTERN
		def attack_change = matcher[0][1].toInteger()
		this.attack_increase += attack_change
	}

}
