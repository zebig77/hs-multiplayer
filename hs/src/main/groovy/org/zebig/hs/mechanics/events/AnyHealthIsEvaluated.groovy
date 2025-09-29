package org.zebig.hs.mechanics.events

import org.zebig.hs.game.Target

class AnyHealthIsEvaluated extends GlobalEvent {
	
	int health_increase
	Target target
	
	AnyHealthIsEvaluated(Target target) {
		super("health is evaluated", target.game)
		this.target = target
	}
	
}
