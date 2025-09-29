package org.zebig.hs.mechanics.events

import org.zebig.hs.game.Target

class ItIsPlayed extends LocalEvent {
	
	ItIsPlayed(Target origin) {
		super( "it is played", origin )
	}

}
