package org.zebig.hs.game

import org.zebig.hs.logger.Log

import java.beans.PropertyChangeEvent

class Hand extends CardZone {

	Hand(Player owner) {
		super(owner)
        cards.addPropertyChangeListener {
            process_cards_change(it)
        }
    }

    @Override
    void process_cards_change(PropertyChangeEvent event) {
        owner.game.transaction?.process_state_change(cards, event)
    }

	def add(Card c) {
		assert c != null
		c.controller = owner
		if (size() >= 10) {
			Log.info "      . too much cards in hand, $c is discarded"
			return
		}
		super.add(c)
		Log.info "      . adding $c to ${owner}'s hand"
	}

	def discard_random(int n=1) {
		n.times{
			if (cards.size() > 0) {
                Card c = this.owner.game.random_pick(cards) as Card
				Log.info "   - discarding at random $c from ${this.owner}'s hand"
				cards.remove(c)
			}
		}
	}

	def remove(Card c) {
		if (cards.contains(c)) {
			Log.info "      . $c is removed from ${this.owner}'s hand"
			cards.remove(c)
		}
	}

    @Override
	String toString() {
		return "hand of $owner.name : $cards"
	}
}
