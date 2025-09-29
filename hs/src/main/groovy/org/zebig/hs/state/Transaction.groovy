package org.zebig.hs.state

class Transaction {
	
	Stack<Change> change_log = new Stack<Change>()
	boolean in_rollback = false

	void log(Change c) {
		change_log.push(c)
	}
	
	void logPropertyCreate(MapState s, String property_name) {
		if (!in_rollback) {
			log(new PropertyChange(s, property_name, null, 'C'))
		}
	}
	
	void logPropertyUpdate(MapState s, String property_name, Object old_value) {
		if (!in_rollback) {
			log(new PropertyChange(s, property_name, old_value, 'U'))
		}
	}
	
	void logListAdd(ListState sl, Object item) {
        if (!in_rollback) {
			log(new ListChange(sl, item, 'A'))
		}
	}

	void logListRemove(ListState sl, Object item) {
        if (!in_rollback) {
			log(new ListChange(sl, item, 'R'))
		}
	}
	
	void logListRemove(ListState sl, Object item, int position) {
        if (!in_rollback) {
			log(new ListChange(sl, item, 'R', position))
		}
	}
	
	void logListClear(ListState sl) {
        log(new ListChange(sl, sl.clone().storage, 'C'))
	}
	
}
