package org.zebig.hs.cards

import org.zebig.hs.game.Card
import org.zebig.hs.mechanics.buffs.BuffType

import org.junit.Test

import org.zebig.hs.utils.TestHelper

class TestWarlock extends TestHelper {

	
	@Test
	void BaneOfDoom_no_summon() {
		// Deal 2 damage to a character. If that kills it, summon a random Demon
		// demons = 'Dread Infernal', 'Succubus', 'Voidwalker', 'Blood Imp', 'Felguard'
		def abo = _play("Abomination")
		_play_and_target("Bane of Doom", abo)
		assert !abo.is_dead()
		assert p1.minions.contains( abo )
	}

	@Test
	void BaneOfDoom_summon_random_demon() {
		// Deal 2 damage to a character. If that kills it, summon a random Demon
		// demons = 'Dread Infernal', 'Succubus', 'Voidwalker', 'Blood Imp', 'Felguard'
		def leg = _play("Leper Gnome")
		_play_and_target("Bane of Doom", leg)
		assert leg.is_dead()
		assert p1.minions.findAll{ it.is_a_demon() }.size() > 0
		println "   - p1.minions = ${p1.minions}"
	}

	@Test
	void BaneOfDoom_summon_dread_infernal() {
		// Deal 2 damage to a character. If that kills it, summon a random Demon
		// demons = 'Dread Infernal', 'Succubus', 'Voidwalker', 'Blood Imp', 'Felguard'
		def abu = _play("Abusive Sergeant")
		_next_random_int(0)
		_play_and_target("Bane of Doom", abu)
		assert p1.minions[0].name == "Dread Infernal"
	}
	
	@Test
	void BaneOfDoom_summon_succubus() {
		// Deal 2 damage to a character. If that kills it, summon a random Demon
		// demons = 'Dread Infernal', 'Succubus', 'Voidwalker', 'Blood Imp', 'Felguard'
		def abu = _play("Abusive Sergeant")
		_next_random_int(1)
		_play_and_target("Bane of Doom", abu)
		assert p1.minions[0].name == "Succubus"
		
		// Test Succubus play - hand contains only succubus
		def suc = g.new_card("Succubus")
		p1.hand.cards.clear()
		p1.hand.add(suc)
		p1.available_mana = suc.cost
		p1.play(suc)
		assert p1.hand.cards.size() == 0
		assert suc.is_in_play()
		
		// Test Succubus play - hand contains another card
		def blu2 = g.new_card("Bluegill Warrior") // should be discarded
		def suc2 = g.new_card("Succubus")
		p1.hand.cards.clear()
		[ suc2, blu2 ].each { p1.hand.add(it) }
		p1.available_mana = suc2.cost
		p1.play(suc2)
		assert p1.hand.cards.size() == 0
		assert suc2.is_in_play()
		
		// Test Succubus play - hand contains 2 other cards
		def lep3 = g.new_card("Leper Gnome") // could be discarded
		def blu3 = g.new_card("Bluegill Warrior") // could be discarded
		def suc3 = g.new_card("Succubus")
		p1.hand.cards.clear()
		[ suc3, blu3, lep3 ].each { p1.hand.add(it) }
		p1.available_mana = suc3.cost
		p1.play(suc3)
		assert p1.hand.contains(lep3) || p1.hand.contains(blu3)
		assert suc3.is_in_play()
		
	}
	
	@Test
	void BaneOfDoom_summon_voidwalker() {
		// Deal 2 damage to a character. If that kills it, summon a random Demon
		// demons = 'Dread Infernal', 'Succubus', 'Voidwalker', 'Blood Imp', 'Felguard'
		def abu = _play("Abusive Sergeant")
		_next_random_int(2)
		_play_and_target("Bane of Doom", abu)
		assert p1.minions[0].name == "Voidwalker"
	}

	@Test
	void BaneOfDoom_summon_blood_imp() {
		// Deal 2 damage to a character. If that kills it, summon a random Demon
		// demons = 'Dread Infernal', 'Succubus', 'Voidwalker', 'Blood Imp', 'Felguard'
		def abu = _play("Abusive Sergeant")
		_next_random_int(3)
		_play_and_target("Bane of Doom", abu)
		assert p1.minions[0].name == "Blood Imp"
	}

	@Test
	void BaneOfDoom_summon_felguard() {
		// Deal 2 damage to a character. If that kills it, summon a random Demon
		// demons = 'Dread Infernal', 'Succubus', 'Voidwalker', 'Blood Imp', 'Felguard'
		def abu = _play("Abusive Sergeant")
		_next_random_int(4)
		_play_and_target("Bane of Doom", abu)
		assert p1.minions[0].name == "Felguard"
	}
	
