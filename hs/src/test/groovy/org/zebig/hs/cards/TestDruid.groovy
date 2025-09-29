package org.zebig.hs.cards

import org.zebig.hs.game.Target

import static org.zebig.hs.mechanics.buffs.BuffType.*
import static org.junit.Assert.*
import org.zebig.hs.game.Card


import org.junit.Test

import org.zebig.hs.utils.TestHelper

class TestDruid extends TestHelper {

	@Test
	void AncientOfLore_choice1_draw() {
		// Choose One - Draw 2 cards; or Restore 5 Health.
		p1.next_choices = [ "Draw 2 cards" ]
		def before_hand_size = p1.hand.size()
		_play("Ancient of Lore")
		assert p1.hand.size() == before_hand_size + 2
	}

	@Test
	void AncientOfLore_choice2_restore_health() {
		// Choose One - Draw 2 cards; or Restore 5 Health.
		p1.next_choices = [ "Restore 5 Health", p1.hero ]
		def before_hand_size = p1.hand.size()
		p1.hero.set_health(27) 
		_play("Ancient of Lore")
		assert p1.hand.size() == before_hand_size
		assert p1.hero.get_health() == 30
	}

	@Test
	void AncientOfLore_fail_no_choice() {
		try {
			_play("Ancient of Lore")
			fail("should have failed: no choice")
		}
		catch( Exception e ) {
			println e // ok
		}
	}

	@Test
	void AncientOfLore_fail_invalid_choice() {
		p1.next_choices = [ "YES !" ]
		try {
			_play("Ancient of Lore")
			fail("should have failed: invalid choice")
		}
		catch( Exception e ) {
			println e // ok
		}
	}

	@Test
	void AncientOfWar_choice1_plus_5_health_taunt() {
		// Choose One - Ancient of War gets +5 Health and Taunt; or +5 Attack
		p1.next_choices = [ "Taunt and +5 Health" ]
		def c = _play("Ancient of War")
		assert c.has_buff('+5 Health')
		assert c.get_health() == c.health + 5
		assert c.get_max_health() == c.card_definition.max_health + 5
		assert c.has_buff(TAUNT)
	}

	@Test
	void AncientOfWar_choice2_plus_5_attack() {
		// Choose One - Ancient of War gets +5 Health and Taunt; or +5 Attack
		p1.next_choices = [ "+5 Attack" ]
		def c =_play("Ancient of War")
		assert c.get_attack() == c.card_definition.attack + 5
		assert !c.has_buff(TAUNT)
	}

	@Test
	void Bite_play() {
		// Give your hero +4 Attack this turn and 4 Armor
		_play("Bite")
		assert p1.hero.get_attack() == 4
		assert p1.hero.armor == 4
		g.end_turn()
		assert p1.hero.get_attack() == 0
		assert p1.hero.armor == 4
	}
	
	@Test
	void Cenarius_choice1() {
		// Choose One - Give your other minions +2/+2; or Summon two 2/2 Treants with Taunt
		def m1 = _play("Panther")
		def m2 = _play("Angry Chicken")
		p1.next_choices = [ "Give your other minions +2/+2" ]
		def cen = _play("Cenarius")
		assert m1.get_attack() == m1.card_definition.attack + 2
		assert m2.get_attack() == m2.card_definition.attack + 2
		assert cen.get_attack() == cen.card_definition.attack // unchanged
		assert m1.get_health() == m1.card_definition.max_health + 2
		assert m2.get_health() == m2.card_definition.max_health + 2
		assert cen.get_health() == cen.card_definition.max_health // unchanged
	}

