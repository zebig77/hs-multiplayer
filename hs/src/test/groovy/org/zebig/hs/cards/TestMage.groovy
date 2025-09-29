package org.zebig.hs.cards


import org.zebig.hs.mechanics.buffs.BuffType

import org.junit.Test

import org.zebig.hs.utils.TestHelper

class TestMage extends TestHelper {

	@Test
	public void ArcaneExplosion_play() {
	// Deal 1 damage to all enemy minions
		def lep = _play("Leper Gnome")
		def abo = _play("Abomination")
		_next_turn()
		_play("Arcane Explosion")
		assert lep.is_dead()
		assert abo.health == abo.max_health - 1
	}

	@Test
	public void ArcaneIntellect_draw() {
		// Draw 2 cards
		def before_hand_size = p1.hand.size()
		_play("Arcane Intellect")
		assert p1.hand.size() == before_hand_size + 2
	}
	
	@Test
	public void EtherealArcanist_play_no_secret() {
		// If you control a Secret at the end of your turn, gain +2/+2
		def eta = _play("Ethereal Arcanist")
		assert eta.has_buff("+2/+2") == false
		_next_turn()
		assert eta.has_buff("+2/+2") == false
	}
	
	@Test
	public void EtherealArcanist_play_1_secret() {
		// If you control a Secret at the end of your turn, gain +2/+2
		def eta = _play("Ethereal Arcanist")
		_play("Counterspell")
		assert eta.has_buff("+2/+2") == false
		_next_turn()
		assert eta.has_buff("+2/+2") == true
	}
	
	@Test
	public void ArcaneMissiles_hit_only_hero() {
		// Deal 3 damage randomly split among enemy characters
		_play("Arcane Missiles")
		assert p2.hero.get_health() == 27
	}

	@Test
	public void ArcaneMissiles_several_possible_targets() {
		// Deal 3 damage randomly split among enemy characters
		def abo = _play("Abomination")
		_next_turn()
		_play("Arcane Missiles")
		assert (30 - p2.hero.get_health()) + (4 - abo.get_health()) == 3
	}
	
	@Test
	public void ArchmageAntonidas_play_1_spell() {
		// Whenever you cast a spell, put a 'Fireball' spell into your hand
		_play("Archmage Antonidas")
		def nb_fireball = p1.hand.cards.findAll{it.name == "Fireball"}.size()
		_play_and_target("Ice Lance", p2.hero)
		assert p1.hand.cards.findAll{it.name == "Fireball"}.size() == nb_fireball + 1
	}

	@Test
	public void ArchmageAntonidas_play_2_spells() {
		// Whenever you cast a spell, put a 'Fireball' spell into your hand
		_play("Archmage Antonidas")
		def nb_fireball = p1.hand.cards.findAll{it.name == "Fireball"}.size()
		_play_and_target("Ice Lance", p2.hero)
		assert p2.hero.is_frozen()
		_play_and_target("Ice Lance", p2.hero)
		assert p2.hero.get_health() == 26
		assert p1.hand.cards.findAll{it.name == "Fireball"}.size() == nb_fireball + 2
	}
	
	@Test
	public void Blizzard_play() {
		// Deal 2 damage to all enemy minions and Freeze them
		def fad = _play("Faerie Dragon")
		def ars = _play("Argent Squire")
		assert ars.has_buff(BuffType.DIVINE_SHIELD) == true
		_next_turn()
		_play( "Blizzard" )
		assert fad.is_dead()
		assert ars.is_dead() == false
		assert ars.is_frozen()
		assert ars.has_buff(BuffType.DIVINE_SHIELD) == false
	}
	
	@Test
	public void ConeOfCold_1_minion_killed() {
		// Freeze a minion and the minions next to it, and deal 1 damage to them
		def lep = _play("Leper Gnome", p2)
		_play_and_target("Cone of Cold", lep)
		assert lep.is_dead()
	}

	@Test
	public void ConeOfCold_1_minion_frozen() {
		// Freeze a minion and the minions next to it, and deal 1 damage to them
		def abo = _play("Abomination", p2)
		_play_and_target("Cone of Cold", abo)
		assert abo.is_dead() == false
		assert abo.is_frozen()
		assert abo.get_health() == abo.card_definition.max_health - 1
	}

