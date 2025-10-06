package org.zebig.hs.game

import org.zebig.hs.logger.Log

import java.beans.PropertyChangeEvent

class Board extends CardZone {

    Board(Player owner) {
		super(owner)
        this.cards.addPropertyChangeListener {
            process_cards_change(it)
        }
	}

    void process_cards_change(PropertyChangeEvent event) {
        owner.game.transaction?.process_state_change(cards, event)
    }

	def add(Card c, int position) {
		assert c != null
		c.controller = owner
        if (size() >= 7) {
            throw new IllegalActionException("Board is full. Cannot add $c")
        }
		cards.add(position, c)
		Log.info "      . adding $c to ${owner}'s board"
	}

    // add to the right
    def add(Card c) {
        add(c, size())
    }

	def remove(Card c) {
		if (cards.contains(c)) {
			Log.info "      . $c is removed from ${owner}'s board"
			cards.remove(c)
		}
	}

    @Override
	String toString() {
		return "board of $owner.name : $cards"
	}
}
