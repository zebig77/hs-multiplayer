package org.zebig.hs.deck;

import org.junit.Test

import org.zebig.hs.utils.TestHelper;
import org.zebig.hs.decks.ValeeraDeck1

class TestValeeraDecks extends TestHelper {

	@Test
	public void testValeeraDeck1() {
		def deck1 = new ValeeraDeck1(p1)
		assert deck1.size() == 30
		assert deck1.cardsNamed("Molten Giant").size() == 2
	}

}
