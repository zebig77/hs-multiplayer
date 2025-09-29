package org.zebig.hs.mechanics.events

import org.zebig.hs.game.Target

class EnragedOff extends LocalEvent {
	
	EnragedOff(Target origin) {
		super( "it is not enraged anymore", origin )
	}

}
