package org.zebig.hs.mechanics.events

import org.zebig.hs.game.Target

class AnySecretIsRevealed extends GlobalEvent {
	
	Target target
	
	AnySecretIsRevealed(Target revealed) {
		super("a secret is revealed", revealed.game)
		this.target = revealed
	}
	
}