	@Test
	void Cenarius_choice2() {
		// Choose One - Give your other minions +2/+2; or Summon two 2/2 Treants with Taunt
		def m1 = _play("Panther")
		def m2 = _play("Angry Chicken")
		p1.next_choices = [ 'Summon two 2/2 Treants with Taunt' ]
		def cen = _play("Cenarius")
		assert m1.get_attack() == m1.card_definition.attack
		assert m2.get_attack() == m2.card_definition.attack
		assert cen.get_attack() == cen.card_definition.attack // unchanged
		assert m1.get_health() == m1.card_definition.max_health
		assert m2.get_health() == m2.card_definition.max_health
		assert cen.get_health() == cen.card_definition.max_health // unchanged
		List<Card> treants = p1.minions.findAll { it.name == "Treant" }
		assert treants.size() == 2
		assert treants[0].has_buff(TAUNT)
		assert treants[1].has_buff(TAUNT)
	}
	
	@Test
	void Claw_play() {
		// Give your hero +2 Attack this turn and 2 Armor
		
		assert p1.hero.get_attack() == 0
		assert p1.hero.armor == 0
		_play("Claw")
		assert p1.hero.get_attack() == 2
		assert p1.hero.armor == 2
		_attack(p1.hero, p2.hero)
		assert p2.hero.health == 28
		
		_next_turn()
		assert p2.hero.get_attack() == 0
		assert p2.hero.armor == 2
	}
	
	@Test
	void Dream_play() {
		// Return a minion to its owner's hand
		
		_should_fail("no valid target") {
			_play("Dream")
		}
		_next_turn()
		
		// enemy minion
		def bou = _play("Boulderfist Ogre")
		_next_turn()
		
		_play("Dream", bou)
		assert ! bou.is_in_play()
		assert p2.hand.contains(bou)
		
		// friendly minion
		def ela = _play("Elven Archer", p2.hero)
		_play("Dream", ela)
		assert ! ela.is_in_play()
		assert p1.hand.contains(ela)
	}
	
	@Test
	void DruidOfTheClaw_choice1() {
		// Choose One - Charge; or +2 Health and Taunt
		p1.next_choices = [ "Charge" ]
		def dru = _play("Druid of the Claw")
		assert dru.is_in_play()
		assert dru.has_charge()
		assert !dru.has_taunt()
		assert !dru.has_buff("+2 Health")
	}

	@Test
	void DruidOfTheClaw_choice2() {
		// Choose One - Charge; or +2 Health and Taunt
		p1.next_choices = [ "+2 Health and Taunt" ]
		def dru = _play("Druid of the Claw")
		assert dru.is_in_play()
		assert !dru.has_charge()
		assert dru.has_taunt()
		assert dru.has_buff("+2 Health")
	}
	
	@Test
	void ForceOfNature_play() {
		// Summon three 2/2 Treants with Charge that die at the end of the turn
		_play("Force of Nature")
		assert p1.minions.size() == 3
		p1.minions.each {
			assert it.name == "Treant"
			assert it.attack == 2
			assert it.health == 2
			assert it.max_health == 2
			assert it.has_charge()
			g.player_attacks(it as Target, p2.hero)
		}
		assert p2.hero.health == 24
	}

	@Test
	void ForceOfNature_end_of_turn_die() {
		// Summon three 2/2 Treants with Charge that die at the end of the turn
		_play("Force of Nature")
		def to_die = p1.minions()
		_next_turn()
		to_die.each{
			assert it.is_dead()
		}
	}

	@Test
	void ForceOfNature_end_of_turn_dont_die() {
		// Summon three 2/2 Treants with Charge that die at the end of the turn
		_play("Force of Nature")
		def treants = p1.minions()
		treants.each {
			_play_and_target( "Silence", it)
		}
		_next_turn()
		treants.each{
			assert !it.is_dead()
		}
	}
	
	@Test
	void Innervate_ok() {
		// Gain 2 Mana Crystals this turn only
		p1.max_mana = 6
		p1.available_mana = 6
		p1.play(g.new_card("Innervate"))
		assert p1.available_mana == 8
	}
	
	@Test
	void Innervate_limite() {
		// Gain 2 Mana Crystals this turn only
		p1.max_mana = 9
		p1.available_mana = 9
		p1.play(g.new_card("Innervate"))
		assert p1.available_mana == 10
	}
	
