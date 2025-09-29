package org.zebig.hs.mechanics.events

import org.zebig.hs.game.Target

class AnyCharacterAttacks extends GlobalEvent {
	
	Target attacker
	Target attacked
	Target changed_attacked = null
	
	AnyCharacterAttacks(Target attacker, Target attacked) {
		super( "a character attacks", attacker.game)
		this.attacker = attacker
		this.attacked = attacked
	}

}
