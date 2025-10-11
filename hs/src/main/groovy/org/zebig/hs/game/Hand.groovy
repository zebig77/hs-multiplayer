package org.zebig.hs.game

import org.zebig.hs.logger.Log

import java.beans.PropertyChangeEvent

class Hand extends CardZone {

	Hand(Player owner) {
		super(owner, "hand", true, false)
        cards.addPropertyChangeListener {
            process_cards_change(it)
        }
    }

    @Override
    void process_cards_change(PropertyChangeEvent event) {
        owner.game.transaction?.process_state_change(cards, event)
    }

    @Override
	void add(Card c) {
		assert c != null
		c.controller = owner
		if (size() >= 10) {
			Log.info "      . too much cards in hand, $c is discarded"
			return
		}
		super.add(c)
		Log.info "      . adding $c to ${owner}'s hand"
	}

	void discard_random(int n=1) {
		n.times{
			if (cards.size() > 0) {
                Card c = this.owner.game.random_pick(cards) as Card
				Log.info "   - discarding at random $c from ${this.owner}'s hand"
				super.remove(c)
			}
		}
	}

}
