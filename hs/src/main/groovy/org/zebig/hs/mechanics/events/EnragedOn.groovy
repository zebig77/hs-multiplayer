package org.zebig.hs.mechanics.events

import org.zebig.hs.game.Target

class EnragedOn extends LocalEvent {
	
	EnragedOn(Target origin) {
		super( "it has become enraged", origin )
	}

}
