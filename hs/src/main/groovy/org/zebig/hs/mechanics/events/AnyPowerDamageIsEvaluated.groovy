package org.zebig.hs.mechanics.events

import org.zebig.hs.game.Player

class AnyPowerDamageIsEvaluated extends GlobalEvent {
	
	Player player
	int power_damage_amount 	// base damage before increase
	int power_damage_increase
	
	AnyPowerDamageIsEvaluated(Player player, int amount) {
		super("Hero's damage power is evaluated", player.game)
		this.player = player
		this.power_damage_amount = amount
	}
	
}