	@Test
	void Innervate_next_turn() {
		// Gain 2 Mana Crystals this turn only
		p1.max_mana = 3
		p1.available_mana = 3
		p1.play(g.new_card("Innervate"))
		assert p1.available_mana == 5
		_next_turn()
		_next_turn()
		assert p1.max_mana == 4
		assert p1.available_mana == 4 // back to normal, effect lost
	}
	
	@Test
	void KeeperOfTheGrove_choice1_deal_damage() {
		// Choose One - Deal 2 damage; or Silence a minion.
		_play("Kobold Geomancer") // +1 Spell Damage should have no effect (not a spell damage)
		p1.next_choices = [ "Deal 2 damage", p2.hero ]
		_play("Keeper of the Grove")
		assert p2.hero.health == 28
	}

	@Test
	void KeeperOfTheGrove_choice2_silence() {
		// Choose One - Deal 2 damage; or Silence a minion.
		def lep = _play("Leper Gnome")
		_next_turn()
		p1.next_choices = [ "Silence a minion", lep ]
		def kpg = _play("Keeper of the Grove")
		_next_turn()
		_attack(lep, kpg)
		assert lep.is_dead()
		assert p2.hero.health == 30 // lep should not deal its deathrattle damage
	}
	
	@Test void MarkOfNature_choice1_attack_increase() {
		// Choose One - Give a minion +4 Attack; or +4 Health and Taunt
		def shb = _play("Shieldbearer")
		p1.next_choices = [ shb, 'Give a minion +4 Attack' ]
		_play("Mark of Nature")
		assert shb.get_attack() == shb.attack + 4
		assert shb.get_health() == shb.health
		assert shb.has_buff(TAUNT)
	}

	@Test void MarkOfNature_choice2_taunt() {
		// Choose One - Give a minion +4 Attack; or +4 Health and Taunt
		def emp = _play("Emperor Cobra")	// 2/3
		p1.next_choices = [ emp, '+4 Health and Taunt' ]
		_play("Mark of Nature")
		assert emp.get_attack() == emp.attack
		assert emp.get_health() == emp.health + 4
		assert emp.has_buff(TAUNT)
	}
	
	@Test
	void Moonfire_play() {
		// Deal 1 damage
		_play("Kobold Geomancer") // +1 Spell Damage
		_play('Moonfire', p2.hero)
		assert p2.hero.health == 28
	}
	
	@Test
	void Nightmare_play() {
		// Give a minion +5/+5.  At the start of your next turn, destroy it
		
		_should_fail("no valid target") {
			_play("Nightmare")
		}
		
		def blu = _play("Bluegill Warrior")
		_play("Nightmare", blu)
		assert blu.get_attack() == blu.attack + 5
		assert blu.get_health() == blu.health + 5
		_attack(blu, p2.hero)
		assert p2.hero.health == 30 - blu.attack - 5
		_next_turn()
		
		assert !blu.is_dead()
		_next_turn()
		
		assert blu.is_dead()
	}
	
	@Test
	void Nourish_choice1() {
		// Nourish: Choose One - Gain 2 Mana Crystals; or Draw 3 cards.
		p1.next_choices = [ "Gain 2 Mana Crystals" ]
		p1.available_mana = 6
		p1.max_mana = 6
		p1.play(g.new_card("Nourish"))
		assert p1.max_mana == 6+2
		assert p1.available_mana == 6-5+2
	}

	@Test
	void Nourish_choice2() {
		// Nourish: Choose One - Gain 2 Mana Crystals; or Draw 3 cards.
		p1.next_choices = [ 'Draw 3 cards' ]
		def before_hand_size = p1.hand.size()
		_play("Nourish")
		assert p1.hand.size() == before_hand_size + 3
	}
	
	@Test
	void PoisonSeeds_no_minion() {
		_play("Poison Seeds")
	}
	
