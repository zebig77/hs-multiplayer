package org.zebig.hs.mechanics.events

import org.zebig.hs.game.Card

class AnyMinionTakesDamage extends GlobalEvent {
	
	Card target
	
	AnyMinionTakesDamage(Card target) {
		super("a minion takes damage", target.game)
		this.target = target
	}
	
}