	@Test
	void BloodImp_play() {
		// Stealth. At the end of your turn, give another random friendly minion +1 Health'
		def bim = _play("Blood Imp")
		assert bim.has_buff(BuffType.STEALTH)
	}

	@Test
	void BloodImp_end_turn_no_friendly_minion() {
		// Stealth. At the end of your turn, give another random friendly minion +1 Health'
		def bim = _play("Blood Imp")
		g.end_turn()
		assert bim.get_health() == bim.card_definition.max_health
	}

	@Test
	void BloodImp_end_turn_with_friendly_minion() {
		// Stealth. At the end of your turn, give another random friendly minion +1 Health'
		def abo = _play("Abomination")
		def bim = _play("Blood Imp")
		assert bim.get_health() == bim.card_definition.max_health
		g.end_turn()
		assert abo.get_health() == abo.card_definition.max_health +1
		assert abo.get_max_health() == abo.card_definition.max_health +1
		assert bim.get_health() == bim.card_definition.max_health
	}

	@Test
	void BloodImp_end_turn_with_2_friendly_minions() {
		// Stealth. At the end of your turn, give another random friendly minion +1 Health'
		def fad = _play("Faerie Dragon")
		def abo = _play("Abomination")
		def bim = _play("Blood Imp")
		assert bim.get_health() == bim.card_definition.max_health
		g.end_turn()
		def nb_health_increased = 0
		if (abo.get_health() == abo.card_definition.max_health +1) {
			nb_health_increased++
		}
		if (fad.get_health() == fad.card_definition.max_health +1) {
			nb_health_increased++
		}
		assert nb_health_increased == 1 // only 1
	}
	
	@Test
	void Corruption_play() {
		// Choose an enemy minion. At the start of your turn, destroy it
		def abo = _play( "Abomination", p2)
		_play_and_target( "Corruption", abo )
		def abo2 = _play( "Abomination", p2)
		_play( "Corruption", abo2 )
		_next_turn()
		_play_and_target( "Silence", abo2 )
		assert !abo.is_dead()
		assert !abo2.is_dead()
		_next_turn()
		assert abo.is_dead()
		assert !abo2.is_dead() // should be saved by silence
	}
	
	@Test
	void Demonfire_damage() {
		// Deal 2 damage to a minion. If it's a friendly Demon, give it +2/+2 instead
		def voi = _play("Voidwalker")
		_next_turn()
		_play("Demonfire", voi) // not friendly, should deal damage
		assert voi.get_attack() == voi.card_definition.attack
		assert voi.get_health() == voi.card_definition.max_health -2
		assert !voi.has_buff("+2/+2")
	}

	@Test
	void Demonfire_friendly_demon() {
		// Deal 2 damage to a minion. If it's a friendly Demon, give it +2/+2 instead
		def voi = _play("Voidwalker")
		_play("Demonfire", voi) // friendly demon, should give +2/+2
		assert voi.get_attack() == voi.card_definition.attack +2
		assert voi.get_health() == voi.card_definition.max_health +2
		assert voi.has_buff("+2/+2")
	}
	
	@Test
	void Doomguard_play() {
		// Charge. Battlecry: Discard two random cards
		def before_hand_size = p1.hand.size()
		def dog = _play("Doomguard")
		assert p1.hand.size() == before_hand_size - 2
		assert dog.is_in_play()
		assert dog.has_charge()
	}
	
	@Test
	void DrainLife_play() {
		// Deal 2 damage. Restore 2 Health to your hero
		def kob = _play("Kobold Geomancer")
		_next_turn()
		p1.hero.health = 20
		_play("Drain Life", kob)
		assert kob.is_dead()
		assert p1.hero.health == 20 + 2
	}
	
	@Test
	void DreadInfernal_battlecry() {
		// Battlecry: Deal 1 damage to ALL other characters
		def lep = _play("Leper Gnome")
		def abo = _play("Abomination")
		_next_turn()
		def blu = _play("Bluegill Warrior")
		def dre = _play("Dread Infernal")
		assert lep.is_dead()
		assert p1.hero.health == 27 // -2 as a result of lep's death, -1 for dre battlecry
		assert p2.hero.health == 29 // -1 for dre battlecry
		assert blu.is_dead()
		assert abo.health == abo.card_definition.max_health -1
		assert dre.health == dre.card_definition.max_health // unhurt
	}
	