	@Test
	void PoisonSeeds_minions() {
		/* Player A */ def abo = _play("Abomination")
		_next_turn()
		
		/* Player B */ def gnome = _play("Leper Gnome")
		/* Player B */ _play("Poison Seeds")
		
		assert abo.is_dead()
		assert gnome.is_dead()
		assert p1.hero.health == 28 // because abo is dead 
		assert p2.hero.health == 26 // because gnome is dead + abo
		
		assert p1.minions.size() == 1
		assert p1.minions[0].name == "Treant"
		assert p1.minions[0].attack == 2
		assert p1.minions[0].health == 2
		
		assert p2.minions.size() == 1
		assert p2.minions[0].name == "Treant"
		assert p2.minions[0].attack == 2
		assert p2.minions[0].health == 2
	}
	
	@Test
	void PowerOfTheWild_choice1() {
		// Power of the Wild: Choose One - Give your minions +1/+1; or summon a 3/2 Panther
		def blu = _play("Bluegill Warrior")
		def lep = _play("Leper Gnome")
		p1.next_choices = [ "Give your minions +1/+1" ]
		assert !blu.has_buff("+1/+1")
		assert !lep.has_buff("+1/+1")
		_play("Power of the Wild")
		assert blu.has_buff("+1/+1")
		assert lep.has_buff("+1/+1")
	}

	@Test
	void PowerOfTheWild_choice2() {
		// Power of the Wild: Choose One - Give your minions +1/+1; or summon a 3/2 Panther
		p1.next_choices = [ 'Summon a 3/2 Panther' ]
		_play("Power of the Wild")
		assert p1.minions.size() == 1
		assert p1.minions[0].name == "Panther"
		assert p1.minions[0].attack == 3
		assert p1.minions[0].health == 2
	}
	
	@Test
	void SavageRoar_play() {
		// Give your characters +2 Attack this turn
		
		def lep = _play("Leper Gnome") // for the opponent
		_next_turn()
		def fae = _play("Faerie Dragon") // for you
		_play("Savage Roar")
		assert lep.get_attack() == lep.attack // should be unchanged
		assert fae.get_attack() == fae.attack + 2
		assert p1.hero.get_attack() == 2
		assert p2.hero.get_attack() == 0
		
		// check end turn ends effect
		_next_turn()
		assert fae.get_attack() == fae.attack
		assert p1.hero.get_attack() == 0
	}
	
	@Test
	void Savagery_play() {
		// Deal damage equal to your hero's Attack to a minion
		
		_play("Faerie Dragon") // cannot be targeted
		_should_fail("no valid target") { _play("Savagery") }
		
		def rec = _play("Reckless Rocketeer")	// 5/2
		_attack(rec, p2.hero)
		_next_turn()
		_play("Savage Roar")	// hero gains +2 Attack
		_play("Savagery", rec)
		assert rec.is_dead()
	}
	
	@Test
	void SoulOfTheForest_play() {
		// Give your minions "Deathrattle: Summon a 2/2 Treant
		
		_play("Force of Nature") // Summon three 2/2 Treants with Charge that die at the end of the turn
		_play("Soul of the Forest")
		p1.minions().each {
			assert it.name == "Treant"
			assert it.attack == 2
			assert it.health == 2
			assert it.has_charge()
			assert it.text != null
			assert it.text.contains("Deathrattle: Summon a 2/2 Treant")
			_attack( it, p2.hero)
		}
		assert p2.hero.health == 30 -2 -2 -2
		_next_turn()
		_next_turn()
		(0..2).each {
			assert !p1.minions[it].is_dead()
		}
		_next_turn()
		_next_turn()
		(0..2).each {
			assert !p1.minions[it].is_dead()
		}
	}
	
