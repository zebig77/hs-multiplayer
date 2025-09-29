package org.zebig.hs.mechanics.events

import org.zebig.hs.game.Game


class AnyTurnEnds extends GlobalEvent {
	
	AnyTurnEnds(Game game) {
		super("a turn ends", game)
	}
	
}
