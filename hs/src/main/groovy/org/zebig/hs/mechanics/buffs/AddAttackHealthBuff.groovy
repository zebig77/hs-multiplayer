package org.zebig.hs.mechanics.buffs

import org.zebig.hs.game.Target

class AddAttackHealthBuff extends Buff {

	static slash = '/'
	static final BUFF_PATTERN = /\+(\d*)${slash}\+(\d*)/
	
	AddAttackHealthBuff(String buff_string, Target t) {
		super(buff_string, t)
		def matcher = buff_string =~ BUFF_PATTERN
		this.attack_increase = matcher[0][1].toInteger()
		this.health_increase = matcher[0][2].toInteger()
	}

}
