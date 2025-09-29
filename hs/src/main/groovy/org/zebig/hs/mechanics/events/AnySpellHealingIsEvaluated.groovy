package org.zebig.hs.mechanics.events

import org.zebig.hs.game.Player

class AnySpellHealingIsEvaluated extends GlobalEvent {
	
	Player player
	int spell_healing_amount 	// base damage before increase
	int spell_healing_increase
	
	AnySpellHealingIsEvaluated(Player player, int amount) {
		super("player's spell healing is evaluated", player.game)
		this.player = player
		this.spell_healing_amount = amount
	}
	
}
