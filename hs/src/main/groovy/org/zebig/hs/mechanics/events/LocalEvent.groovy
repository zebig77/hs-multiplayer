package org.zebig.hs.mechanics.events

import org.zebig.hs.game.GameObject

abstract class LocalEvent extends Event {
	
	LocalEvent(String name, GameObject origin) {
		super(name, origin)
	}
	
	@Override
	List<GameObject> get_scope() {
		return [ origin ]
	}

}