	@Test
	void FlameImp_play() {
		// Battlecry: Deal 3 damage to your hero
		_play("Flame Imp")
		assert p1.hero.health == 27
		_play("Explosive Trap") // not possible, I know
		assert p1.secrets.size() != 0
		assert p1.secrets[0].name == "Explosive Trap"
		_play("Flame Imp") // just to check that the trap doesn't trigger
		assert p1.secrets.size() != 0
		assert p1.hero.health == 24	
	}
	
	@Test
	void Hellfire_play() {
		// Deal 3 damage to ALL characters
		def blu = _play("Bluegill Warrior")
		_next_turn()
		def abo = _play("Abomination")
		_play("Hellfire")
		assert abo.health == abo.max_health - 3
		assert p1.hero.health == 30 - 3
		assert p2.hero.health == 30 - 3
		assert blu.is_dead()
	}
	
	@Test
	void LordJaraxxus_play() {
		// Battlecry: Destroy your hero and replace him with Lord Jaraxxus
		
		_play("Lord Jaraxxus")
		assert p1.hero.name == "Lord Jaraxxus"
		assert p1.hero.health == 15
		assert p1.hero.weapon != null
		assert p1.hero.weapon.name == "Blood Fury"
		assert p1.minions.size() == 0
		
		_attack(p1.hero, p2.hero)
		assert p2.hero.health == 30 - p1.hero.weapon.get_attack()
		
		_use_hero_power() // Inferno: Summon a 6/6 Infernal
		assert p1.minions.size() == 1
		assert p1.minions[0].name == "Infernal"
		assert p1.minions[0].attack == 6
		assert p1.minions[0].health == 6
		assert p1.minions[0].creature_type == "demon"		
	}
	
	@Test
	void LordJaraxxus_sacrificial_pact() {
		// Battlecry: Destroy your hero and replace him with Lord Jaraxxus
		
		_play("Lord Jaraxxus")
		assert p1.hero.name == "Lord Jaraxxus"
		_next_turn()
		
		_play("Sacrificial Pact", p2.hero)
		assert p2.hero.is_dead()	
		assert g.is_ended	
	}
	
	
	@Test
	void LordJaraxxus_mirror_entity() {
		// Battlecry: Destroy your hero and replace him with Lord Jaraxxus
		
		_play("Mirror Entity") // Secret: When your opponent plays a minion, summon a copy of it
		_next_turn()
		
		_play("Lord Jaraxxus")
		assert p2.minions.size() == 1
		assert p2.minions[0].name == 'Lord Jaraxxus'
	}
	
	@Test
	void MortalCoil_draw() {
		// Deal 1 damage to a minion. If that kills it, draw a card
		def crz =_play("Crazed Alchemist")	// 2/2
		_next_turn()		
		_play("Kobold Geomancer") 	// +1 Spell Damage
		def before_hand_size = p1.hand.size()
		_play('Mortal Coil', crz)
		assert crz.is_dead()
		assert p1.hand.size() == before_hand_size + 1
	}

	@Test
	void MortalCoil_no_draw() {
		// Deal 1 damage to a minion. If that kills it, draw a card
		def crz =_play("Crazed Alchemist")	// 2/2
		_next_turn()		
		def before_hand_size = p1.hand.size()
		_play('Mortal Coil', crz)
		assert !crz.is_dead()
		assert p1.hand.size() == before_hand_size
	}
	
	@Test
	void PitLord_play() {
		// Battlecry: Deal 5 damage to your hero
		_play("Pit Lord")
		assert p1.hero.health == 25
	}
	
	@Test
	void PowerOverwhelming_play() {
		// Give a friendly minion +4/+4 until end of turn. Then, it dies. Horribly
		def blu = _play("Bluegill Warrior")
		_play("Power Overwhelming", blu)
		assert blu.has_buff('+4/+4')
		assert blu.get_attack() == blu.attack + 4
		assert blu.get_health() == blu.health + 4
		_attack(blu, p2.hero)
		_next_turn()
		assert blu.is_dead()
		
		// test with silence
		_next_turn()
		def blu2 = _play("Bluegill Warrior")
		_play("Power Overwhelming", blu2)
		_attack(blu2, p2.hero)
		_play("Ironbeak Owl", blu2)		// silence
		_next_turn()
		assert !blu2.is_dead()
		assert blu2.get_attack() == blu2.attack
		assert blu2.get_health() == blu2.health
	}
	
