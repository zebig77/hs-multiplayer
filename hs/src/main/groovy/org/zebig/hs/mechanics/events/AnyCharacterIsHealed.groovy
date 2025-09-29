package org.zebig.hs.mechanics.events

import org.zebig.hs.game.Target

class AnyCharacterIsHealed extends GlobalEvent {
	
	Target target
	
	AnyCharacterIsHealed(Target target) {
		super("a character is healed", target.game)
		this.target = target
	}
	
}
