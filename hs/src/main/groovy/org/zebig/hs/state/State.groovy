package org.zebig.hs.state

import org.zebig.hs.game.Card
import org.zebig.hs.game.Game
import org.zebig.hs.mechanics.Trigger

abstract class State {
	
	abstract int size()
	abstract boolean equals(Object o)
	abstract int hashCode()

    Game game

    State(Game game) {
        this.game = game
    }
	
	static StateNode buildGameTree(Game g) { // TODO replace g by game
		StateNode gameNode = new StateNode("Game", g.ps)
		int player_num=0
		g.players.each {
			player_num++
			def playerNode = gameNode.add( "Player#${player_num}", it.ps)
			def handNode = playerNode.add( "Hand#${player_num}", it.hand.cards)
			def deckNode = playerNode.add( "Deck#${player_num}", it.deck.cards)
			def minionsNode = playerNode.add( "Minions#${player_num}", it.minions)
			def secretsNode = playerNode.add( "Secrets#${player_num}", it.secrets)
			def heroNode = playerNode.add( "Hero#${player_num}", it.hero.ps)			
			heroNode.add( "HeroPower#${player_num}", it.hero.power.ps)
			if (it.hero.weapon != null) {
				heroNode.add( "Weapon#${player_num}", it.hero.weapon.ps)
			}
			createCardNode(it.hand.cards, handNode)
			createCardNode(it.deck.cards, deckNode)
			createCardNode(it.minions, minionsNode)
			createCardNode(it.secrets, secretsNode)
		}
		return gameNode
	}
	
	static int countNodes(StateNode sn, int count=0) {
		count++
		//println "${sn.name} -> ${sn.state}"
		sn.children.each { child ->
			count = countNodes(child, count)
		}
		return count
	}
	
	static Map<State,StateNode> mapTree(StateNode root) {
		return root.asMap()
	}
	
	static void printTree(StateNode sn) {
		println sn.dump()
	}
	
	static void createCardNode(ListState<Card> ls, StateNode parent) {
		ls.@storage.each { Card c ->
			def cardNode = parent.add("Card ${parent.name} $c", c.ps)
			c.triggers.@storage.each { Trigger t ->
				cardNode.add("Trigger ${parent.name} $c > $t", t.ps)
			}
			if (c.buffs.size() > 0) { parent.add("Buffs ${parent.name} $c", c.buffs) } 
		}
	}
}

class StateNode {
	
	final String name
	final State state
	List<StateNode> children = []
	
	StateNode(String name, State s) {
		this.name = name
		this.state = s
	}
	
	StateNode add(String name, State s) {
		def child = new StateNode(name, s)
		children.add(child)
		return child
	}
	
	StateNode add(StateNode child) {
		children.add(child)
		return child
	}
	
	static void mapNode(StateNode sn, Map<String,State> map) {
		map[sn.name] = sn.state
		sn.children.each {
			mapNode(it, map)
		}
	}
	
	Map<String,State> asMap() {
		Map<String,State> result = [:]
		mapNode(this, result)
		return result
	}
	
	static StateNode cloneNode(StateNode parent, StateNode sn) {
		StateNode clone_node = new StateNode(sn.name, sn.state.clone())
		parent?.add(clone_node)
		sn.children.each { StateNode child_node ->
			cloneNode(clone_node,child_node)
		}
		return clone_node
	}
	
	StateNode clone() {
		return cloneNode(null,this)
	}
	
	String dump() {
		StringWriter sw = new StringWriter()
		dumpNodes(this, sw, 0)
		return sw.toString()
	}
	
	static void dumpNodes(StateNode sn, StringWriter sw, int level) {
		if (sn.state.size() > 0) {
			def indent = "   "*level
			sw << "$indent- $sn [n=${sn.state.size()}]\n"
		}
		sn.children.each {
			dumpNodes(it, sw, level+1)
		}
	}
	
	boolean equalsTree(StateNode otherNode, StringBuilder sb=null) {
		def t1 = this.asMap()
		def t2 = otherNode.asMap()
		boolean result = true
		if (t1.size() != t2.size()) {
			result = false
			sb?.append("T1 size = ${t1.size()} - T2 size = ${t2.size()}\n")
		}
		t1.each { k, v ->
            if (k == "Game" || k.startsWith("Player")) {
                return // ignore
            }
			if (t2[k] == null) {
				sb?.append("key $k in T1 but not in T2\n")
				result = false
				return
			}
			if (t2[k] != v) {
				sb?.append("different content for key $k in T1 and T2\n")
				sb?.append("T1[$k]=${v}\n")
				sb?.append("T2[$k]=${t2[k]}\n")
				result = false
			}
		}
		t2.each { k, v ->
			if (t1[k] == null) {
				sb?.append("key $k in T2 but not in T1\n")
				result = false
			}
		}
		return result
	}
	
	String toString() {
		this.name
	}
}