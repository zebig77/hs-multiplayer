package org.zebig.hs.game

import org.zebig.hs.logger.Log

import java.beans.PropertyChangeEvent

abstract class CardZone {

    Game game
    Player owner
	def cards = [] as ObservableList
    String name
    boolean visible_by_owner
    boolean visible_by_all

    CardZone(Player owner, String name, boolean visible_by_owner, boolean visible_by_all) {
        this.game = owner.game
        this.owner = owner
        this.name = name
        this.visible_by_owner = visible_by_owner
        this.visible_by_all = visible_by_all
        this.cards.addPropertyChangeListener {
            process_cards_change(it)
        }
    }

    abstract void process_cards_change(PropertyChangeEvent event)

    String toString() {
        return "$name of $owner"
    }

    Card get(int index) {
        return cards.get(index) as Card
    }

	void add(Card c) {
		cards.add(0, c)
	}

    void add(int index, Card c) {
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

    void clear() {
        cards.clear()
    }

    void remove(Card c) {
        if (cards.contains(c)) {
            Log.info "      . $c is removed from ${owner}'s $name"
            cards.remove(c)
        }
    }
}