	@Test
	void Starfall_deal_5_damage_to_a_minion() {
		// Choose One - Deal 5 damage to a minion; or 2 damage to all enemy minions
		
		_should_fail("no valid target") { _play("Starfall") }
		_next_turn()		
		
		def bou = _play("Boulderfist Ogre")	// 6/7
		_next_turn()
		
		_play("Kobold Geomancer") // +1 Spell Damage 
		p1.next_choices = [ "Deal 5 damage to a minion", bou ]
		
		_play("Starfall")
		assert bou.health == bou.max_health -5 -1	// 1
	}
	
	@Test
	void Starfall_deal_2_damage_to_all() {
		// Choose One - Deal 5 damage to a minion; or 2 damage to all enemy minions
		
		def bou = _play("Boulderfist Ogre")	// 6/7
		def fae = _play("Faerie Dragon")
		_next_turn()
		
		def kob = _play("Kobold Geomancer") // +1 Spell Damage, should not be killed 
		p1.next_choices = [ "2 damage to all enemy minions" ]
		
		_play("Starfall")
		assert bou.health == bou.max_health -2 -1	// 3
		assert fae.is_dead()
		assert !kob.is_dead()
	}
	
	@Test
	void Swipe_hero() {
		// Deal 4 damage to an enemy and 1 damage to all other enemies
		_play("Swipe", p2.hero)
		assert p2.hero.health == 26
	}

	@Test
	void Swipe_hero_and_minions() {
		// Deal 4 damage to an enemy and 1 damage to all other enemies
		def lep = _play("Leper Gnome")
		def fae = _play("Faerie Dragon")
		_next_turn()
		_play("Swipe", p2.hero)
		assert p2.hero.health == 26
		assert lep.is_dead()
		assert !fae.is_dead()
		assert fae.get_health() == 1 // not a target, should take damage
	}

	@Test
	void Swipe_hero_and_minions_spell_damage_increase() {
		// Deal 4 damage to an enemy and 1 damage to all other enemies
		def lep = _play("Leper Gnome")
		def fae = _play("Faerie Dragon")
		def abo = _play("Abomination")
		_next_turn()
		_play("Kobold Geomancer")
		_play("Swipe", p2.hero)
		assert p2.hero.health == 25
		assert lep.is_dead()
		assert fae.is_dead()
		assert abo.get_health() == abo.card_definition.max_health -2
	}
	
	@Test
	void WildGrowth_10_mana() {
		// Gain an empty Mana Crystal
		
		p1.max_mana = 10
		p1.hand.cards.clear()
		_play("Wild Growth")
		assert p1.hand.size() == 1
		assert p1.hand.cards[0].name == "Excess Mana"
		p1.play(p1.hand.cards[0]) /// draw a card
		assert p1.hand.size() == 1
		assert p1.hand.cards[0].name != "Excess Mana"
	}
	
	@Test
	void WildGrowth_less_than_10_mana() {
		// Gain an empty Mana Crystal
		
		p1.max_mana = 9
		p1.available_mana = 5
		def before_available_mana = p1.available_mana
		def c = g.new_card("Wild Growth")
		p1.hand.add(c)
		p1.play(c)
		assert p1.max_mana == 10
		assert p1.available_mana == before_available_mana - c.cost // empty mana crystal doesn't give a mana
	}
	
	@Test
	void Wrath_choice1() {
		// Choose One - Deal 3 damage to a minion; or Deal 1 damage to a minion and draw a card
		def shi = _play("Shieldbearer")
		_next_turn()
		p1.next_choices = [ "Deal 3 damage to a minion", shi ]
		_play("Wrath")
		assert shi.get_health() == 1
	}

	@Test
	void Wrath_choice2() {
		// Choose One - Deal 3 damage to a minion; or Deal 1 damage to a minion and draw a card
		def lep = _play("Leper Gnome")
		_next_turn()
		p1.next_choices = [ "Deal 1 damage to a minion and draw a card", lep ]
		def before_hand_size = p1.hand.size()
		_play("Wrath")
		assert lep.is_dead()
		assert p1.hand.size() == before_hand_size + 1
	}

}
