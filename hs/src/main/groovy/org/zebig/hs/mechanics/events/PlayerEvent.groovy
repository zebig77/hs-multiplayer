package org.zebig.hs.mechanics.events

import org.zebig.hs.game.GameObject
import org.zebig.hs.game.Player

abstract class PlayerEvent extends Event {
	
	Player p
	
	PlayerEvent(String name, Player p) {
		super(name, p.game)
		this.p = p
	}
	
	@Override
	List<GameObject> get_scope() {
		return p.hero + p.minions() + p.artefact
	}
	
	def check() {
		get_scope().each { GameObject o ->
			o.check_event(this) 
		}
	}
	
}
