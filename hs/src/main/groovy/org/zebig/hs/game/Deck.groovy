package org.zebig.hs.game

import java.beans.PropertyChangeEvent

class Deck extends CardZone {

    Deck(Game game) {
        super(game)
        this.cards.addPropertyChangeListener {
            process_cards_change(it)
        }
    }

    void process_cards_change(PropertyChangeEvent event) {
        game.transaction?.process_state_change(cards, event)
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
		return (cards as List<Card>).remove(0)
	}
	
}
