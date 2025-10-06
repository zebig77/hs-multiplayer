package org.zebig.hs.deck;


import org.junit.Test

import org.zebig.hs.utils.TestHelper
import org.zebig.hs.decks.JainaDeck1

class TestJainaDecks extends TestHelper {

	@Test
	void JainaDecks_deck1() {
		def deck1 = new JainaDeck1(p1)
		assert deck1.size() == 30
		assert deck1.cardsNamed("Ice Lance").size() == 2
	}

}
