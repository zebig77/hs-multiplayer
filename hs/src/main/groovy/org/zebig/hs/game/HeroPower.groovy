package org.zebig.hs.game

import org.zebig.hs.mechanics.Trigger
import org.zebig.hs.state.MapState

class HeroPower extends GameObject {
	
	MapState ps
	
	HeroPower( HeroPowerDefinition power_definition) {
        super(power_definition.game)
		ps = new MapState()
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
	}
	
	String getName() { ps.name }
	void setName(String n) { ps.name = n }
	
	String getText() { ps.text }
	void setText(String t) { ps.text = t }
	
	int getCost() { ps.cost }
	void setCost(int c) { ps.cost = c }
	
	int getUse_counter() { ps.use_counter }
	void setUse_counter(int uc) { ps.use_counter = uc }
	
	List<Closure> getGet_targets() { ps.get_targets }
	void setGet_targets(List<Closure> gt) { ps.get_targets = gt }

		
	String toString() {
		return "'$name' power"
	}

}
