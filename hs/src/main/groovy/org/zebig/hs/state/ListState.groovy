package org.zebig.hs.state

import org.zebig.hs.game.Game

class ListState<T> extends State {
	
	ArrayList<T> storage
	
	ListState(Game game) {
        super(game)
		storage = []
	}
	
	ListState(Game game, ArrayList<T> sto) {
        super(game)
		storage = sto
	}
	
	int size() { storage.size() }
	
	boolean add(T item) {
        game?.transaction?.logListAdd(this, item)
		storage.add(item)
	}
	
	boolean add(int position, T item) {
		game?.transaction?.logListAdd(this, item)
		storage.add(position, item)
	}
	
	boolean addAll(Collection<T> items) {
		storage.addAll(items)
	}
	
	T last() { storage.last() }
	
	T remove(T item) {
		def position = storage.indexOf(item)
		if (storage.remove(item)) {
			game?.transaction?.logListRemove(this, item, position)
		}
	}
	
	T remove(int position) {
		def item = storage.remove(position)
		game?.transaction?.logListRemove(this, item, position)
		return item
	}
	
	boolean contains(T item) {
		storage.contains(item)
	}
	
	void clear() {
		if (storage.size() > 0) {
			game?.transaction?.logListClear(this)
			storage.clear()
		}
	}
	
	ListState<T> clone() {
		new ListState<T>(game, storage.clone() as ArrayList<T>)
	}
	
	void each(Closure c) {
		storage.each {  
			c.call(it)
		}
	}
	
	T find(Closure c) {
		storage.find(c)
	}
	
	T getAt(int position) {
		storage[position]
	}
	
	Collection<T> findAll(Closure c) {
		storage.findAll(c)
	}
	
	boolean isEmpty() {
		storage.isEmpty()
	}
	
	ArrayList<T> minus(T item) {
		storage - item
	}

		ArrayList<T> plus(T item) {
		storage + item
	}
	
	void shuffle() {
		Collections.shuffle(storage)
	}

	@Override
	public boolean equals(Object o) {
		if (o == null) {
			return false
		}
		if (o.getClass() != this.getClass()) {
			return false
		}
		return (this.@storage == (o as ListState).@storage)
	}

	@Override
	public int hashCode() {
		return storage.hashCode()
	}
	
	public String toString() {
		return storage.toString()
	}
	
}
