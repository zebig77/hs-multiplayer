package org.zebig.hs.decks

import org.zebig.hs.game.Deck
import org.zebig.hs.game.Game

class MalfurionDeck1 extends Deck {
	
	static definition = [
		'Innervate'			:2,
		'Naturalize'		:2,
		'Claw'				:2,
		'Leper Gnome'		:1,
		'Wrath'				:2,
		'Mark of the Wild'	:1,
		'Power of the Wild'	:2,
		'Ironbeak Owl'		:1,
		'Acidic Swamp Ooze'	:2,
		'Healing Touch'		:1,
		'Wolfrider'			:1,
		'Thrallmar Farseer'	:2,
		'Swipe'				:2,
		'Nourish'			:1,
		'Azure Drake'		:2,
		'Druid of the Claw'	:2,
		'Starfire'			:2,
		'Force of Nature'	:1,
		'Lord of the Arena'	:1
		]
	
	MalfurionDeck1(Game g) {
		super(g)
		build(definition)
	}

}
