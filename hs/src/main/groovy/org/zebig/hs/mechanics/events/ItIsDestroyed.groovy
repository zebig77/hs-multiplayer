package org.zebig.hs.mechanics.events

import org.zebig.hs.game.Target

class ItIsDestroyed extends LocalEvent {
	
	ItIsDestroyed(Target origin) {
		super( "it is destroyed", origin )
	}

}
