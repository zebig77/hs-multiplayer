package org.zebig.hs.mechanics.events

import org.zebig.hs.game.Card
import org.zebig.hs.game.Player

class ItsControllerPlaysACard extends PlayerEvent {
	
	Card target
	
	ItsControllerPlaysACard(Player p, Card played_card) {
		super( "its controller plays a card", p )
		this.target = played_card
	}
	
}
