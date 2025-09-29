package org.zebig.hs.mechanics.events

import org.zebig.hs.game.Target

class AnyMinionIsPlayed extends GlobalEvent {
	
	Target target
	
	AnyMinionIsPlayed(Target minion) {
		super("a minion is played", minion.game)
		this.target = minion
	}
	
}
