package org.zebig.hs.game

import org.zebig.hs.logger.Log
import org.zebig.hs.mechanics.Trigger
import org.zebig.hs.mechanics.buffs.BuffType
import org.zebig.hs.mechanics.events.Event
import org.zebig.hs.state.ListState

class GameObject extends ScriptObject {

	ListState<Trigger> triggers

    GameObject(Game game) {
        super(game)
        this.triggers = new ListState<Trigger>(game)
    }

	/*
	 * check if the target has triggers the event type of e
	 */
    def check_event = { Event e ->

        e.origin = this

        game.events.push(e)

        List<Trigger> todo_triggers = triggers.findAll { (it as Trigger).event_class == e.class }
        todo_triggers.each { Trigger t ->
            Log.info "      . executing $t for ${this} because $e"
            t.script.call()
            if (t.last_call()) {
                Log.info "      . removing $t because it was called for the last time"
                this.remove_trigger(t)
            }
        }

        game.events.pop()
    }
	
	def remove_trigger(Trigger t) {
		triggers.remove(t)
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
