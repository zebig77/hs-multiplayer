package org.zebig.hs.deck;

import org.junit.Test

import org.zebig.hs.utils.TestHelper;
import org.zebig.hs.decks.MalfurionDeck1

class TestMalfurionDecks extends TestHelper {

	@Test
	public void TestMalfurionDecks_deck1() {
		def deck1 = new MalfurionDeck1(g)
		assert deck1.size() == 30
		assert deck1.cards.findAll{ it.name == "Swipe" }.size() == 2
	}

}
