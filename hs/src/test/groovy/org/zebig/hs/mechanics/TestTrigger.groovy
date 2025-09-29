package org.zebig.hs.mechanics;

import org.zebig.hs.game.ScriptObject;
import org.zebig.hs.mechanics.buffs.BuffType
import org.zebig.hs.mechanics.events.ItComesInPlay

import org.junit.Test
import org.zebig.hs.utils.TestHelper

class TestTrigger extends TestHelper {

	@Test
	void Trigger_new() {
        def blu = _play("Bluegill Warrior")
		def c = { blu.this_minion.gains(BuffType.TAUNT) }
		def trigger = new Trigger( ItComesInPlay.class, c, blu )
		assert trigger.event_class == ItComesInPlay.class
		assert trigger.script == c
		assert trigger.container == blu
	}

}
