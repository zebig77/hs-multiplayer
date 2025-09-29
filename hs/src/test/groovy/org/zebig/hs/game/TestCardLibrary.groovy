package org.zebig.hs.game

import org.zebig.hs.utils.TestHelper;

import static org.junit.Assert.*;

import org.junit.Test;

class TestCardLibrary extends TestHelper {

	@Test
	public void testLoad() {
		CardDefinition c = g.card_library.getCardDefinition('Execute')
		assert c.type == 'spell'
	}

}
