package org.zebig.hs.state

import org.zebig.hs.game.Game

class MapState extends State {

    MapState(Game game) {
        super(game)
    }
	
	def storage = [:]
	
	void setProperty(String name, value) {
        if (storage.containsKey(name)) {
            game?.transaction?.logPropertyUpdate(this, name, storage[name])
        }
        else {
            game?.transaction?.logPropertyCreate(this, name)
        }
		storage[name] = value
	}
	
	def getProperty(String name) {
		storage[name]
	}
	
	int size() { storage.size() }
	
	
	MapState clone() {
		def ps2 = new MapState(this.game)
		this.@storage.each { k,v ->
			ps2[k] = v
		}
		return ps2
	}

	@Override
	boolean equals(Object o) {
		if (o == null) {
			return false
		}
		if (o.getClass() != this.getClass()) {
			return false
		}
		boolean result = this.@storage == (o as MapState).@storage
		return result
	}

	@Override
	int hashCode() {
		return storage.hashCode()
	}
	
}