	@Test
	public void ConeOfCold_1_center_minion_frozen_1_neighbor() {
		// Freeze a minion and the minions next to it, and deal 1 damage to them
		def abo = _play("Abomination", p2)
		def lep = _play("Leper Gnome", p2)
		_play_and_target("Cone of Cold", abo)
		assert abo.is_dead() == false
		assert abo.is_frozen()
		assert abo.get_health() == abo.card_definition.max_health - 1
		assert lep.is_dead()
	}

	@Test
	public void ConeOfCold_1_center_minion_frozen_2_neighbors() {
		// Freeze a minion and the minions next to it, and deal 1 damage to them
		def lep1 = _play("Leper Gnome", p2)
		def abo  = _play("Abomination", p2)
		def lep2 = _play("Leper Gnome", p2)
		_play_and_target("Cone of Cold", abo)
		assert abo.is_dead() == false
		assert abo.is_frozen()
		assert abo.get_health() == abo.card_definition.max_health - 1
		assert lep1.is_dead()
		assert lep2.is_dead()
	}

	@Test
	public void ConeOfCold_1_center_minion_killed_2_neighbors() {
		// Freeze a minion and the minions next to it, and deal 1 damage to them
		def abo  = _play("Abomination", p2)
		def lep1 = _play("Leper Gnome", p2)
		def lep2 = _play("Leper Gnome", p2)
		_play_and_target("Cone of Cold", lep1)
		assert abo.is_dead() == false
		assert abo.is_frozen()
		assert abo.get_health() == abo.card_definition.max_health - 1
		assert lep1.is_dead()
		assert lep2.is_dead()
	}
	
	@Test
	public void Counterspell_play() {
		// Secret: When your opponent casts a spell, Counter it
		def csp = _play("Counterspell")
		assert p1.secrets.contains(csp)
		_next_turn() // p1 swap with p2
		assert p2.secrets.contains(csp)
		_play("Arcane Missiles")
		assert p1.hero.get_health() == 30
		assert p2.hero.get_health() == 30
		assert p1.secrets.isEmpty()
		assert p2.secrets.isEmpty()
		// check that the secret triggers is removed
		_play("Arcane Missiles")
		assert p2.hero.get_health() == 27
	}

	@Test
	public void Duplicate_play_unfriendly_minion_dies() {
		/* Player A */ _play("Duplicate")
		assert p1.secrets.size() == 1
		assert p1.secrets[0].name == "Duplicate"
		
		/* Player A */ 
		def abo = _play("Abomination")
		
		_next_turn()
		
		/* Player B */ 
		def blu = _play("Bluegill Warrior")
		_attack(blu, abo) // blu dies, should not trigger secret
		_next_turn()		
		
		assert p1.secrets.size() == 1
	}
	
	@Test
	public void Duplicate_play_friendly_minion_dies() {
		/* Player A */
		def abo = _play("Abomination")
		_next_turn()
		
		/* Player B */ 
		_play("Duplicate")
		assert p1.secrets.size() == 1
		assert p1.secrets[0].name == "Duplicate"
		def blu = _play("Bluegill Warrior")	
		def before_hand_size = p1.hand.size()
		def blu_cnt1 = p1.hand.cards.findAll { it.name == "Bluegill Warrior" }.size()
		_next_turn()
		
		/* Player A */
		_attack(abo, blu) // blu dies, should trigger secret
		_next_turn()
		
		/* Player B */ 
		assert p1.secrets.size() == 0
		assert p1.hand.size() == before_hand_size + 3 // draw + 2 duplicates
		def blu_cnt2 = p1.hand.cards.findAll { it.name == "Bluegill Warrior" }.size()
		assert blu_cnt2 == blu_cnt1 + 2
	}
	
	
	@Test
	public void EtherealArcanist_opponent_play_1_secret() {
		_play("Counterspell") // should have no effect
		_next_turn()
		def eta = _play("Ethereal Arcanist")
		assert eta.has_buff("+2/+2") == false
		_next_turn()
		assert eta.has_buff("+2/+2") == false
	}
	
	@Test
	public void FrostNova_play() {
		// Freeze all enemy minions
		def abo = _play("Abomination")
		def fae = _play("Faerie Dragon")
		_next_turn()
		_play("Frost Nova")
		assert abo.is_frozen()
		assert fae.is_frozen()
		_next_turn()
		assert abo.is_frozen()
		assert fae.is_frozen()
		_next_turn()
		assert abo.is_frozen() == false
		assert fae.is_frozen() == false
	}
	
