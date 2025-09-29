package org.zebig.hs.mechanics.events

import org.zebig.hs.game.GameObject
import org.zebig.hs.game.Target

class AnyMinionDealsDamage extends GlobalEvent {
	
	GameObject origin
	Target target
	
	AnyMinionDealsDamage(GameObject origin, Target damaged_target) {
		super( "a minion deals damage", origin.game)
		this.target = damaged_target
	}

}
