package org.zebig.hs.game

import java.beans.PropertyChangeEvent

abstract class CardZone {

    Game game
	def cards = [] as ObservableList

    CardZone(Game game) {
        this.game = game
        this.cards.addPropertyChangeListener {
            process_cards_change(it)
        }
    }

    abstract void process_cards_change(PropertyChangeEvent event)

	def add(Card c) {
		cards.add(0, c)
	}
	
	boolean isEmpty() {
		return cards.isEmpty()
	}
	
	int size() {
		return cards.size()
	}
}
