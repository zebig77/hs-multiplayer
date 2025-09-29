package org.zebig.hs.game;

import org.junit.Test
import org.zebig.hs.utils.TestHelper

class TestCardDefinition extends TestHelper {

	@Test
	void CardDefinition_new() {
		CardDefinition cd = g.card_library.getCardDefinition("Abomination")
		assert cd != null
	}

}