	@Test
	public void IceBarrier_play() {
		// Secret: When your hero is attacked, gain 8 Armor
		def ice = _play("Ice Barrier")
		assert p1.hero.armor == 0
		assert p1.secrets.size() == 1
		assert p1.secrets.contains(ice)
		_next_turn()
		def blu = _play("Bluegill Warrior")
		_attack(blu, p2.hero)
		assert p2.hero.health == 30
		assert p2.hero.armor == 8 - blu.attack
		assert p1.secrets.size() == 0
		assert p1.secrets.contains(ice) == false
		def blu2 = _play("Bluegill Warrior")
		_attack(blu2, p2.hero)
		assert p2.hero.health == 30
		assert p2.hero.armor == 8 - (2 * blu.attack)
	}

	@Test
	public void IceBlock_fatal_damage() {
		// Secret: When your hero takes fatal damage, prevent it and become Immune this turn
		def ice = _play("Ice Block")
		assert p1.hero.triggers.size() == 1 // when_a_hero_takes_damage
		p1.hero.health = 1
		assert p1.secrets.size() == 1
		assert p1.secrets.contains(ice)
		assert ice.is_revealed() == false
		_next_turn()
			def blu = _play("Bluegill Warrior")
			_attack(blu, p2.hero)
			assert p2.hero.health == 1
			assert p2.hero.has_buff(BuffType.IMMUNE)
			assert ice.is_revealed()
			assert p1.hero.triggers.size() == 0 // its power only
			def blu2 = _play("Bluegill Warrior")
			_attack(blu2, p2.hero)
			assert p2.hero.health == 1
			assert p2.hero.has_buff(BuffType.IMMUNE)
		_next_turn()
		// check that the IMMUNE buff is removed
		assert p2.hero.has_buff(BuffType.IMMUNE) == false
		assert p2.hero.triggers.size() == 0 // its power only
		_next_turn()
			_attack(blu, p2.hero)
			assert p2.hero.is_dead()
	}
	
	@Test
	public void IceBlock_non_fatal_damage() {
		// Secret: When your hero takes fatal damage, prevent it and become Immune this turn
		def ice = _play("Ice Block")
		assert p1.hero.triggers.size() == 1 // when_a_hero_takes_damage
		p1.hero.health = 4
		assert p1.secrets.size() == 1
		assert p1.secrets.contains(ice)
		assert ice.is_revealed() == false
		_next_turn()
			def blu = _play("Bluegill Warrior")
			_attack(blu, p2.hero)
			assert p2.hero.health == 4 - blu.attack // 2
			assert p2.hero.has_buff(BuffType.IMMUNE) == false
			assert ice.is_revealed() == false
			assert p2.hero.triggers.size() == 1 // when_a_hero_takes_damage
	}

	@Test
	public void IceBlock_non_fatal_then_fatal_damage() {
		// Secret: When your hero takes fatal damage, prevent it and become Immune this turn
		def ice = _play("Ice Block")
		p1.hero.health = 4
		_next_turn()
			def blu = _play("Bluegill Warrior")
			_attack(blu, p2.hero) // non fatal damage
			def blu2 = _play("Bluegill Warrior")
			_attack(blu2, p2.hero) // fatal damage
			assert p2.hero.health == 4 - blu.attack // 2
			assert p2.hero.has_buff(BuffType.IMMUNE) == true
			assert ice.is_revealed() == true
			assert p2.hero.triggers.size() == 1 // when_a_hero_takes_damage
		_next_turn()
		assert p1.hero.has_buff(BuffType.IMMUNE) == false
		assert p1.hero.triggers.size() == 0 
	}
	
	@Test
	public void KirinTorMage_2_secrets() {
		// Battlecry: The next Secret you play this turn costs (0)
		def csp = g.new_card("Counterspell")
		p1.hand.add(csp)
		assert csp.get_cost() == csp.cost // =3
		_play("Kirin Tor Mage")
		p1.available_mana = 1
		p1.play(csp)
		assert csp.cost == 0
		assert p1.available_mana == 1 // should not have used mana
		def csp2 = g.new_card("Counterspell") // effect should work only for the first secret
		_should_fail("cost cannot be paid"){ p1.play(csp2) }
	}
	
	@Test
	public void KirinTorMage_play_no_secret() {
		// Battlecry: The next Secret you play this turn costs (0)
		_play("Kirin Tor Mage")
		_next_turn() // effect should be lost at end of turn
		_next_turn()
		def csp = g.new_card("Counterspell")
		p1.available_mana = 1 // not enough mana
		_should_fail("cost cannot be paid"){ p1.play(csp) }
	}
	
