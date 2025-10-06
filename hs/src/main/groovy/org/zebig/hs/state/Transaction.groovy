package org.zebig.hs.state

import java.beans.PropertyChangeEvent

/* log elementary raw changes to be able to rollback if needed (invalid action detected or bug) */
class Transaction {
	
	Stack<Change> change_log = new Stack<Change>()
	boolean in_rollback = false

    void process_state_change(ObservableMap ms, PropertyChangeEvent event) {
        if (!in_rollback) {
            if (event instanceof ObservableMap.PropertyEvent) {
                if (event instanceof ObservableMap.PropertyAddedEvent) {
                    ObservableMap.PropertyAddedEvent e = event as ObservableMap.PropertyAddedEvent
                    logPropertyCreate(ms, e.propertyName)
                }
                if (event instanceof ObservableMap.PropertyRemovedEvent) {
                    ObservableMap.PropertyRemovedEvent e = event as ObservableMap.PropertyRemovedEvent
                    logPropertyRemove(ms, e.propertyName)
                }
                if (event instanceof ObservableMap.PropertyUpdatedEvent) {
                    ObservableMap.PropertyUpdatedEvent e = event as ObservableMap.PropertyUpdatedEvent
                    logPropertyUpdate(ms, e.propertyName, e.oldValue)
                }
            }
        }
    }

    void process_state_change(ObservableList ls, PropertyChangeEvent event) {
        if (!in_rollback) {
            if (event instanceof ObservableMap.PropertyEvent) {
                if (event instanceof ObservableList.ElementAddedEvent) {
                    ObservableList.ElementAddedEvent e = event as ObservableList.ElementAddedEvent
                    logListAdd(ls, e.newValue)
                }
                if (event instanceof ObservableList.ElementUpdatedEvent) {
                    ObservableList.ElementUpdatedEvent e = event as ObservableList.ElementUpdatedEvent
                    logListUpdate(ls, e.index, e.oldValue)
                }
                if (event instanceof ObservableList.ElementRemovedEvent) {
                    ObservableList.ElementRemovedEvent e = event as ObservableList.ElementRemovedEvent
                    logListRemove(ls, e.index)
                }
            }
        }
    }

    void log(Change c) {
		change_log.push(c)
	}
	
    void logPropertyCreate(ObservableMap s, String property_name) {
        log(new PropertyChange(s, property_name, null, 'C'))
    }

    void logPropertyRemove(ObservableMap s, String property_name) {
        log(new PropertyChange(s, property_name, null, 'R'))
    }

    void logPropertyUpdate(ObservableMap s, String property_name, Object old_value) {
        log(new PropertyChange(s, property_name, old_value, 'U'))
	}
	
	void logListAdd(ObservableList sl, Object item) {
        log(new ListChange(sl, item, 'A'))
	}

	void logListRemove(ObservableList sl, Object item) {
        log(new ListChange(sl, item, 'R'))
	}
	
	void logListRemove(ObservableList sl, Object item, int position) {
        log(new ListChange(sl, item, 'R', position))
	}

    void logListUpdate(ObservableList sl, int index, Object oldValue) {
        log(new ListChange(sl, oldValue, 'U', index))
    }
	
	void logListClear(ObservableList sl) {
        log(new ListChange(sl, sl.clone(), 'C'))
	}
	
}
