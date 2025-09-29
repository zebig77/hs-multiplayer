package org.zebig.hs.cards


import org.junit.Test

import org.zebig.hs.utils.TestHelper

import static org.junit.Assert.fail

class TestRogue extends TestHelper {

	@Test
	void AnubArAmbusher_play_alone() {
		def ambusher = _play("Anub'ar Ambusher")
		assert ambusher.is_in_play
		ambusher.dies()
		assert ambusher.is_dead()
		assert !p1.hand.contains(ambusher)
	}

	@Test
	void AnubArAmbusher_play_return() {
		def si7agent = _play("SI-7 Agent")
		def ambusher = _play("Anub'ar Ambusher")
		ambusher.dies()
		assert p1.hand.contains(si7agent)
	}

	@Test
	void Assassinate_ok() {
		// Destroy a damaged enemy minion.
		def abo = _play("Abomination", p2)
		_play_and_target("Assassinate", abo)
		assert abo.is_dead()
		assert !abo.is_in_play()
		assert !p2.minions.contains(abo)
	}

	@Test
	void Backstab_no_target() {
		// Deal 2 damage to an undamaged minion
		try {
			_play("Backstab")
			fail("should have failed: no valid target")
		}
		catch( Exception e ) {
			println e // OK
		}
	}

	@Test
	void Backstab_no_undamaged_target() {
		// Deal 2 damage to an undamaged minion
		def abo = g.summon(p2, "Abomination")
		abo.set_health( abo.get_health() - 1) // damaged
		try {
			_play("Backstab")
			fail("should have failed: no valid target")
		}
		catch( Exception e ) {
			println e // OK
		}
	}

	@Test
	void Backstab_deal_damage() {
		// Deal 2 damage to an undamaged minion
		def abo = g.summon(p2, "Abomination")
		_play_and_target("Backstab", abo)
		assert abo.get_health() == abo.card_definition.max_health -2
	}

	@Test
	void Backstab_deal_damage_plus_1() {
		// Deal 2 damage to an undamaged minion
		def abo = g.summon(p2, "Abomination")
		_play("Azure Drake") // gives spell damage +1
		_play_and_target("Backstab", abo)
		assert abo.get_health() == abo.card_definition.max_health -3
	}

	@Test
	void AssassinsBlade_ok() {
		// attack=3; max_health=4
		_play("Assassin's Blade")
		assert p1.hero.weapon.durability == 4
		g.player_attacks(p1.hero, p2.hero)
		assert p2.hero.health == 30 - 3
		assert p1.hero.weapon.attack == 3
		assert p1.hero.weapon.durability == 3
	}

	@Test
	void Betrayal_no_effect() {
		// An enemy minion deals its damage to the minions next to it
		def abo = g.summon(p2, "Abomination")
		assert abo.right_neighbor() == null
		assert abo.left_neighbor() == null
		assert abo.neighbors().size() == 0
		_play_and_target("Betrayal", abo)
	}

	@Test
	void Betrayal_right_neighbor() {
		// An enemy minion deals its damage to the minions next to it
		def abo = g.summon(p2, "Abomination")
		def tiw = g.summon(p2, "Timber Wolf")
		assert abo.right_neighbor() == tiw
		assert abo.left_neighbor() == null
		assert abo.neighbors() == [tiw]
		_play_and_target("Betrayal", abo)
		assert tiw.is_dead()
	}

	@Test
	void Betrayal_left_neighbor() {
		// An enemy minion deals its damage to the minions next to it
		def tiw = g.summon(p2, "Timber Wolf")
		def abo = g.summon(p2, "Abomination")
		assert abo.right_neighbor() == null
		assert abo.left_neighbor() == tiw
		assert abo.neighbors() == [tiw]
		_play_and_target("Betrayal", abo)
		assert tiw.is_dead()
	}

