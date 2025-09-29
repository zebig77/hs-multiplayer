package org.zebig.hs.mechanics.events

import org.zebig.hs.game.Game
import org.zebig.hs.game.GameObject

abstract class Event {
	
	String name
	GameObject origin	// this_minion, this_spell, this_card, this_target...
    Game game

    Event( String name, Game game ) {
        this.name = name
        this.game = game
    }

    Event( String name, GameObject origin ) {
        this.name = name
        this.origin = origin
        this.game = origin.game
    }

    String toString() {
		return "$name"
	}
	
	/**
	 * 
	 * @return the list of targets that will receive this event
	 */
	abstract List<GameObject> get_scope()
	
	def check() {
		get_scope().each { it.check_event(this) }
		return this
	}

}