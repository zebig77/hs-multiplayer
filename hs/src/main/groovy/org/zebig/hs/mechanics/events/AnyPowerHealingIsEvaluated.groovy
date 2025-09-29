package org.zebig.hs.mechanics.events

import org.zebig.hs.game.Player

class AnyPowerHealingIsEvaluated extends GlobalEvent {
	
	Player player
	int power_healing_amount 	// base damage before increase
	int power_healing_increase
	
	AnyPowerHealingIsEvaluated(Player player, int amount) {
		super("player's power healing is evaluated", player.game)
		this.player = player
		this.power_healing_amount = amount
	}
	
}
