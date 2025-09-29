package org.zebig.hs.mechanics.events

import org.zebig.hs.game.HeroPower

class BeforeUsePower extends LocalEvent {
	
	BeforeUsePower(HeroPower hp) {
		super( "we check if power can be used", hp )
	}
	
}
