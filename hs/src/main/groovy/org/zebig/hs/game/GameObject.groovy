package org.zebig.hs.game

import org.zebig.hs.logger.Log
import org.zebig.hs.mechanics.Trigger
import org.zebig.hs.mechanics.events.Event

import java.beans.PropertyChangeEvent

class GameObject extends ScriptObject {

	def triggers = [] as ObservableList

    GameObject(Game game) {
        super(game)
        triggers.addPropertyChangeListener {
            process_triggers_change(it)
        }
    }

    void process_triggers_change(PropertyChangeEvent event) {
        game.transaction?.process_state_change(triggers, event, this)
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
