package org.zebig.hs.mechanics.events

import org.zebig.hs.game.Hero

class AnyHeroTakesDamage extends GlobalEvent {
	
	Hero target
	int amount
	
	AnyHeroTakesDamage(Hero target, int amount) {
		super("a hero takes damage", target.game)
		this.target = target
		this.amount = amount
	}
	
}
