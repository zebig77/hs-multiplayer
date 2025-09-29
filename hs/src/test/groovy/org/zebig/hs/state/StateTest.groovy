package org.zebig.hs.state

import org.zebig.hs.utils.TestHelper;

import static org.junit.Assert.*;

import org.junit.Test;

class StateTest extends TestHelper {

	@Test
	void testState() {
		def s = new MapState()
		s.test = "hello"
		assert s.test == "hello"
		s.test = "world"
		assert s.test == "world"
	}
	
	@Test
	void testStateList() {
		def sl = new ListState(g)
		sl.add("Hello")
		sl.add("World")
		assert sl.size() == 2
	}
	
	@Test
	void testClone() {
		def ls = new ListState(g)
		ls.add("Hello")
		ls.add("World")
		ListState ls2 = ls.clone()
		assert ls2.size() == 2
		assert ls2.contains("World")
		
		def ps = new MapState(g)
		ps.test1 = "hello"
		ps.test2 = "world"
		def ps2 = new MapState(g)
		ps.@storage.each { k,v ->
			ps2[k] = v
		}
		assert ps2.size() == 2
	}
	

	@Test
	void testTransaction() {
		def sl = new ListState(g)
		g.begin_transaction()
		sl.add("Hello")
		sl.add("World")
		assert sl.size() == 2
		g.rollback_transaction()
		assert sl.size() == 0
	}

	@Test
	void testClear() {
		def sl = new ListState(g)
		sl.add("Hello")
		sl.add("World")
		assert sl.size() == 2
		sl.clear()
		assert sl.size() == 0
		sl.add("Hello")
		sl.add("World")
		g.begin_transaction()
		sl.clear()
		assert sl.size() == 0
		g.rollback_transaction()
		assert sl.size() == 2
	}
	
	@Test
	void testPrint() {
		def ps = new MapState()
		ps.day = 13
		ps.month = "September"
		ps.year = 1964
		def ls = []
		ls.add("Sylvie")
		ls.add("Aurélien")
		ls.add("Camille")
		//State.print()
	}
	
	@Test
	void testEquals() {
		def s1 = new MapState()
		def s2 = new MapState()
		assert s1.@storage != null
		assert s2.@storage != null
		assert s1.@storage == s2.@storage
		assert s1 == s2
		s1.x = 1
		assert s1.@storage != s2.@storage
		s2.x = 1
		assert s1.@storage == s2.@storage
		assert s1 == s2
	}

}