	@Test
	void SacrificialPact_play() {
		// Destroy a Demon. Restore 5 Health to your hero
		
		_play("Timber Wolf") // not a demon
		_should_fail("no valid target") { _play("Sacrificial Pact") }
		
		def suc = _play("Succubus") // a demon
		assert suc.is_a_demon()
		p1.hero.health = 10
		_play("Sacrificial Pact", suc)
		assert p1.hero.health == 15
		assert suc.is_dead()
	}
	
	@Test
	void SacrificialJaraxxus() {
		// Destroy a Demon. Restore 5 Health to your hero
		
		_play("Lord Jaraxxus")
		_next_turn()
		
		_play("Sacrificial Pact", p2.hero)
		assert p2.hero.is_dead()
		assert g.is_ended
	}
	
	@Test
	void SenseDemons_2_demons_in_deck() {
		// Put 2 random Demons from your deck into your hand
		
		p1.deck.cards.clear()
		def d1 = g.new_card("Blood Imp")
		def d2 = g.new_card("Succubus")
		p1.deck.add( d1 )	// demon #1
		p1.deck.add( g.new_card("Leper Gnome"))	// not a demon
		p1.deck.add( d2 )	// demon #2
		p1.deck.add( g.new_card("Sacrificial Pact"))
		p1.deck.add( g.new_card("Ironbeak Owl"))
		
		_play("Sense Demons")
		assert p1.hand.contains(d1)
		assert p1.hand.contains(d2)
	}
	
	@Test
	void SenseDemons_1_demon_in_deck() {
		// Put 2 random Demons from your deck into your hand
		
		p1.deck.cards.clear()
		def d1 = g.new_card("Blood Imp")
		p1.deck.add( d1 )	// demon #1
		p1.deck.add( g.new_card("Leper Gnome"))	// not a demon
		p1.deck.add( g.new_card("Sacrificial Pact"))
		p1.deck.add( g.new_card("Ironbeak Owl"))
		
		_play("Sense Demons")
		assert p1.hand.contains(d1)
		assert p1.hand.cards.find{ it.name == "Worthless Imp" } != null
	}

	@Test
	void SenseDemons_no_demon_in_deck() {
		// Put 2 random Demons from your deck into your hand
		// If you have no more demons (or never had any if you got this card with a mind vision or something)
		// you will draw  Worthless Imps instead.
		
		p1.deck.cards.clear()
		p1.deck.add( g.new_card("Leper Gnome"))	// not a demon
		p1.deck.add( g.new_card("Sacrificial Pact"))
		p1.deck.add( g.new_card("Ironbeak Owl"))

		p1.hand.cards.clear()		
		_play("Sense Demons")
		assert p1.hand.cards.size() == 2
		assert p1.hand.cards[0].name == "Worthless Imp"
		assert p1.hand.cards[0].cost == 1
		assert p1.hand.cards[0].attack == 1
		assert p1.hand.cards[0].health == 1
		assert p1.hand.cards[1].name == "Worthless Imp"
		assert p1.hand.cards[1].cost == 1
		assert p1.hand.cards[1].attack == 1
		assert p1.hand.cards[1].health == 1
	}
	
	@Test
	void ShadowBolt_play() {
		// Deal 4 damage to a minion.
		
		// no valid target
		_play("Jungle Panther")		// stealth
		_play("Faerie Dragon")	// cannot be targeted
		_next_turn()
		_should_fail("no valid target") { _play("Shadow Bolt") }
		_next_turn()
		
		// 1 valid target
		def anc = _play("Ancient Watcher")	// 2/5
		_next_turn()
		_play("Kobold Geomancer")	// +1 Spell Damage
		_play("Shadow Bolt", anc)
		assert anc.is_dead()
	}
	
	@Test
	void Shadowflame_play() {
		// Destroy a friendly minion and deal its Attack damage to all enemy minions.
		
		_should_fail("no valid target") { _play("Shadowflame") }
		def aci = _play("Acidic Swamp Ooze")	// 3/2
		_next_turn()
		
		def aco = _play("Acolyte of Pain")	// 1/3
		def ama = _play("Amani Berserker")	// 2/3
		_next_turn()
		
		def bou = _play("Bluegill Warrior")
		_play("Shadowflame", aci)
		assert aco.is_dead()
		assert ama.is_dead()
		assert aci.is_dead()
		assert !bou.is_dead()        // only enemy minions
		
		// test with emperor cobra
		_next_turn()
		def sgi = _play("Sea Giant")		// 8/8
		_next_turn()
		def emp = _play("Emperor Cobra")	// Destroy any minion damaged by this minion
		_play("Shadowflame", emp)
		assert !sgi.is_dead()        // Shadowflame deals the damage, not the cobra
		assert emp.is_dead()
	}
	
