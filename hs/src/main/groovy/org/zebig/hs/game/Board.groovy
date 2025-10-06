package org.zebig.hs.game

import org.zebig.hs.logger.Log

import java.beans.PropertyChangeEvent

class Board {

	Player board_owner
	def cards = [] as ObservableList

    Board(Player owner) {
		this.board_owner = owner
        this.cards.addPropertyChangeListener {
            process_cards_change(it)
        }
	}

    void process_cards_change(PropertyChangeEvent event) {
        board_owner.game.transaction?.process_state_change(cards, event)
    }

	def add(Card c, int position) {
		assert c != null
		c.controller = board_owner
        if (size() >= 7) {
            throw new IllegalActionException("Board is full. Cannot add $c")
        }
		cards.add(position, c)
		Log.info "      . adding $c to ${board_owner}'s board"
	}

    // add to the right
    def add(Card c) {
        add(c, size())
    }

	boolean contains(Card c) {
		return (cards.contains(c))
	}

	def remove(Card c) {
		if (cards.contains(c)) {
			Log.info "      . $c is removed from ${board_owner}'s board"
			this.cards.remove(c)
		}
	}

	int size() {
		return cards.size()
	}

	String toString() {
		return "board of $board_owner.name : $cards"
	}
}