	@Test
	public void Malygos_play() {
		// Spell Damage +5
		def maly = _play("Malygos")
		assert maly.has_buff("Spell Damage +5")
		_play("Fireball", p2.hero)
		assert p2.hero.health == 30 -6 -5
	}
	
	@Test
	public void ManaWyrm_play() {
		// Whenever you cast a spell, gain +1 Attack
		def maw = _play("Mana Wyrm")
		assert maw.get_attack() == maw.attack
		_play("Fireball", p2.hero)
		assert maw.get_attack() == maw.attack + 1
		assert maw.has_buff("+1 Attack")
		_play("Explosive Trap")
		assert maw.get_attack() == maw.attack + 2
		_next_turn()
		
		// check effect triggers only for its controller
		_play("Counterspell")
		assert maw.get_attack() == maw.attack + 2
		_next_turn()
		
		// check that a countered spell still triggers the effect
		_play("Fireball", p2.hero) // should be countered
		assert maw.get_attack() == maw.attack + 3
		_next_turn()
		
		// check silence
		_play("Silence", maw)
		assert maw.get_attack() == maw.attack
		assert maw.has_buff("+1 Attack") == false
	}
	
	@Test
	public void MirrorEntity_play() {
		// Secret: When your opponent plays a minion, summon a copy of it
		
		// simple test
		_play("Mirror Entity") // create the secret
		_next_turn()
		_play("Elven Archer", p2.hero) // battlecry should deal 1 damage
		assert p2.hero.health == 29
		assert p1.minions.size() == 1 // copy, doesn't steal
		assert p2.minions.size() == 1 // should have a copy of Elven Archer
		assert p1.hero.health == 30	  // battlecry of copied minion should not trigger
		def a1 = p1.minions[0]
		def a2 = p2.minions[0]
		assert a1.id != a2.id
		assert a1.name == a2.name
		assert a1.attack == a2.attack
		assert a1.attack_counter == a2.attack_counter
		assert a1.buffs.size() == a2.buffs.size()
		assert a1.card_definition == a2.card_definition
		assert a1.controller != a2.controller
		assert a1.cost == a2.cost
		assert a1.creature_type == a2.creature_type
		assert a1.health == a2.health
		assert a1.is_a_secret == false
		assert a2.is_a_secret == false
		assert a1.is_attacking == false
		assert a2.is_attacking == false
		assert a1.is_being_played == false
		assert a2.is_being_played == false
		assert a1.is_destroyed == false
		assert a1.is_destroyed == false
		assert a1.is_enraged == false
		assert a2.is_enraged == false
		assert a1.is_in_play == true
		assert a2.is_in_play == true
		assert a1.just_summoned == true
		assert a2.just_summoned == true
		assert a1.max_health == a2.max_health
		assert a2.play_order > a1.play_order
		assert a1.target_type == a2.target_type
		assert a1.text == a2.text
		assert a1.triggers.size() == a2.triggers.size()
		assert a1.type == a2.type
		
		// test copied minion is a new object, not a reference on an existing object
		_play("Silence", a2)
		assert a2.text == ''
		assert a2.triggers.size() == 0
		assert a1.text != ''
		assert a1.triggers.size() > 0
		
		// test with buff
		_next_turn()
		_play("Mirror Entity") // create the secret
		p1.minions.clear()
		p2.minions.clear()
		_next_turn()
		_play("Shieldbearer") // has taunt
		assert p1.minions.size() == 1 // copy, doesn't steal
		assert p2.minions.size() == 1 // should have a copy of Elven Archer
		assert p1.minions[0].has_taunt()
		assert p2.minions[0].has_taunt()
		assert p1.minions[0].buffs.size() == p2.minions[0].buffs.size()
		_play("Silence", p2.minions[0]) // the copy
		assert p1.minions[0].has_taunt() == true
		assert p2.minions[0].has_taunt() == false
	}
	
	@Test
	public void MirrorImage_play() {
		// Summon two 0/2 minions with Taunt
		_play("Mirror Image")
		assert p1.minions.size() == 2
		[0,1].each {
			assert p1.minions[it].name == "Mirror Image"
			assert p1.minions[it].type == "minion"
			assert p1.minions[it].cost == 0
			assert p1.minions[it].attack == 0
			assert p1.minions[it].health == 2
			assert p1.minions[it].has_taunt()
		}
		_play("Mirror Image")
		assert p1.minions.size() == 4
		_play("Mirror Image")
		assert p1.minions.size() == 6
		_play("Mirror Image")
		assert p1.minions.size() == 7 // no space left
		_should_fail("no space left") { _play("Mirror Image") }
		assert p1.minions.size() == 7
	}
	
