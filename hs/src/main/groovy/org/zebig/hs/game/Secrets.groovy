package org.zebig.hs.game

import org.zebig.hs.logger.Log

import java.beans.PropertyChangeEvent

class Secrets extends CardZone {

    Secrets(Player owner) {
		super(owner, "secrets", true, false)
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
            throw new IllegalActionException("Secrets are full. Cannot add $c")
        }
		super.add(position, c)
		Log.info "      . adding $c to ${owner}'s secrets"
	}

    // add to the right
    void add(Card c) {
        add(c, size())
    }

}
