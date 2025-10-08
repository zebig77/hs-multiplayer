package org.zebig.hs.game

import org.zebig.hs.logger.Log

import java.beans.PropertyChangeEvent

class Board extends CardZone {

    Board(Player owner) {
		super(owner, "board", true, true)
        this.cards.addPropertyChangeListener {
            process_cards_change(it)
        }
	}

    void process_cards_change(PropertyChangeEvent event) {
        owner.game.transaction?.process_state_change(cards, event)
    }

	void add(Card c, int position) {
		assert c != null
		c.controller = owner
        if (size() >= 7) {
            throw new IllegalActionException("Board is full. Cannot add $c")
        }
		super.add(position, c)
		Log.info "      . adding $c to ${owner}'s board"
	}

    // add to the right
    void add(Card c) {
        add(c, size())
    }

	void remove(Card c) {
		if (cards.contains(c)) {
			Log.info "      . $c is removed from ${owner}'s board"
			cards.remove(c)
		}
	}

}
