package org.zebig.hs.mechanics.events

import org.zebig.hs.game.Card
import org.zebig.hs.game.Player

class BeforeItsControllerPlaysACard extends PlayerEvent {
	
	Card target
	
	BeforeItsControllerPlaysACard(Player p, Card played_card) {
		super( "its controller plays a card", p )
		this.target = played_card
	}
	
}
