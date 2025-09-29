package org.zebig.hs.mechanics.events

import org.zebig.hs.game.Game


class AnyTurnStarts extends GlobalEvent {
	
	AnyTurnStarts(Game game) {
		super("a turn starts", game)
	}
	
}
