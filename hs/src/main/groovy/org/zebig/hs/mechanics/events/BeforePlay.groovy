package org.zebig.hs.mechanics.events

import org.zebig.hs.game.Target

class BeforePlay extends LocalEvent {
	
	BeforePlay(Target origin) {
		super( "we check if it can be played", origin )
	}
	
}
