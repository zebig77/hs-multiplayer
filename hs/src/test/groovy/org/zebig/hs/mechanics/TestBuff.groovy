package org.zebig.hs.mechanics


import java.util.regex.Matcher
import org.zebig.hs.mechanics.buffs.*

import org.junit.Test

import org.zebig.hs.utils.TestHelper

import static org.zebig.hs.mechanics.buffs.BuffType.*

class TestBuff extends TestHelper {

	@Test
	void Buff_test_patterns() {
		Matcher matcher

		matcher = "+19 attack" =~ AddAttackBuff.BUFF_PATTERN
		assert matcher.matches()
		assert matcher[0][1] == "19"
		
		matcher = "+22/+12" =~ AddAttackHealthBuff.BUFF_PATTERN
		assert matcher.matches()
		assert matcher[0][1] == "22"
		assert matcher[0][2] == "12"		
		
		matcher = "+11 health" =~ AddHealthBuff.BUFF_PATTERN
		assert matcher.matches()
		assert matcher[0][1] == "11"
		
		matcher = "spell damage +9" =~ AddSpellDamageBuff.BUFF_PATTERN
		assert matcher.matches()
		assert matcher[0][1] == "9"

		matcher = "change attack to 42" =~ ChangeAttackBuff.BUFF_PATTERN
		assert matcher.matches()
		assert matcher[0][1] == "42"
		
		matcher = "costs (12) more" =~ AddCostBuff.BUFF_PATTERN
		assert matcher.matches()
		assert matcher[0][1] == "12"

		matcher = "costs (1) less" =~ SubCostBuff.BUFF_PATTERN
		assert matcher.matches()
		assert matcher[0][1] == "1"
	}
	
	@Test
	void Buff_return_to_battlefield_when_destroyed() {
		def abo = _play("Abomination") // 4/4, Deathrattle: Deal 2 damage to ALL characters
		_play_and_target("Ancestral Spirit", abo)
		assert abo.has_buff(RETURN_TO_BATTLEFIELD_WHEN_DESTROYED)
		_next_turn()
		def kor = _play("Kor'kron Elite")	// 4/3
		g.player_attacks(kor, abo) // kor should kill abo
		assert !abo.is_dead()
		assert abo.is_in_play()
		assert !abo.has_buff(RETURN_TO_BATTLEFIELD_WHEN_DESTROYED)
		assert p1.hero.get_health() == 28 // abo deathrattle
		assert p2.hero.get_health() == 28 // abo deathrattle
	}

}
