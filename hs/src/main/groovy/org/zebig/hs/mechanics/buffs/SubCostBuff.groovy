package org.zebig.hs.mechanics.buffs

import org.zebig.hs.game.Target

class SubCostBuff extends Buff {

	static final BUFF_PATTERN = /costs\s+\((\d+)\)\s+less/

	SubCostBuff(String buff_string, Target t) {
		super(buff_string, t)
		def matcher = buff_string =~ BUFF_PATTERN
		def cost_change = matcher[0][1].toInteger()
		this.cost_increase -= cost_change
	}

}
