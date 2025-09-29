package org.zebig.hs.mechanics.events

import org.zebig.hs.game.Target

class AnyMinionDies extends GlobalEvent {
	
	Target target
	
	AnyMinionDies(Target destroyed) {
		super("a minion is destroyed", destroyed.game)
		this.target = destroyed
	}
	
}