	@Test
	void Betrayal_both_neighbors() {
		// An enemy minion deals its damage to the minions next to it
		def tiw = g.summon(p2, "Timber Wolf")
		def abo = g.summon(p2, "Abomination")
		def arm = g.summon(p2, "Archmage")
		assert abo.right_neighbor() == arm
		assert abo.left_neighbor() == tiw
		assert abo.neighbors() == [arm, tiw]
		_play_and_target("Betrayal", abo)
		assert tiw.is_dead()
		assert arm.get_health() == arm.card_definition.max_health - abo.get_attack()
	}

	@Test
	void DefiasRingleader_no_combo() {
		// Combo: Summon a 2/1 Defias Bandit
		def drl = _play("Defias Ringleader")
		assert drl.is_in_play()
		assert p1.minions.size() == 1
	}

	@Test
	void BladeFlurry_no_weapon() {
		// Destroy your weapon and deal its damage to all enemies
		try {
			_play("Blade Flurry")
			fail("should have failed: no weapon")
		}
		catch( Exception e ) {
			println e // ok
		}
	}

	@Test
	void BladeFlurry_deal_damage() {
		// Destroy your weapon and deal its damage to all enemies
		def tiw = g.summon(p2, "Timber Wolf")
		p1.hero.equip_weapon(2,2)
		_play("Blade Flurry")
		assert p2.hero.get_health() == 28
		assert tiw.is_dead()
		assert p1.hero.weapon == null
	}

	@Test
	void ColdBlood_play() {
		// Give a minion +2 Attack. Combo: +4 Attack instead

		// no combo
		def abo = _play("Abomination")
		def shb = _play("Shieldbearer")
		_next_turn()
		_next_turn()
		_play("Cold Blood", abo)
		assert abo.get_attack() == abo.attack + 2

		// combo
		_play("Cold Blood", shb)
		assert shb.get_attack() == shb.attack + 4

		// return to hand
		_next_turn()
		_play("Sap", abo)
		assert abo.get_attack() == abo.attack

		// silence
		_play("Ironbeak Owl", shb)
		assert shb.get_attack() == shb.attack
	}

	@Test
	void Conceal_gives_stealth() {
		// Give your minions Stealth until your next turn.
		def lep = _play("Leper Gnome")
		def kor = _play("Kor'kron Elite")
		_play("Conceal")
		assert lep.has_stealth()
		assert kor.has_stealth()
		g.player_attacks(kor, p2.hero)
		assert !kor.has_stealth()
		// minions played after conceal don't have the buff
		def abo = _play("Abomination")
		assert !abo.has_stealth()
	}

	@Test
	void Conceal_until_your_next_turn() {
		// Give your minions Stealth until your next turn.
		def lep = _play("Leper Gnome")
		def kor = _play("Kor'kron Elite")
		_play("Conceal")
		assert lep.has_stealth()
		assert kor.has_stealth()
		g.next_turn()
		assert lep.has_stealth()
		assert kor.has_stealth()
		g.next_turn()
		assert !lep.has_stealth()
		assert !kor.has_stealth()
	}

	@Test
	void DeadlyPoison_no_weapon() {
		// Give your weapon +2 Attack
		try {
			_play("Deadly Poison")
			fail("Should have failed: no weapon")
		}
		catch (Exception e) {
			println e // ok
		}
	}

	@Test
	void DeadlyPoison_ok() {
		// Give your weapon +2 Attack
		p1.hero.equip_weapon(1,1)
		_play("Deadly Poison")
		assert p1.hero.weapon.get_attack() == 1+2
		g.player_attacks(p1.hero, p2.hero)
		assert p2.hero.health == 27
		assert p1.hero.weapon == null
	}

	@Test
	void DefiasRingleader_combo() {
		// Combo: Summon a 2/1 Defias Bandit
		_play("Abomination")
		def drl = _play("Defias Ringleader")
		assert drl.is_in_play()
		assert p1.minions.size() == 3
	}

	@Test
	void Eviscerate_no_combo() {
		// Deal 2 damage. Combo: Deal 4 damage instead
		_play("Eviscerate", p2.hero)
		assert p2.hero.health == 28
	}

	@Test
	void Eviscerate_with_combo() {
		// Deal 2 damage. Combo: Deal 4 damage instead
		_play("Defias Ringleader")
		_play("Eviscerate", p2.hero)
		assert p2.hero.health == 26
	}

