package org.zebig.hs.game

import org.zebig.hs.mechanics.Trigger
import org.zebig.hs.mechanics.buffs.Buff

class DefinitionObject extends ScriptObject {

	String name
	String text
	List<Trigger> triggers = []
	List<Closure> get_targets

    DefinitionObject(Game game) {
        super(game)
    }

	@Override
	Trigger add_trigger(Class event_class, Closure c) {
		Trigger t = new Trigger( event_class, c, this )
		triggers.add(t)
		return t
	}

	@Override
	Trigger add_trigger(Class event_class, Closure c, String comment) {
		Trigger t = new Trigger( event_class, c, this, comment )
		triggers.add(t)
		return t
	}
}
