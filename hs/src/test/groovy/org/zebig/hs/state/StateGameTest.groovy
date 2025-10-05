package org.zebig.hs.state;

import org.zebig.hs.game.Card

import org.junit.Test

import org.zebig.hs.utils.TestHelper

class StateGameTest extends TestHelper {
	
	@Test
	void testStateChange() {
        assert g.transaction == null
		g.begin_transaction()
        assert g.transaction != null
        assert g.transaction.change_log.size() == 0
        g.ps.game = g // TODO understand why this is needed !
        g.feugen_died = true
        assert g.feugen_died
        assert g.transaction.change_log.size() == 1
		g.rollback_transaction()
		assert !g.feugen_died
        assert g.transaction == null
	}
	
	@Test
	void testStateTree() {
		def gameNode = g.buildGameTree()
		println gameNode.dump()
	}
	
	@Test
	void testCloneTree() {
		def gameNode = g.buildGameTree()
		println gameNode.dump()
		def n1 = State.countNodes(gameNode)
		def cloneNode = gameNode.clone()
		println gameNode.dump()
		def n2 = State.countNodes(cloneNode)
		assert n1 == n2		
		assert cloneNode.equalsTree(gameNode)
	}
	
	@Test
	void testMapTree() {
		def gameNode = g.buildGameTree()
		def map = State.mapTree(gameNode)
		assert State.countNodes(gameNode) == map.size()
	}
	
	@Test
	void testEqualsTree() {		
		def gameNode1 = g.buildGameTree()
		def gameNode2 = gameNode1.clone()
		assert gameNode1.equalsTree(gameNode2)
		g.next_turn()
		def gameNode3 = g.buildGameTree()
		assert !gameNode3.equalsTree(gameNode2)
	}
	
	@Test
	void testBuffProblem() {
		def c1 = _play("Argent Commander")
		assert c1.has_charge()
		assert c1.has_divine_shield()
		_next_turn()
		def c2 = _play("Argent Commander")
		assert c2.has_charge()
		assert c2.has_divine_shield()

	}

}
