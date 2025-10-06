package org.zebig.hs.game

import java.beans.PropertyChangeEvent

abstract class CardZone {

    Game game
    Player owner
	def cards = [] as ObservableList

    CardZone(Player owner) {
        this.game = owner.game
        this.owner = owner
        this.cards.addPropertyChangeListener {
            process_cards_change(it)
        }
    }

    abstract void process_cards_change(PropertyChangeEvent event)
    abstract String toString()

	def add(Card c) {
		cards.add(0, c)
	}

    def add(int index, Card c) {
        cards.add(index, c)
    }

    boolean isEmpty() {
		return cards.isEmpty()
	}

    boolean contains(Card c) {
        return cards.contains(c)
    }
	
	int size() {
		return cards.size()
	}

    List<Card> cardsNamed(String name) {
        return (cards as List<Card>).findAll {it.name == name }
    }

    List<Card> minions() {
        return (cards as List<Card>).findAll {it.is_a_minion() }
    }
}
