package org.zebig.hs.mechanics.events

import org.zebig.hs.game.Target

class ItTakesDamage extends LocalEvent {
	
	ItTakesDamage(Target origin) {
		super( "it takes damage", origin )
	}

}
