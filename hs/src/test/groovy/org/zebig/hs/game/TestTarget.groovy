package org.zebig.hs.game;

import org.zebig.hs.mechanics.buffs.BuffType;

import org.junit.Test

import org.zebig.hs.utils.TestHelper

import static org.zebig.hs.mechanics.buffs.BuffType.*
import static org.zebig.hs.mechanics.buffs.BuffType.TAUNT

class TestTarget extends TestHelper {
	
	@Test
	void Target_new() {
		Card c2 = g.new_card('Timber Wolf')
		assert c2 != null
		assert c2.get_attack() == 1
		assert c2.is_a_minion()
		assert c2.is_a_beast()
		assert !c2.has_buff(TAUNT)
		Card c3 = g.new_card("Abomination")
		assert c3 != null
		assert !c3.has_buff(TAUNT)
		assert !c3.has_buff(WINDFURY)
		g.summon(p1, c3)
		assert p1.minions.contains(c3)
		assert c3.has_buff(TAUNT) // as an effect of being put in play
	}
	
	@Test
	void Target_plus() {
		Card c1 = g.new_card('Abomination')		
		Card c2 = g.new_card('Timber Wolf')
		assert c1 + c2 == [ c1, c2 ]
	}

	@Test
	void Target_silence() {
		Card abo = g.new_card('Abomination')	
		g.summon(p1, abo)
		assert abo.has_buff(TAUNT)
		Card ibo = g.new_card('Ironbeak Owl')
		p1.hand.add(ibo)
		p1.next_choices = [ abo ]
		p1.available_mana = ibo.get_cost()
		p1.play(ibo)
		assert !abo.has_buff(TAUNT)
	}

}
