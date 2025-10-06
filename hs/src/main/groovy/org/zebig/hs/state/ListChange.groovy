package org.zebig.hs.state

class ListChange extends Change {
	
	ObservableList ls
	Object item
	String action
	int position
	
	
	ListChange(ObservableList ls, Object item, String action, int position=-1) {
		assert action in [ 'A', 'R', 'C', 'U' ]	// ADD, REMOVE, CLEAR, UPDATE
		this.ls = ls
		this.item = item
		this.action = action
		this.position = position
	}
	
	def undo() {
		if (action == 'A') { // undo an add -> remove
			ls.remove(item)
		}
		else if (action == 'R' ){ // undo a remove -> add
			if (position == -1) {
				ls.add(item)	// no info on original position
			}
			else {
				ls.add(position, item)
			}
		}
        else if (action == 'U') {
            ls.set(position,item) // restore old value
        }
		else { // undo clear, restore full list 
			assert action == 'C'
			ls = item as ObservableList
		}
	}
	
	String toString() {
		"ListChange(${ls.toString()},$item,$action)"
	}

}
