package org.zebig.hs.state

class PropertyChange extends Change {
	
	final MapState s
	final String property_name
	final Object old_value
	final String action // C=create, U=update
	
	
	PropertyChange(MapState s, String property_name, Object old_value, String action) {
		assert action in [ 'C', 'U' ]
		this.s = s
		this.property_name = property_name
		this.old_value = old_value
		this.action = action
		//println this
	}
	
	def undo() {
		if (action == 'C') { // was a creation -> delete
			s.@storage.remove(property_name)
		}
		else { // update -> restore old value
			s.@storage[property_name] = old_value
		}		
	}

	
	String toString() {
		"PropertyChange($s,$property_name,$old_value,$action)"
	}
}
