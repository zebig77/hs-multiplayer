package org.zebig.hs.mechanics.events

import org.zebig.hs.game.Target

class ItAttacks extends LocalEvent {
	
	ItAttacks(Target origin) {
		super( "it attacks", origin )
	}

}
