package org.zebig.hs.state

class ListChange extends Change {
	
	final ListState ls
	final Object item
	final String action
	final int position
	
	
	ListChange(ListState ls, Object item, String action, int position=-1) {
		assert action in [ 'A', 'R', 'C' ]	// ADD, REMOVE, CLEAR
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
				ls.storage.add(item)	// no info on original position
			}
			else {
				ls.storage.add(position, item)
			}
		}
		else { // undo clear, restore full list 
			assert action == 'C'
			ls.storage = item
		}
	}
	
	String toString() {
		"ListChange(${ls.toString()},$item,$action)"
	}

}
