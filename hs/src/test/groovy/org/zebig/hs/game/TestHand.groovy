package org.zebig.hs.game;

import static org.junit.Assert.*;

import org.junit.Test;

import org.zebig.hs.utils.TestHelper;

class TestHand extends TestHelper {

	@Test
	public void Hand_too_much_cards_in_hand() {
		while( p1.hand.size() < 10 ) {
			p1.draw(1)
		}
		p1.draw(1)
		assert p1.hand.size() == 10
	}

}
