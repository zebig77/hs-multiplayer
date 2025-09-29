package org.zebig.hs.decks

import org.zebig.hs.game.Deck
import org.zebig.hs.game.Game

class GarroshDeck1 extends Deck {
	
	static definition = [
		'inner rage'		:1,
		'upgrade !'			:1,
		'execute'			:2,
		'whirlwind'			:2,
		'fiery war axe'		:2,
		'heroic strike'		:2,
		'shield slam'		:1,
		'acidic swamp ooze'	:1,
		'acolyte of pain'	:1,
		'Frothing Berserker':2,
		'tauren warrior'	:1,
		'Thrallmar Farseer'	:1,
		'Arathi Weaponsmith':2,
		'cult master'		:2,
		"Kor'kron Elite"	:2,
		'arcanite reaper'	:2,
		'gurubashi berserker':2,
		'stormpike commando':1,
		'frost elemental'	:1,
		'Stormwind Champion':1
		]
	
	GarroshDeck1(Game g) {
		super(g)
		build(definition)
	}

}
