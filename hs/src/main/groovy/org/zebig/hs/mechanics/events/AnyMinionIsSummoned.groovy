package org.zebig.hs.mechanics.events

import org.zebig.hs.game.Target

class AnyMinionIsSummoned extends GlobalEvent {
	
	Target target
	
	AnyMinionIsSummoned(Target minion) {
		super("a minion is summoned", minion.game)
		this.target = minion
	}
	
}
