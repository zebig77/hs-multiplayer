package org.zebig.hs.decks

import org.zebig.hs.game.Deck
import org.zebig.hs.game.Game

class JainaDeck1 extends Deck {
	
	static definition = [
		'Faerie Dragon'		:2,
		'Amani Berserker'	:2,
		'Arcane Explosion'	:2,
		'Leper Gnome'		:2,
		'Arcane Intellect'	:2,
		'Ice Lance'			:2,
		'Frostbolt'			:2,
		'Ironbeak Owl'		:2,
		'Acidic Swamp Ooze'	:2,
		'Wolfrider'			:2,
		'Thrallmar Farseer'	:2,
		'Azure Drake'		:2,
		'Flamestrike'		:2,
		'Fireball'			:2,
		'Frost Elemental'	:1,
		'Archmage'			:1
		]
	
	JainaDeck1(Game g) {
		super(g)
		build(definition)
	}

}
