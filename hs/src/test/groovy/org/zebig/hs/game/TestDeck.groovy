package org.zebig.hs.game;

import org.junit.Test

import org.zebig.hs.utils.TestHelper;
import org.zebig.hs.decks.MalfurionDeck1;

class TestDeck extends TestHelper {

	@Test
	void Deck_fail_wrong_card_name() {
		Deck d = new Deck(g)
		try { 
			d.build( [ 'nexistepas':1 ] )
			fail("ça aurait du planter because ça n'existe pas...")
		}
		catch( Exception e ) {
			println e // OK
		}
	}

	@Test
	void Deck_fail_not_deckable() {
		Deck d = new Deck(g)
		try {
			d.build( [ 'leokk':1 ] )
			fail("ça aurait du planter because il n'est pas deckable...")
		}
		catch( Exception e ) {
			println e // OK
		}
	}

	@Test
	void Deck_fail_not_30_cards() {
		Deck d = new Deck(g)
	try {
			d.build( [ 'Azure Drake':1 ] )
			fail("ça aurait du planter because y en a pas 30...")
		}
		catch( Exception e ) {
			println e // OK
		}
	}
		
	@Test
	void Deck_success() {
		Deck d = new Deck(g)
		d.build( MalfurionDeck1.definition ) // �a doit marcher
	}

}
