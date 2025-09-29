package org.zebig.hs.mechanics.events

import org.zebig.hs.game.Player
import org.zebig.hs.game.Target

class SpellTargetSelected extends GlobalEvent {
	
	Player player
	Target choice	// can be changed by effect (Spellbender)
	
	SpellTargetSelected(Player player, Target spell_target) {
		super("a spell target is selected", player.game)
		this.player = player
		this.choice = spell_target
	}
	
}