	@Test
	void FanOfKnives_play() {
		// Deal 1 damage to all enemy minions. Draw a card.
		def lep = _play("Leper Gnome")
		def fae = _play("Faerie Dragon")
		_next_turn()
		def ela = _play("Elven Archer", fae)
		assert !fae.is_dead()
		def before_hand_size = p1.hand.size()
		_play("Fan of Knives")
		assert lep.is_dead()
		assert fae.is_dead()
		assert !ela.is_dead()
		assert p1.hand.size() == before_hand_size + 1
	}

	@Test
	void Headcrack_play() {
		// Deal 2 damage to the enemy hero. Combo: Return this to your hand next turn

		// no combo
		def hc1 = _play("Headcrack")
		assert p2.hero.health == 28
		_next_turn()
		assert !p2.hand.contains(hc1)
		assert p1.secrets.size() == 0
		assert p2.secrets.size() == 0

		// combo
		_next_turn()
		_play("Leper Gnome")
		def hc2 = _play("Headcrack")
		assert p2.hero.health == 26
		assert p1.secrets.size() == 0
		assert p2.secrets.size() == 0
		_next_turn()
		assert p2.hand.contains(hc2)
		assert p1.secrets.size() == 0
		assert p2.secrets.size() == 0
	}

	@Test
	void Kidnapper_play() {
		// Combo: Return a minion to its owner's hand

		def bou = _play("Boulderfist Ogre")
		_next_turn()

		// test with no combo
		_play("Kidnapper")

		// test with combo
		_play("Kidnapper", bou)
		assert !bou.is_in_play()
		assert p2.hand.contains(bou)
	}

	@Test
	void MasterOfDisguise_play() {
		// Battlecry: Give a friendly minion Stealth

		// no minion
		def md1 = _play("Master of Disguise")
		assert !md1.has_stealth()

		// with a friendly minion
		def md2 = _play("Master of Disguise", md1)
		assert md1.has_stealth()

		// 2 possible choices
		_play("Master of Disguise", md2)
		assert md2.has_stealth()
	}

	@Test
	void PatientAssassin_play() {
		// Stealth. Destroy any minion damaged by this minion
		def pat = _play("Patient Assassin")	// 1/1
		assert pat.has_stealth()
		_next_turn()
		def bou = _play("Boulderfist Ogre")
		_next_turn()
		_attack(pat, bou)
		assert bou.is_dead()
		assert bou.is_destroyed
		assert pat.is_dead()

		// prepare betrayal
		def shb = _play("Shieldbearer")
		def pat2 = _play("Patient Assassin")
		def fae = _play("Faerie Dragon")
		_next_turn()

		// check lose stealth when attacks
		_next_turn()
		_attack(pat2, p2.hero)
		assert !pat2.has_stealth()

		// check betrayal
		_next_turn()
		_play("Betrayal", pat2) // An enemy minion deals its damage to the minions next to it
		assert shb.is_dead()
		assert fae.is_dead()
		assert !pat2.is_dead()
	}

	@Test
	void PerditionsBlade_play() {
		// weapon
		// Battlecry: Deal 1 damage. Combo: Deal 2 instead

		// no combo
		_play("Perdition's Blade", p2.hero)	// 2/2
		assert p1.hero.weapon != null
		assert p1.hero.weapon.name == "Perdition's Blade"
		assert p1.hero.weapon.get_attack() == 2
		assert p1.hero.get_attack() == 2
		assert p1.hero.weapon.get_durability() == 2
		assert p2.hero.health == 29

		// combo
		_play("Perdition's Blade", p2.hero)
		assert p2.hero.health == 29 -2

		// attack with weapon
		_attack(p1.hero, p2.hero)
		assert p2.hero.health == 29 -2 -2
		assert p1.hero.weapon.get_durability() == 1
	}

