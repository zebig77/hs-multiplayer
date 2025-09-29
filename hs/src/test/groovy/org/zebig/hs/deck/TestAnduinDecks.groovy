package org.zebig.hs.deck;

import org.junit.Test

import org.zebig.hs.utils.TestHelper
import org.zebig.hs.decks.AnduinDeck1

class TestAnduinDecks extends TestHelper {

	@Test
	public void testAnduinDeck1() {
		def deck1 = new AnduinDeck1(g)
		assert deck1.size() == 30
		assert deck1.cards.findAll{ it.name == "Northshire Cleric" }.size() == 2
	}

}
