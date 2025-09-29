package org.zebig.hs.mechanics.events

import org.zebig.hs.game.Player

class AnySpellDamageIsEvaluated extends GlobalEvent {
	
	Player player
	int spell_damage_amount 	// base damage before increase
	int spell_damage_increase
	
	AnySpellDamageIsEvaluated(Player player, int amount) {
		super("player's spell damage is evaluated", player.game)
		this.player = player
		this.spell_damage_amount = amount
	}
	
}
