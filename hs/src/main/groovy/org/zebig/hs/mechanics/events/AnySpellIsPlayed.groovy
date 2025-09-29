package org.zebig.hs.mechanics.events

import org.zebig.hs.game.Target

class AnySpellIsPlayed extends GlobalEvent {
	
	Target target
	boolean stop_action = false
	
	AnySpellIsPlayed(Target spell) {
		super("a spell is played", spell.game)
		this.target = spell
	}
	
}
