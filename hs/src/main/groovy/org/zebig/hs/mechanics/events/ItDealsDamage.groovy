package org.zebig.hs.mechanics.events

import org.zebig.hs.game.Target

class ItDealsDamage extends LocalEvent {
	
	Target target
	
	ItDealsDamage(Target origin, Target damaged_target) {
		super( "it deals damage", origin )
		this.target = damaged_target
	}

}
