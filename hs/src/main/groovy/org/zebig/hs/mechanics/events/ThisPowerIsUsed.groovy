package org.zebig.hs.mechanics.events

import org.zebig.hs.game.HeroPower

class ThisPowerIsUsed extends LocalEvent {
	
	ThisPowerIsUsed(HeroPower hero_power) {
		super( "this power is used", hero_power )
	}

}