	@Test
	public void Polymorph_play() {
		//Transform a minion into a 1/1 Sheep
		def bou = _play("Boulderfist Ogre")
		_next_turn()
		_play("Polymorph", bou)
		assert p2.minions.find{it.name == "Sheep"} != null
		assert p2.minions.find{it.name == "Boulderfist Ogre"} == null
		assert bou.name == "Sheep"
		assert bou.cost == 0
		assert bou.attack == 1
		assert bou.health == 1
		assert bou.max_health == 1
		
		// Check silence has no effect
		_next_turn()
		_play("Silence", bou)
		assert bou.name == "Sheep"
		assert bou.cost == 0
		assert bou.attack == 1
		assert bou.health == 1
		assert bou.max_health == 1
		
		// check return to hand has no effect
		bou.return_to_hand()
		assert bou.name == "Sheep"
		assert bou.cost == 0
		assert bou.attack == 1
		assert bou.health == 1
		assert bou.max_health == 1
	}
	
	@Test
	public void Pyroblast_play() {
		// Deal 10 damage
		
		_play("Kobold Geomancer") // +1 Spell Damage
		_play("Pyroblast", p2.hero)
		assert p2.hero.health == 30 - 10 - 1
	}
	
	@Test
	public void Spellbender_play() {
		// Secret: When an enemy casts a spell on a minion, summon a 1/3 as the new target
		
		def mal = _play("Malygos")
		_play("Spellbender")
		assert p1.secrets.size() == 1
		
		_next_turn()
		_play("Sap", mal)	// should target a spellbender minion instead
		assert p2.secrets.size() == 0
		assert mal.is_in_play()
		
		def m = p2.hand.cards.find{it.name == "Spellbender" && it.is_a_minion()}
		assert m != null
		assert m.cost == 0
		assert m.attack == 1
		assert m.health == 3
	}
	
	@Test
	public void SorcerersApprentice_play() {
		// Your spells cost (1) less
		
		p1.hand.cards.clear()
		def spell1 = g.new_card("Fireball")
		def spell2 = g.new_card("Silence")		// cost==0 !
		def blu = g.new_card("Bluegill Warrior")
		p1.hand.add(spell1)
		p1.hand.add(spell2)
		p1.hand.add(blu)
		
		def sor = _play("Sorcerer's Apprentice")
		assert spell1.get_cost() == spell1.cost - 1
		assert spell2.get_cost() == 0
		assert blu.get_cost() == blu.cost // not a spell
		
		p2.hand.cards.clear()
		def spell3 = g.new_card("Assassinate")
		p2.hand.add(spell3)
		assert spell3.get_cost() == spell3.cost // not one of your spells
		
		_next_turn()
		_play("Silence", sor)
		assert spell1.get_cost() == spell1.cost
		assert spell2.get_cost() == spell2.cost
	}
	
	@Test
	public void Vaporize_play() {
		// 'Secret: When a minion attacks your hero, destroy it.'
		
		_play("Vaporize")
		_next_turn()
		
		def blu = _play("Bluegill Warrior")
		_attack(blu, p2.hero)
		assert blu.is_dead()
	}
	
	@Test
	public void WaterElemental_play() {
		// Freeze any character damaged by this minion
		
		def wat = _play("Water Elemental")	// 3/6
		_next_turn()
		
		def arg = _play("Argent Commander")	// 4/2 charge, divine shield
		assert arg.has_divine_shield() == true
		_attack(arg, wat)
		assert arg.is_frozen() == false
		assert arg.has_divine_shield() == false
		
		def rhi = _play("Tundra Rhino")		// 2/5 charge
		_attack(rhi, wat)
		assert rhi.is_frozen()
		assert wat.is_dead()
		_next_turn()
		
		def shb = _play("Shieldbearer")		// 0/4
		def wat2 = _play("Water Elemental")	// 3/6
		_next_turn()
		
		_play("Betrayal", wat2) // Force an enemy minion to deal its damage to the minions next to it
		assert shb.health == shb.max_health - wat.get_attack()
		assert shb.is_frozen()
		_next_turn()
		
		_attack(wat2, p2.hero)
		assert p2.hero.is_frozen()
		_next_turn()
		
		assert p1.hero.is_frozen()
		assert rhi.is_frozen() == false
		_next_turn()
		
		assert p2.hero.is_frozen() == false
	}

}
