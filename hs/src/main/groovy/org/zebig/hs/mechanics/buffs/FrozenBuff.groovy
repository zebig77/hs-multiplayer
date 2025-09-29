package org.zebig.hs.mechanics.buffs

import org.zebig.hs.game.Target

class FrozenBuff extends Buff {
	
	static final BUFF_STRING = 'frozen'

	public FrozenBuff(Target t) {
		super(BuffType.FROZEN,t)
	}
	
}
