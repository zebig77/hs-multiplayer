package org.zebig.hs.mechanics.events

import org.zebig.hs.game.Target

class ItComesInPlay extends LocalEvent {
	
	ItComesInPlay(Target origin) {
		super( "it comes in play", origin )
	}

}
