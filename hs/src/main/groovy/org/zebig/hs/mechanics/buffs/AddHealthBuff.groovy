package org.zebig.hs.mechanics.buffs

import org.zebig.hs.game.Target

class AddHealthBuff extends Buff {

	static final BUFF_PATTERN = /\+(\d*)\shealth/

	AddHealthBuff(String buff_string, Target t) {
		super(buff_string, t)
		def matcher = buff_string =~ BUFF_PATTERN
		def health_change = matcher[0][1].toInteger()
		this.health_increase = health_change
	}

}
