package org.zebig.hs.mechanics.events

import org.zebig.hs.game.Target

class ItIsAttacked extends LocalEvent {
	
	ItIsAttacked(Target origin) {
		super( "it is attacked", origin )
	}

}
