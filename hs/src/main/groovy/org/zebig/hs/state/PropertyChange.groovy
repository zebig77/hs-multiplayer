package org.zebig.hs.state

class PropertyChange extends Change {
	
	ObservableMap s
	String property_name
	Object old_value
	String action // C=create, U=update
	
	PropertyChange(ObservableMap s, String property_name, Object old_value, String action) {
		assert action in [ 'C', 'U', 'R' ]
		this.s = s
		this.property_name = property_name
		this.old_value = old_value
		this.action = action
	}
	
	def undo() {
		if (action == 'C') { // was a creation -> delete
			s.remove(property_name)
		}
		else if (action == 'U') { // update -> restore old value
			s[property_name] = old_value
		}
        else { // Remove
            s.remove(property_name)
        }
	}

	
	String toString() {
		"PropertyChange($s,$property_name,$old_value,$action)"
	}
}
