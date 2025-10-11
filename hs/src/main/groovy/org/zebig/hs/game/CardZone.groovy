package org.zebig.hs.game

import org.zebig.hs.logger.Log
import org.zebig.hs.state.GameChange

import java.beans.PropertyChangeEvent

import static org.zebig.hs.state.GameChange.Type.*

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
		this.add(0, c)
	}

    void add(int index, Card c) {
        cards.add(index, c)
        game.transaction?.record(ZoneSizeChange, owner.name, [zone_name:name, new_size:size()])
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
        game.transaction?.record(ZoneSizeChange, owner.name, [zone_name:name, new_size:size()])
    }

    Card remove(Card c) {
        if (cards.contains(c)) {
            Log.info "      . $c is removed from ${owner}'s $name"
            cards.remove(c)
            game.transaction?.record(ZoneSizeChange, owner.name, [zone_name:name, new_size:size()])
            return c
        }
        return null
    }

    Card remove(int index) {
        if (index < cards.size()) {
            return remove(cards.get(index) as Card)
        }
        return null
    }
}
