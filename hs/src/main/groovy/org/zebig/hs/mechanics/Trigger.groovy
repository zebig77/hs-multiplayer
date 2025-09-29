package org.zebig.hs.mechanics

import org.zebig.hs.game.ScriptObject
import org.zebig.hs.state.MapState


class Trigger {

	final Class event_class
	final Closure script
	final ScriptObject container
	final MapState ps
	String comment
	
	Trigger(Class event_class, Closure script, container ) {
		this.event_class = event_class
		this.script = script
		this.container = container
		this.ps = new MapState()
		this.count_down = 0
	}
	
	Trigger(Class event_class, Closure script, container, comment ) {
		this(event_class, script, container)
		this.comment = comment
	}
	
	int getCount_down() { ps.count_down }
	void setCount_down(int count_down) {	ps.count_down = count_down }

	Closure getEnd_condition() { ps.end_condition }
	void setEnd_condition(Closure end_condition) { ps.end_condition = end_condition }

	boolean last_call() {
		// check if trigger should be removed
		if (end_condition != null) {
			return end_condition.call()
		}
		if (count_down == 0) {
			return false // no limit
		}
		count_down--
		return (count_down == 0)
	}
	
	String toString() {
		if (comment == null) {
			return "${event_class.name}"
		}
		return "'$comment'"
	}
	
	def run_once() {
		count_down = 1
	}
	
	def until_end_of_turn() {
		this.container.when_its_controller_turn_ends('remove trigger') {
			this.container.remove_trigger(this)
		}.run_once()
	}
	
	def until_your_next_turn() {
		this.container.when_its_controller_turn_starts("remove this trigger") {
			this.container.remove_trigger(this)
		}.run_once()
	}
	
	def until(Closure c) {
		end_condition = c
	}
}
