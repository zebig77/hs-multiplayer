package org.zebig.hs.game

import org.zebig.hs.state.ListState

class Deck {

    Game game
	ListState<Card> cards

    Deck(Game game) {
        this.game = game
        this.cards = new ListState<Card>(game)
    }
	
	def build(Map<String, Integer> definition) {
		cards.clear()
		definition.each{ String card_name, int count ->
			count.times{ 
				def cd = game.card_library.getCardDefinition(card_name)
				if (!cd.collectible) {
					throw new InvalidDeckException("$card_name ne peut pas être mis dans un deck")
				}
				Card c = new Card(game.card_library.getCardDefinition(card_name))
				add(c)
			}
		}
		if (size() != 30) {
			throw new InvalidDeckException("La définition du deck contient ${size()} cartes au lieu de 30")
		}
	}
	
	Card draw() {
		if (cards.isEmpty())
			return null
		return cards.remove(0)
	}
	
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