	@Test
	void SiphonSoul_play() {
		// Destroy a minion. Restore 3 Health to your hero
		
		_play("Faerie Dragon")
		_should_fail("no valid target") { _play("Siphon Soul") }
		
		def sbp = _play("Silverback Patriarch")
		p1.hero.health = 20
		_play("Siphon Soul", sbp)
		assert p1.hero.health == 23
		assert sbp.is_dead()
	}

	@Test
	void Soulfire_play() {
		// Deal 4 damage. Discard a random card
		
		def before_hand_size = p1.hand.size()
		_play("Kobold Geomancer") // Spell Damage +1
		_play("Soulfire", p2.hero)
		assert p2.hero.health == 30 - 4 - 1
		assert p1.hand.size() == before_hand_size - 1
		
		// no card in hand
		p1.hand.cards.clear()
		_play("Soulfire", p2.hero) // should not fail
		assert p2.hero.health == 30 - 4 - 1 - 4 - 1	
	}
	
	@Test
	void SummoningPortal_play() {
		// Your minions cost (2) less, but not less than (1)
		
		_play("Summoning Portal")
		Card c1 = g.new_card("Drain Life") // not a minion
		Card c2 = g.new_card("Blood Imp") // cost=1
		Card c3 = g.new_card("Felguard") // cost=3
		p1.hand.cards.clear()
		p1.hand.add(c1)
		p1.hand.add(c2)
		p1.hand.add(c3)
		assert c1.get_cost() == c1.cost
		assert c2.get_cost() == 1
		assert c3.get_cost() == c3.cost - 2
		
		_next_turn()
		Card c4 = g.new_card("Abomination")
		p1.hand.cards.clear()
		p1.hand.add(c4)
		assert c4.get_cost() == c4.cost // only your minions 
	}
	
	@Test
	void Voidcaller_play_no_summon() {
		p1.hand.cards.clear()
		def v = _play("Voidcaller")
		assert v.has_deathrattle()
		assert p1.minions.size() == 1
		assert p1.minions[0].name == "Voidcaller"
		v.dies()
		assert p1.minions.size() == 0
	}
	
	@Test
	void Voidcaller_play_summon() {
		p1.hand.cards.clear()
		p1.hand.cards.add(g.new_card("Blood Imp"))
		def v = _play("Voidcaller")
		assert v.has_deathrattle()
		assert p1.minions.size() == 1
		assert p1.minions[0].name == "Voidcaller"
		v.dies()
		assert p1.minions.size() == 1
		assert p1.minions[0].name == "Blood Imp"
	}
	
	@Test
	void VoidTerror_play_2_neighbors() {
		// Battlecry: Destroy the minions on either side of this minion and gain their Attack and Health
		
		def lep = _play("Leper Gnome")
		def fae = _play("Faerie Dragon")
		def lep_a = lep.get_attack()
		def lep_h = lep.get_health()
		def fae_a = fae.get_attack()
		def fae_h = fae.get_health()
		def voi = _play("Void Terror", 1) // insert into lep and fae
		assert lep.is_dead()
		assert fae.is_dead()
		assert voi.get_attack() == voi.attack + lep_a + fae_a 
		assert voi.get_health() == voi.health + lep_h + fae_h 
	}
	
	@Test
	void VoidTerror_play_1_neighbor() {
		// Battlecry: Destroy the minions on either side of this minion and gain their Attack and Health
		
		def lep = _play("Leper Gnome")
		def lep_a = lep.get_attack()
		def lep_h = lep.get_health()
		def voi = _play("Void Terror")
		assert lep.is_dead()
		assert voi.get_attack() == voi.attack + lep_a 
		assert voi.get_health() == voi.health + lep_h 
	}
	
	@Test
	void VoidTerror_play_no_neighbor() {
		// Battlecry: Destroy the minions on either side of this minion and gain their Attack and Health
		
		def voi = _play("Void Terror")
		assert voi.get_attack() == voi.attack
		assert voi.get_health() == voi.health 
	}
	
	@Test
	void TwistingNether_play() {
		// Destroy all minions
		
		def fae = _play("Faerie Dragon")
		_next_turn()
		
		def rag = _play("Ragnaros the Firelord")
		_next_turn()
		
		_play("Twisting Nether")
		assert fae.is_dead()
		assert rag.is_dead()
	}
}
