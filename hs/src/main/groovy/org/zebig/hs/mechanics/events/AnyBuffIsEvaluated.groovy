package org.zebig.hs.mechanics.events

import org.zebig.hs.game.Target
import org.zebig.hs.mechanics.buffs.BuffType

class AnyBuffIsEvaluated extends GlobalEvent {

	BuffType buff_type	
	Target target
	boolean has_buff = false
	boolean stop_action = false
	
	AnyBuffIsEvaluated(BuffType buff_type, Target target) {
		super("buff is evaluated", target.game)
		this.buff_type = buff_type
		this.target = target
	}
	
}
