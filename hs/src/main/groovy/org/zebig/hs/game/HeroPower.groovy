package org.zebig.hs.game

import org.zebig.hs.mechanics.Trigger

import java.beans.PropertyChangeEvent

class HeroPower extends GameObject {
	
	def state = [:] as ObservableMap
	
	HeroPower( HeroPowerDefinition power_definition) {
        super(power_definition.game)
		this.name = power_definition.name
		this.text = power_definition.text
		this.cost = power_definition.cost
		this.get_targets = power_definition.get_targets
		use_counter = 0
		triggers.clear()
		power_definition.triggers.each{ Trigger t ->
			Trigger new_t =  new Trigger(t.event_class, t.script, this)
			triggers.add( new_t )
		}
        state.addPropertyChangeListener {
            process_state_change(it)
        }
	}

    void process_state_change(PropertyChangeEvent event) {
        game.transaction?.process_state_change(state, event)
    }
	
	String getName() { state.name }
	void setName(String n) { state.name = n }
	
	String getText() { state.text }
	void setText(String t) { state.text = t }
	
	int getCost() { state.cost }
	void setCost(int c) { state.cost = c }
	
	int getUse_counter() { state.use_counter }
	void setUse_counter(int uc) { state.use_counter = uc }
	
	List<Closure> getGet_targets() { state.get_targets }
	void setGet_targets(List<Closure> gt) { state.get_targets = gt }

		
	String toString() {
		return "'$name' power"
	}

}
