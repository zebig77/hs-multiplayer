package org.zebig.hs.mechanics.events

import org.zebig.hs.game.Player

class ItsControllerTurnEnds extends PlayerEvent {
	
	ItsControllerTurnEnds(Player p) {
		super( "its controller turn ends", p )
	}

}