	@Test
	void Preparation_play() {
		// The next spell you cast this turn costs (3) less

		_play("Preparation")
		def evi = g.new_card("Eviscerate")
		p1.hand.add(evi)
		assert evi.get_cost() == 0 // normally 2
		p1.available_mana = 0
		p1.next_choices = [p2.hero]
		p1.play(evi)

		// check effect stops
		def evi2 = g.new_card("Eviscerate")
		p1.hand.add(evi2)
		assert evi2.get_cost() == evi2.cost // not reduced
		p1.available_mana = 0
		p1.next_choices = [p2.hero]
		_should_fail("cost cannot be paid") { p1.play(evi2) }

		// check effect stops after end of turn
		_next_turn()
		_next_turn()
		_play("Preparation")
		def ass = g.new_card("Assassinate")
		p1.hand.add(ass)
		assert ass.get_cost() == ass.cost - 3
		_next_turn()
		assert ass.get_cost() == ass.cost
	}

	@Test
	void Sap_play() {
		// Return an enemy minion to your opponent's hand

		// no valid target
		_play("Faerie Dragon")
		_next_turn()
		_should_fail("no valid target") { _play("Sap") }

		// 1 valid target
		_next_turn()
		def bou = _play("Boulderfist Ogre")
		_next_turn()
		_play("Sap", bou)
		assert !bou.is_in_play()
		assert p2.hand.contains(bou)
	}

	@Test
	void Shadowstep_play() {
		// Return a friendly minion to your hand. It costs (2) less

		_should_fail("no valid target") { _play("Shadowstep") }
		_next_turn()

		// opponent play a minion
		def blu = _play("Bluegill Warrior")
		_next_turn()

		// should be impossible: not a friendly minion
		_should_fail("no valid target") { _play("Shadowstep", blu) }

		// should be possible for a friendly minion
		def nib = _play("Nightblade") // deal 3 damage to opponent hero
		assert p2.hero.health == 27
		_play("Shadowstep", nib)
		assert p1.hand.contains(nib)
		assert nib.get_cost() == nib.cost - 2	// 5 - 2
		p1.available_mana = 3
		p1.play(nib)
		assert p2.hero.health == 24
		assert p1.available_mana == 0

		// cost reduction effect should be lost when returning to hand
		_next_turn()
		_play("Vanish")
		assert p2.hand.contains(nib)
		assert nib.get_cost() == nib.cost
	}

	@Test
	void Shiv_play() {
		// Deal 1 damage. Draw a card

		_play("Kobold Geomancer")	// +1 Spell Damage
		def before_hand_size = p1.hand.size()
		_play("Shiv", p2.hero)
		assert p2.hero.health == 28
		assert p1.hand.size() == before_hand_size + 1
	}

	@Test
	void SI7Agent_play() {
		// Combo: Deal 2 damage

		// no combo
		_play("SI-7 Agent")

		// combo
		_play("Kobold Geomancer")	// +1 Spell Damage, should have no effect
		_play("SI-7 Agent", p2.hero)
		assert p2.hero.health == 28
	}

	@Test
	void SinisterStrike_play() {
		// Deal 3 damage to the enemy hero

		_play("Kobold Geomancer")
		_play("Sinister Strike")
		assert p2.hero.health == 30 -3 -1
	}

	@Test
	void Sprint_play() {
		// Draw 4 cards

		def before_hand_size = p1.hand.size()
		def before_deck_size = p1.deck.size()
		_play("Sprint")
		assert p1.hand.size() == before_hand_size + 4
		assert p1.deck.size() == before_deck_size - 4

		_play("Sprint") // max hand size should be reached
		assert p1.hand.size() == 10
		assert p1.deck.size() == before_deck_size - 8
	}

	@Test
	void Vanish_play() {
		// Return all minions to their owner's hand
		def fae = _play("Faerie Dragon")
		def lep = _play("Leper Gnome")
		_next_turn()
		def ela = _play("Elven Archer", p2.hero)
		_play("Vanish")
		assert !fae.is_in_play()
		assert !lep.is_in_play()
		assert !ela.is_in_play()
		assert p2.hand.contains(fae)
		assert p2.hand.contains(lep)
		assert p1.hand.contains(ela)
	}

}
