package org.zebig.hs.decks

import org.zebig.hs.game.Deck
import org.zebig.hs.game.Game

class AnduinDeck1 extends Deck {
	
	static definition = [
		'Power Word: Shield'	:2,
		'Northshire Cleric'		:2,
		'Holy Nova'				:2,
		'Mind Control'			:1,
		'Amani Berserker'		:2,
		'Leper Gnome'			:2,
		'Ironbeak Owl'			:2,
		'Acidic Swamp Ooze'		:2,
		'Wolfrider'				:2,
		'Azure Drake'			:2,
		'Novice Engineer'		:1,
		'Ironfur Grizzly'		:2,
		'Chillwind Yeti'		:2,
		'Gurubashi Berserker'	:2,
		'Lord of the Arena'		:2,
		'Stormwind Champion'	:2
		]
	
	AnduinDeck1(Game g) {
		super(g)
		build(definition)
	}

}
