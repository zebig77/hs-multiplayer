package org.zebig.hs.mechanics.events

import org.zebig.hs.game.Player

class ItsControllerTurnStarts extends PlayerEvent {
	
	ItsControllerTurnStarts(Player p) {
		super( "its controller turn starts", p )
	}

}
