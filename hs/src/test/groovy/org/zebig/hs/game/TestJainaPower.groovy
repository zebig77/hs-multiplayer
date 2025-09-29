package org.zebig.hs.game;

import org.junit.Test

import org.zebig.hs.utils.TestHelper

public class TestJainaPower extends TestHelper {
	
	@Test
	public void JainaPower_on_Amani_Berserker() {
		// I want Jaina as p1
		p1.hero = new JainaProudmoore(g)
		p1.hero.controller = p1
		Card amb = _play("Amani Berserker")
		assert amb.get_attack() == amb.card_definition.attack
		assert amb.get_health() == amb.card_definition.max_health
		p1.next_choices = [ amb ]
		_use_hero_power()
		assert amb.get_attack() == amb.card_definition.attack + 3
		assert amb.get_health() == amb.card_definition.max_health -1
	}

}
