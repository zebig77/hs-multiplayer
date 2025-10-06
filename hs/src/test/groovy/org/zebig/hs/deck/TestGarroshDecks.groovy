

package org.zebig.hs.deck;

import org.junit.Test

import org.zebig.hs.utils.TestHelper
import org.zebig.hs.decks.GarroshDeck1

class TestGarroshDecks extends TestHelper {

	@Test
	void testGarroshDeck1() {
		def deck1 = new GarroshDeck1(p1)
		assert deck1.size() == 30
		assert deck1.cardsNamed("Execute").size() == 2
	}

}
