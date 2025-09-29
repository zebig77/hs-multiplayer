package org.zebig.hs.decks

import org.zebig.hs.game.Deck
import org.zebig.hs.game.Game

class UtherDeck1 extends Deck {
	
	static definition = [
		'Blessing of Might'		:2,
		'Equality'				:2,
		'Divine Favor'			:2,
		'Consecration'			:2,
		'Hammer of Wrath'		:2,
		'Avenging Wrath'		:2,
		'Abusive Sergeant'		:2,
		'Argent Squire'			:2,
		'Elven Archer'			:2,
		'Leper Gnome'			:2,
		'Bluegill Warrior'		:2,
		'Ironbeak Owl'			:2,
		'Loot Hoarder'			:2,
		'Coldlight Oracle'		:2,
		'Wolfrider'				:1,
		'Leeroy Jenkins'		:1
		]
	
	UtherDeck1(Game g) {
		super(g)
		build(definition)
	}

}
