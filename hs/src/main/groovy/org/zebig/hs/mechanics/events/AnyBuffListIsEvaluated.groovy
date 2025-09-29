package org.zebig.hs.mechanics.events

import org.zebig.hs.game.Target
import org.zebig.hs.mechanics.buffs.Buff

class AnyBuffListIsEvaluated extends GlobalEvent {
	
	Target target
	List<Buff> additional_buffs = []
	
	AnyBuffListIsEvaluated(Target origin) {
		super( "its buff list is evaluated", origin.game )
		this.target = origin
	}

}
