package org.zebig.hs.decks

import org.zebig.hs.game.Deck
import org.zebig.hs.game.Game

class ValeeraDeck1 extends Deck {
	
	static definition = [
		'Backstab'				:2,
		'Deadly Poison'			:2,
		'Eviscerate'			:2,
		'Blade Flurry'			:2,
		'SI:7 Agent'			:2,
		"Anub'ar Ambusher"		:2,
		'Assassinate'			:1,
		"Assassin's Blade"		:1,
		'Novice Engineer'		:2,
		'Sunfury Protector'		:2,
		'Coldlight Oracle'		:2,
		'Faceless Manipulator'	:1,
		'Azure Drake'			:2,
		'Loatheb'				:1,
		'The Black Knight'		:1,
		'Alexstrasza'			:1,
		'Mountain Giant'		:2,
		'Molten Giant'			:2
		]
	
	ValeeraDeck1(Game g) {
		super(g)
		build(definition)
	}

}
