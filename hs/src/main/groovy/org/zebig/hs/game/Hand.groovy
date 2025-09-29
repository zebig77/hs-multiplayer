package org.zebig.hs.game

import org.zebig.hs.logger.Log
import org.zebig.hs.state.ListState

class Hand {

	Player hand_owner
	ListState<Card> cards

	Hand(Player hand_owner) {
		this.hand_owner = hand_owner
        this.cards = new ListState<Card>(hand_owner.game)
	}

	def add(Card c) {
		assert c != null
		c.controller = hand_owner
		if (size() >= 10) {
			Log.info "      . too much cards in hand, $c is discarded"
			return
		}
		cards.add(c)
		Log.info "      . adding $c to ${hand_owner}'s hand"
	}

	boolean contains(Card c) {
		return (cards.contains(c))
	}

	def discard_random(int n=1) {
		n.times{
			if (cards.size() > 0) {
                Card c = hand_owner.game.random_pick(cards.storage) as Card
				Log.info "   - discarding at random $c from ${hand_owner}'s hand"
				cards.remove(c)
			}
		}
	}

	def remove(Card c) {
		if (cards.contains(c)) {
			Log.info "      . $c is removed from ${hand_owner}'s hand"
			this.cards.remove(c)
		}
	}

	int size() {
		return cards.size()
	}

	String toString() {
		return "hand of $hand_owner.name : $cards"
	}
}
