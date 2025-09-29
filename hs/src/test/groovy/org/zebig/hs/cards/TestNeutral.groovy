package org.zebig.hs.cards

import org.zebig.hs.game.Card
import org.zebig.hs.game.JainaProudmoore

import static org.junit.Assert.fail
import static org.zebig.hs.mechanics.buffs.BuffType.*

import org.junit.Test

import org.zebig.hs.utils.TestHelper

class TestNeutral extends TestHelper {

	@Test
	void Abomination_play() {
		// Taunt. Deathrattle: Deal 2 damage to ALL characters.
		/*
		 * Test1 : play it, check it has TAUNT
		 */
		def abo = _play( "Abomination" )
		assert abo.has_buff(TAUNT)
	}

	@Test
	void Abomination_destroyed() {
		// Taunt. Deathrattle: Deal 2 damage to ALL characters.
		/*
		 * Test2 : destroy Abomination, all heroes have lost 2 health
		 */
		def abo = _play( "Abomination" )
		def h1 = p1.hero.get_health()
		def h2 = p2.hero.get_health()
		abo.dies()
		assert p1.hero.get_health() == h1-2
		assert p2.hero.get_health() == h2-2
	}


	@Test
	void AbusiveSergeant_play() {
		// 'Battlecry: Give a minion +2 Attack this turn.

		/*
		 * Test 1 : check battlecry effect
		 */
		def abo = _play("Abomination")
		_play_and_target("Abusive Sergeant", abo)
		assert abo.get_attack() == abo.card_definition.attack + 2
	}

	@Test
	void AbusiveSergeant_remove_end_of_turn_effect() {
		/*
		 * Test 2 : check effect removed at end of turn
		 */
		def abo = _play("Abomination")
		_play_and_target("Abusive Sergeant", abo)
		g.end_turn()
		assert abo.get_attack() == abo.card_definition.attack
	}

	@Test
	void Alexstrasza_play() {
		_play_and_target("Alexstrasza", p1.hero)
		assert p1.hero.get_health() == 15
	}

	@Test
	void AmaniBerserker_enrage_on() {
		// 'Enrage: +3 Attack.'

		// check enrage on effect
		def amb = _play("Amani Berserker")
		assert amb.get_attack() == amb.card_definition.attack
		_play_and_target( "Cruel Taskmaster", amb)
		/* check amb has now +5 Attack, 3 because enraged, 2 because of ctm */
		assert amb.is_enraged
		assert amb.has_buff("+2 Attack")
		assert amb.has_buff("+3 Attack")
		assert amb.get_attack() == amb.card_definition.attack + 5

		// check enrage off effect
		_play_and_target( "Ancestral Healing", amb) // restore health to full
		assert !amb.is_enraged
		assert amb.get_health() == amb.card_definition.max_health
		assert amb.get_attack() == amb.card_definition.attack + 2 // should have lost its +3 Attack
	}

	@Test
	void AmaniBerserker_enrage_off() {
		// 'Enrage: +3 Attack.'

		def amb = _play("Amani Berserker")
		assert amb.get_attack() == amb.card_definition.attack
		_play_and_target( "Cruel Taskmaster", amb)
		// check enrage off effect
		_play_and_target( "Ancestral Healing", amb) // restore health to full
		assert !amb.is_enraged
		assert amb.get_health() == amb.card_definition.max_health
		assert amb.get_attack() == amb.card_definition.attack + 2 // should have lost its +3 Attack
	}

	@Test
	void AncientBrewmaster_battlecry() {
		// Battlecry: Return a friendly minion from the battlefield to your hand
		def abo = _play("Abomination")
		def abm = _play_and_target("Ancient Brewmaster", abo)
		assert abm.is_in_play()
		assert !abo.is_in_play()
		assert p1.hand.contains(abo)
	}

	@Test
	void AncientBrewmaster_nominion() {
		// Battlecry: Return a friendly minion from the battlefield to your hand
		def abm = _play("Ancient Brewmaster")
		assert abm.is_in_play
	}

	@Test
	void AncientMage_play_alone() {
		def am = _play("Ancient Mage")
		assert !am.has_buff("Spell Damage +1")
	}

	@Test
	void AncientMage_play_1_neighbour() {
		def watcher = _play("Ancient Watcher")
		assert !watcher.has_buff("Spell Damage +1")
		def mage = _play("Ancient Mage")
		assert watcher.has_buff("Spell Damage +1")
		mage.leave_play()
		assert watcher.has_buff("Spell Damage +1")
	}

	@Test
	void AncientMage_play_2_neighbours() {
		def watcher = _play("Ancient Watcher")
		def gnome = _play("Leper Gnome")
		assert !watcher.has_buff("Spell Damage +1")
		assert !gnome.has_buff("Spell Damage +1")
		def mage = _play("Ancient Mage", 1)
		assert gnome.has_buff("Spell Damage +1")
		assert watcher.has_buff("Spell Damage +1")
		mage.leave_play()
		assert gnome.has_buff("Spell Damage +1")
		assert watcher.has_buff("Spell Damage +1")
	}

	@Test
	void AncientMage_play_2_neighbours_plus_one() {
		def watcher = _play("Ancient Watcher")
		def gnome = _play("Leper Gnome")
		def berserker = _play("Amani Berserker")
		assert !watcher.has_buff("Spell Damage +1")
		assert !gnome.has_buff("Spell Damage +1")
		def mage = _play("Ancient Mage", 1)
		assert gnome.has_buff("Spell Damage +1")
		assert watcher.has_buff("Spell Damage +1")
		assert !berserker.has_buff("Spell Damage +1")
		mage.leave_play()
		assert gnome.has_buff("Spell Damage +1")
		assert watcher.has_buff("Spell Damage +1")
	}

	@Test
	void AncientWatcher_attack_fail_because_cant_attack_buff() {
		def c = _play("Ancient Watcher") // text = "Can't attack"
		try {
			c.just_summoned = false
			g.player_attacks(c, g.passive_player.hero)
			fail("should not be possible for ${c.name} to attack because of can't attack buff")
		}
		catch (Exception e) {
			println e // ok
		}
	}

	@Test
	void AngryChicken_enraged() {
		// Enrage: +5 Attack
		def anc = _play("Angry Chicken") // 1/1
		p1.next_choices = ["Give your other minions +2/+2"]
		_play("Cenarius")
		assert anc.get_attack() == 3
		assert anc.health == 1
		assert anc.get_health() == 3
		assert anc.get_max_health() == 3
		_play_and_target( "Cruel Taskmaster", anc )
		assert anc.health == 0
		assert anc.get_health() == 2
		assert anc.is_enraged
		assert anc.get_attack() == 10 // = 1 + 2 (cen) + 5 (enraged) + 2 (ctm). wow.
	}

	@Test
	// Charge. Battlecry: Give your opponent a Mana Crystal
	void ArcaneGolem_play_give_1_crystal_to_opponent() {
		p2.max_mana = 3
		def arg = _play("Arcane Golem")
		assert p2.max_mana == 4
		assert arg.has_charge()
	}

	@Test
	// Charge. Battlecry: Give your opponent a Mana Crystal
	void ArcaneGolem_play_cannot_give_crystal() {
		p2.max_mana = 10
		_play("Arcane Golem")
		assert p2.max_mana == 10
	}

	@Test
	void Archmage_play() {
		// Spell Damage +1
		_play("Archmage")
		assert p1.get_spell_damage(1) == 2
	}

	@Test
	void ArgentSquire_survive_first_damage() {
		// has DIVINE_SHIELD
		Card arp = _play("Argent Squire")
		assert arp.has_buff(DIVINE_SHIELD)
		_play_and_target("Elven Archer", arp)
		assert !arp.is_dead()
		assert !arp.has_buff(DIVINE_SHIELD)
	}

	@Test
	void ArgentSquire_dies_second_damage() {
		// has DIVINE_SHIELD
		Card arp = _play("Argent Squire")
		assert arp.has_buff(DIVINE_SHIELD)
		_play_and_target("Elven Archer", arp)
		assert !arp.is_dead()
		assert !arp.has_buff(DIVINE_SHIELD)
		_play_and_target("Elven Archer", arp)
		assert arp.is_dead()
	}

	@Test
	void AzureDrake_backstab_deal_damage_plus_1() {
		// Spell Damage +1. Battlecry: Draw a card
		def abo = _play("Abomination")
		def before_hand_size = p1.hand.size()
		_play("Azure Drake") // gives spell damage +1, draw 1
		assert p1.hand.size() == before_hand_size +1
		_play_and_target("Backstab", abo) // Deal 2 damage to an undamaged minion
		assert abo.get_health() == abo.card_definition.max_health -3 // 3 instead of 2
	}

	@Test
	void Bananas_play() {
		// Give a minion +1/+1
		def blm = _play("Bloodmage Thalnos")
		_play("Bananas", blm)
		assert blm.has_buff("+1/+1")
	}

	@Test
	void BaronGeddon_end_of_turn_effect() {
		// At the end of your turn, deal 2 damage to ALL other characters
		def bge = _play("Baron Geddon")
		def abo = _play("Abomination")
		_next_turn()
		assert p1.hero.get_health() == 28
		assert p2.hero.get_health() == 28
		assert bge.get_health() == bge.card_definition.max_health
		assert abo.get_health() == abo.card_definition.max_health -2
		// no effect at end of opponent's turn
		_next_turn()
		assert p1.hero.get_health() == 28
		assert p2.hero.get_health() == 28
	}

	@Test
	void BigGameHunter_no_target() {
		// Battlecry: Destroy a minion with an Attack of 7 or more
		def arm = _play("Archmage" ) // attack = 4 only
		def bgh = _play("Big Game Hunter", arm)
		assert !arm.is_dead()
		assert bgh.is_in_play()
	}

	@Test
	void BaronRivendare_play() {
		/* Player A */
		def gnome = _play("Leper Gnome")
		def hoarder = _play("Loot Hoarder")
		def baron = _play("Baron Rivendare")
		_next_turn()

		/* Player B */
		def abo = _play("Abomination")
		_next_turn()

		/* Player A */
		_attack(gnome, abo)
		assert gnome.is_dead()
		assert p2.hero.health == 26
		def before_hand_size = p1.hand.size()

		_attack(hoarder, abo)
		assert hoarder.is_dead()
		assert abo.is_dead()
		assert p1.hand.size() == before_hand_size + 2
		assert p1.hero.health == 26 // damage dealt * 2 by abo
		assert p2.hero.health == 26 -4 // damage dealt * 2 by abo
		assert baron.health == 7-4
	}

	@Test
	void BigGameHunter_destroy() {
		// Battlecry: Destroy a minion with an Attack of 7 or more
		def ibp = _play("Ironbark Protector" ) // attack = 8
		def bgh = _play("Big Game Hunter", ibp)
		assert ibp.is_dead()
		assert bgh.is_in_play()
	}

	@Test
	void BloodmageThalnos_play() {
		// Spell Damage +1. Deathrattle: Draw a card.'
		def blm = _play("Bloodmage Thalnos")
		assert blm.has_buff("Spell Damage +1")
		_play("Fireball", p2.hero)
		assert p2.hero.health == 30 - (6 + 1)
	}

	@Test
	void BloodsailCorsair_play_no_weapon() {
		def corsair = _play("Bloodsail Corsair")
		assert corsair.is_in_play

	}

	@Test
	void BloodsailCorsair_play_your_weapon() {
		def axe = _play("Fiery War Axe")
		assert p1.hero.weapon != null
		assert p1.hero.weapon.durability == axe.card_definition.max_health
		def corsair = _play("Bloodsail Corsair") // should have no impact
		assert corsair.is_in_play
		assert p1.hero.weapon.durability == axe.card_definition.max_health
	}

	@Test
	void BloodsailCorsair_play_opponent_weapon() {
		/* Player A */
		def axe = _play("Fiery War Axe")
		_next_turn()

		/* Player B */
		_play("Bloodsail Corsair") // should reduce axe's durability
		assert p2.hero.weapon.durability == axe.card_definition.max_health -1
	}

	@Test
	void BloodsailCorsair_play_destroy_weapon() {
		/* Player A */
		def axe = _play("Fiery War Axe")
		_attack(p1.hero,p2.hero)
		assert p1.hero.weapon.durability == axe.card_definition.max_health -1 // because of attack
		_next_turn()

		/* Player B */
		_play("Bloodsail Corsair") // should reduce axe's durability to 0 => destroy
		assert p2.hero.weapon == null
	}

	@Test
	void BloodKnight_no_divine_shield() {
		// Battlecry: All minions lose Divine Shield. Gain +3/+3 for each Shield lost.'
		def blk = _play("Blood Knight")
		assert blk.get_attack() == blk.card_definition.attack
		assert blk.get_attack() == blk.card_definition.max_health
	}

	@Test
	void BloodKnight_1_divine_shield() {
		// Battlecry: All minions lose Divine Shield. Gain +3/+3 for each Shield lost.'
		def ars = _play("Argent Squire")
		assert ars.has_buff(DIVINE_SHIELD)
		def blk = _play("Blood Knight")
		assert !ars.has_buff(DIVINE_SHIELD)
		assert blk.get_attack() == blk.card_definition.attack + 3
		assert blk.get_health() == blk.card_definition.max_health + 3
	}

	@Test
	void BloodKnight_2_divine_shields() {
		// Battlecry: All minions lose Divine Shield. Gain +3/+3 for each Shield lost.'
		def ars1 = _play("Argent Squire")
		def ars2 = _play("Argent Squire")
		def blk = _play("Blood Knight")
		assert !ars1.has_buff(DIVINE_SHIELD)
		assert !ars2.has_buff(DIVINE_SHIELD)
		assert blk.get_attack() == blk.card_definition.attack + 6
		assert blk.get_health() == blk.card_definition.max_health + 6
	}

	@Test
	void BloodsailRaider_no_weapon() {
		// Battlecry: Gain Attack equal to the Attack of your weapon
		def bsr = _play("Bloodsail Raider")
		assert bsr.get_attack() == bsr.card_definition.attack
	}

	@Test
	void BloodsailRaider_with_weapon() {
		// Battlecry: Gain Attack equal to the Attack of your weapon
		p1.hero.equip_weapon(3, 2)
		def bsr = _play("Bloodsail Raider")
		assert bsr.get_attack() == bsr.card_definition.attack + 3
	}

	@Test
	void CairneBloodhoof_p1_destroyed() {
		// Deathrattle: Summon a 4/5 Baine Bloodhoof
		def cbh = _play("Cairne Bloodhoof")
		cbh.dies()
		assert p1.minions.size() == 1
		assert p1.minions[0].name == "Baine Bloodhoof"
	}

	@Test
	void CairneBloodhoof_p2_destroyed() {
		// Deathrattle: Summon a 4/5 Baine Bloodhoof
		def cbh = _play("Cairne Bloodhoof")
		_next_turn()
		_play("Assassinate", cbh)
		assert p2.minions.size() == 1
		assert p2.minions[0].name == "Baine Bloodhoof"
	}

	@Test
	void CaptainGreenskin_play_no_weapon() {
		def greenskin = _play("Captain Greenskin")
		assert greenskin.is_in_play
	}

	@Test
	void CaptainGreenskin_play_with_weapon() {
		_play("Fiery War Axe")
		assert p1.hero.weapon != null
		assert p1.hero.weapon.attack == 3
		assert p1.hero.weapon.durability == 2
		def greenskin = _play("Captain Greenskin")
		assert greenskin.is_in_play
		assert p1.hero.weapon.attack == 4
		assert p1.hero.weapon.durability == 3
		_attack(p1.hero, p2.hero)
		assert p2.hero.health == 26
	}

	@Test
	void CaptainsParrot_no_pirate_in_deck() {
		// Battlecry: Put a random Pirate from your deck into your hand
		assert p1.deck.cards.findAll{it.creature_type == "pirate"}.size() == 0
		def before_hand_size = p1.hand.size()
		_play("Captain's Parrot")
		assert p1.hand.size() == before_hand_size
	}

	@Test
	void CaptainsParrot_pirate_in_deck() {
		// Battlecry: Put a random Pirate from your deck into your hand
		def pirate_card = g.new_card("Bloodsail Raider")
		p1.deck.cards.add(0,pirate_card)
		def before_hand_size = p1.hand.size()
		_play("Captain's Parrot")
		assert p1.hand.size() == before_hand_size + 1
		assert p1.hand.contains(pirate_card)
		p1.available_mana = 6
		p1.play(pirate_card)

		// Test Mark of the Wild
		assert !pirate_card.has_buff("+2/+2")
		assert !pirate_card.has_buff(TAUNT)
		_play("Mark of the Wild", pirate_card)
		assert pirate_card.has_buff("+2/+2")
		assert pirate_card.has_buff(TAUNT)

		// Test Naturalize
		_next_turn()
		before_hand_size = p2.hand.size()
		_play("Naturalize", pirate_card)
		assert pirate_card.is_dead()
		assert p2.hand.size() == before_hand_size + 2
	}

	@Test
	void ColdlightOracle_play() {
		// Battlecry: Each player draws 2 cards

		def bhs1 = p1.hand.size()
		def bhs2 = p2.hand.size()
		_play("Coldlight Oracle")
		assert p1.hand.size() == bhs1 + 2
		assert p2.hand.size() == bhs2 + 2
	}

	@Test
	void ColdlightSeer_1_murloc() {
		// Battlecry: Give ALL other Murlocs +2 Health.
		def clo = _play("Coldlight Oracle") 		// murloc for p1
		def abo = _play("Abomination")	    		// non-murloc for p1
		def clo2 = _play("Coldlight Oracle", p2) 	// murloc for p2
		def abo2 = _play("Abomination", p2)	    	// non-murloc for p2
		def cls = _play("Coldlight Seer")
		assert clo.get_health() == clo.card_definition.max_health + 2
		assert abo.get_health() == abo.card_definition.max_health
		assert clo2.get_health() == clo2.card_definition.max_health + 2
		assert abo2.get_health() == abo2.card_definition.max_health
		assert cls.get_health() == cls.card_definition.max_health
	}

	@Test
	void CrazedAlchemist_play_no_buff() {
		// Battlecry: Swap the Attack and Health of a minion
		def lep = _play("Leper Gnome") // 2/1
		assert lep.get_attack() != lep.get_health()
		_play_and_target( "Crazed Alchemist", lep ) // 1/2
		assert lep.get_attack() == 1
		assert lep.get_max_health() == 2
		assert lep.get_health() == 2
	}

	@Test
	void CrazedAlchemist_play_add_attack_buff() {
		// Battlecry: Swap the Attack and Health of a minion
		def lep = _play("Leper Gnome") // 2/1
		_play_and_target("Abusive Sergeant", lep) // 4/1
		assert lep.get_attack() == 4
		assert lep.get_health() == 1
		_play_and_target( "Crazed Alchemist", lep )
		assert lep.get_attack() == 3 // 1 + 2 for buff
		assert lep.get_health() == 4
		assert lep.get_max_health() == 4
	}

	@Test
	void CrazedAlchemist_play_add_attack_buff_end_turn() {
		// Battlecry: Swap the Attack and Health of a minion
		def lep = _play("Leper Gnome") // 2/1
		_play_and_target("Abusive Sergeant", lep) // 4/1
		assert lep.get_attack() == 4
		assert lep.get_health() == 1
		_play_and_target( "Crazed Alchemist", lep )
		assert lep.get_attack() == 3 // 1 + 2 for buff
		assert lep.get_health() == 4
		assert lep.get_max_health() == 4
		_next_turn()
		assert lep.get_attack() == 1
		assert lep.get_health() == 4
		assert lep.get_max_health() == 4
	}

	@Test
	void CrazedAlchemist_enrage_off() {
		// Battlecry: Swap the Attack and Health of a minion
		def amb = _play("Amani Berserker") // 2/3
		_play_and_target("Cruel Taskmaster", amb) // 7/2
		assert amb.get_attack() == 7
		assert amb.get_health() == 2
		assert amb.get_max_health() == 3
		assert amb.is_enraged
		_play_and_target( "Crazed Alchemist", amb ) // 2(+2)/7
		assert !amb.is_enraged
		assert amb.get_health() == 7
		assert amb.get_max_health() == 7
		assert amb.get_attack() == 4 // 2 + 2 no more enrage buff
		_next_turn()
		_play_and_target( "Silence", amb )
		assert amb.get_attack() == 2
		assert amb.get_health() == 7
		assert amb.get_max_health() == 7
	}

	@Test
	void CultMaster_1_friendly_m_dies() {
		// Whenever one of your other minions dies, draw a card
		def bou = _play("Boulderfist Ogre")
		_next_turn()
		_play("Cult Master")
		def blu = _play("Bluegill Warrior")
		def before_hand_size = p1.hand.size()
		g.player_attacks(blu, bou)
		assert blu.is_dead()
		assert p1.hand.size() == before_hand_size + 1
	}

	@Test
	void CultMaster_2_friendly_m_dies() {
		// Whenever one of your other minions dies, draw a card
		def bou = _play("Boulderfist Ogre")
		_next_turn()
		_play("Cult Master")
		def blu1 = _play("Bluegill Warrior")
		def blu2 = _play("Bluegill Warrior")
		def before_hand_size = p1.hand.size()
		g.player_attacks(blu1, bou)
		g.player_attacks(blu2, bou)
		assert blu1.is_dead()
		assert blu2.is_dead()
		assert p1.hand.size() == before_hand_size + 2
	}

	@Test
	void CultMaster_dies_no_draw() {
		// Whenever one of your other minions dies, draw a card
		def cul = _play("Cult Master")
		_next_turn()
		def bou = _play("Boulderfist Ogre")
		_next_turn()
		def before_hand_size = p1.hand.size()
		g.player_attacks(cul, bou)
		assert cul.is_dead()
		assert p1.hand.size() == before_hand_size // only draw when OTHER minions die
	}

	@Test
	void CultMaster_enemy_minion_dies_no_draw() {
		// Whenever one of your other minions dies, draw a card
		_play("Cult Master")
		def bou = _play("Boulderfist Ogre")
		_next_turn()
		def blu = _play("Bluegill Warrior")
		def before_hand_size1 = p1.hand.size()
		def before_hand_size2 = p2.hand.size()
		g.player_attacks(blu, bou)
		assert blu.is_dead()
		assert p1.hand.size() == before_hand_size1
		assert p2.hand.size() == before_hand_size2
	}

	@Test
	void DancingSwords_play() {
		/* Player A */
		p1.hand.cards.clear()
		_next_turn()

		/* Player B */
		def ds = _play("Dancing Swords")
		assert p2.hand.size() == 0
		ds.dies()
		assert p2.hand.size() == 1
	}

	@Test
	void DarkIronDwarf_no_target() {
		// Battlecry: Give a minion +2 Attack this turn.
		def dar = _play("Dark Iron Dwarf")
		assert dar.cost == 4
		assert dar.attack == 4
		assert dar.health == 4
	}

	@Test
	void DarkIronDwarf_target() {
		// Battlecry: Give a minion +2 Attack this turn.
		def blu = _play("Bluegill Warrior")		// 2/1
		_play("Dark Iron Dwarf", blu)	// +2 ATT
		g.player_attacks(blu, p2.hero)
		assert p2.hero.health == 30 -2 -2
	}

	@Test
	void DarkIronDwarf_end_of_turn() {
		// Battlecry: Give a minion +2 Attack this turn.
		def blu = _play("Bluegill Warrior")		// 2/1
		_play("Dark Iron Dwarf", blu)	// +2 ATT
		g.player_attacks(blu, p2.hero)
		_next_turn()
		_next_turn()
		g.player_attacks(blu, p2.hero)
		assert p2.hero.health == 30 -2 -2 -2
	}

	@Test
	void DarkscaleHealer_play() {
		// Battlecry: Restore 2 Health to all friendly characters
		def abo = _play("Abomination")
		def kob = _play("Kobold Geomancer")
		def tau = _play("Tauren Warrior")
		_play("Whirlwind")
		assert tau.get_attack() == 2+3
		assert tau.is_enraged
		assert p1.hero.health == 30
		p1.hero.health = 29
		def dar = _play("Darkscale Healer")
		assert dar.cost == 5
		assert dar.attack == 4
		assert dar.health == 5
		assert abo.get_health() == abo.card_definition.max_health
		assert kob.get_health() == kob.card_definition.max_health
		assert tau.get_health() == tau.card_definition.max_health
		assert !tau.is_enraged
		assert p1.hero.health == 30
	}

	@Test
	void Deathlord_play() {
		def dt = _play("Deathlord")
		def before_deck_size = p2.deck.size()
		assert p2.minions.size() == 0
		dt.dies()
		assert p2.deck.size() == before_deck_size -1
		assert p2.minions.size() == 1
	}


	@Test
	void Deathwing_battlecry() {
		// Battlecry: Destroy all other minions and discard your hand
		def lep = _play("Leper Gnome")
		_next_turn()
		def fae = _play("Faerie Dragon")
		_play("Deathwing")
		assert lep.is_dead()
		assert fae.is_dead()
		assert p1.hand.size() == 0
		assert p2.hand.size() > 0
	}

	@Test
	void AcidicSwampOoze_destroys_weapon() {

		/*
		 * Test1 : test Battlecry: Destroy your opponent's weapon
		 */
		p2.hero.equip_weapon(2, 2)
		assert p2.hero.weapon != null
		_play("Acidic Swamp Ooze")
		assert p2.hero.weapon == null

	}

	@Test
	void AcidicSwampOoze_with_no_weapon() {

		/*
		 * Test2 : test with no opponent weapon
		 */
		_play("Acidic Swamp Ooze") // no exception
	}

	@Test
	void AcolyteOfPain_play_gets_damage() {

		/* test case:
		 * - abo is pushed on the deck (next to draw)
		 * - aop is in play
		 * - ctm is played, deals 1 damage to aop
		 * - abo is drawed
		 */
		def abo = g.new_card("Abomination")
		p1.deck.add(abo)
		def aop = _play("Acolyte of pain")
		g.active_player.next_choices = [aop]
		def before_attack = aop.get_attack()
		def before_hand_size = p1.hand.size()
		def before_deck_size = p1.deck.size()
		_play_and_target("Cruel Taskmaster", aop)
		assert aop.has_buff("+2 attack")
		assert aop.get_attack() == before_attack + 2
		assert p1.hand.size() == before_hand_size + 1
		assert p1.deck.size() == before_deck_size - 1
		assert p1.hand.contains(abo)
	}


	/* Alarm-o-Bot
	 * At the start of your turn, swap this minion with a random one in your hand.
	 * Note: this effects happens before you draw a card
	 * 
	 */

	@Test
	void AlarmOBot_start_turn_with_minion_in_hand() {
		/* test 1:
		 * - put aob in play for p2
		 * - put abo in p2's hand
		 * - end p1's turn()
		 * - start p2's turn
		 * => abo should be put in play
		 * => aob should not be in play but in p2's hand
		 */
		def aob = _play("Alarm-O-Bot")
		def abo = g.new_card("Abomination")
		p1.hand.cards.clear()
		p1.deck.cards.add(0,abo) // will be drawed
		_next_turn()
		_next_turn() // aob should not swap with abo
		assert !p1.hand.contains(aob)
		assert !p1.minions.contains(abo)
		assert p1.hand.contains(abo)
		assert p1.minions.contains(aob)
		_next_turn()
		_next_turn() // abo should swap with aob
		assert p1.hand.contains(aob)
		assert p1.minions.contains(abo)
		assert !p1.hand.contains(abo)
		assert !p1.minions.contains(aob)

	}

	@Test
	void AlarmOBot_start_turn_with_no_minion_in_hand() {
		/* test 2:
		 * - put aob in play for p2
		 * - empty p2's hand
		 * - push a non-minion Card on p2's deck (will be drawed)
		 * - end p1's turn()
		 * - start p2's turn
		 * => aob should still be in play
		 */
		def aob = _play("Alarm-O-Bot")
		_next_turn()
		assert p2.minions.contains(aob)
		p2.hand.cards.clear()
		def exe = g.new_card("Execute")
		p2.deck.add(exe) // for the next draw
		g.next_turn()
		assert p2.hand.contains(exe)
		assert p2.minions.contains(aob)
	}

	@Test
	void DefenderOfArgus_no_neighbor() {
		_play("Defender of Argus") // no exception
	}

	@Test
	void DefenderOfArgus_1_neighbor_right() {
		// Battlecry: Give adjacent minions +1/+1 and Taunt
		def lep = _play("Leper Gnome")
		_play("Defender of Argus", 0) // lep should have +1/+1 and taunt
		assert lep.has_buff("+1/+1")
		assert lep.has_buff(TAUNT)
	}

	@Test
	void DefenderOfArgus_2_neighbors_right() {
		// Battlecry: Give adjacent minions +1/+1 and Taunt
		def lep = _play("Leper Gnome")
		def blu = _play("Bluegill Warrior")
		_play("Defender of Argus", 0) // only lep should have +1/+1 and taunt
		assert lep.has_buff("+1/+1")
		assert lep.has_buff(TAUNT)
		assert !blu.has_buff("+1/+1")
		assert !blu.has_buff(TAUNT)
	}

	@Test
	void DefenderOfArgus_2_neighbors_left_right() {
		// Battlecry: Give adjacent minions +1/+1 and Taunt
		def lep = _play("Leper Gnome")
		def blu = _play("Bluegill Warrior")
		_play("Defender of Argus", 1) // inserted between lep and blu, both should have +1/+1 and taunt
		assert lep.has_buff("+1/+1")
		assert lep.has_buff(TAUNT)
		assert blu.has_buff("+1/+1")
		assert blu.has_buff(TAUNT)
	}

	@Test
	void DireWolfAlpha_no_neighbor() {
		// Adjacent minions have +1 Attack
		_play("Dire Wolf Alpha") // no exception
	}

	@Test
	void DireWolfAlpha_right_neighbor() {
		// Adjacent minions have +1 Attack
		_play("Dire Wolf Alpha")
		def lep = _play("Leper Gnome")
		assert lep.get_attack() == lep.card_definition.attack + 1
		def abo = _play("Abomination") // not adjacent
		assert abo.get_attack() == abo.card_definition.attack
	}

	@Test
	void Demolisher_next_turn() {
		// At the start of your turn, deal 2 damage to a random enemy
		_play("Demolisher")
		_next_turn()
		_next_turn()
		assert p2.hero.get_health() == 28
	}

	@Test
	void DireWolfAlpha_left_right_neighbor() {
		// Adjacent minions have +1 Attack
		def lep = _play("Leper Gnome")
		_play("Dire Wolf Alpha")
		def abo = _play("Abomination")
		assert lep.get_attack() == lep.card_definition.attack + 1
		assert abo.get_attack() == abo.card_definition.attack + 1
	}

	@Test
	void Doomsayer_play() {
		// At the start of your turn, destroy ALL minions
		def blu = _play("Bluegill Warrior")
		def dos = _play("Doomsayer")
		_next_turn()
		def lep = _play("Leper Gnome")
		_next_turn()
		assert blu.is_dead()
		assert dos.is_dead()
		assert lep.is_dead()
	}

	@Test
	void DragonlingMechanic_play() {
		// Battlecry: Summon a 2/1 Mechanical Dragonling
		def dlm = _play("Dragonling Mechanic")
		assert dlm.is_in_play()
		assert p1.minions.size() == 2
		def mdl = p1.minions.find{it.name == "Mechanical Dragonling"}
		assert mdl != null
		assert mdl.get_attack() == 2
		assert mdl.get_health() == 1
	}

	@Test
	void DreadCorsair_play_no_weapon() {
		// Taunt. Costs (1) less per Attack of your weapon
		def dco = g.new_card("Dread Corsair")
		p1.hand.add(dco)
		assert dco.get_cost() == dco.card_definition.cost // 4
	}

	@Test
	void DreadCorsair_play_with_weapon() {
		// Taunt. Costs (1) less per Attack of your weapon
		p1.hero.equip_weapon(3, 2)
		def dco = g.new_card("Dread Corsair")
		p1.hand.add(dco)
		assert dco.get_cost() == dco.card_definition.cost - 3 // 4 - 3
	}

	@Test
	void EdwinVanCleef_play_no_combo() {
		// Combo: Gain +2/+2 for each other card played earlier this turn
		def edw = _play("Edwin VanCleef")
		assert edw.buffs.size() == 0
	}

	@Test
	void EchoingOoze_play() {
		_play("Sword of Justice")
		def echo1 = _play("Echoing Ooze")
		assert echo1.get_attack() == echo1.attack + 1
		assert echo1.get_health() == echo1.health + 1
		_next_turn()

		assert p2.minions.size() == 2
		def echo2 = p2.minions[1]
		assert echo2.get_attack() == echo2.attack + 2
		assert echo2.get_health() == echo2.health + 2
	}

	@Test
	void EdwinVanCleef_play_combo1() {
		// Combo: Gain +2/+2 for each other card played earlier this turn
		_play("Bluegill Warrior")
		def edw = _play("Edwin VanCleef")
		assert edw.buffs.size() == 1
		assert edw.buffs[0].toString() == "+2/+2"
	}

	@Test
	void EdwinVanCleef_play_combo2() {
		// Combo: Gain +2/+2 for each other card played earlier this turn
		_play("Bluegill Warrior")
		_play("Leper Gnome")
		def edw = _play("Edwin VanCleef")
		assert edw.buffs.size() == 1
		assert edw.buffs[0].toString() == "+4/+4"
	}

	@Test
	void EmperorCobra_defends() {
		// Destroy any minion damaged by this minion
		def emp = _play("Emperor Cobra")	// 2/3
		_next_turn()
		def kor = _play("Kor'kron Elite")	// 4/3
		g.player_attacks(kor, emp)
		assert emp.is_dead()
		assert kor.is_dead()	// should have survived otherwise
	}

	@Test
	void Emboldener3000_play() {
		def embo = g.summon(p1, "Emboldener 3000")
		_next_turn()
		assert embo.get_attack() == embo.attack + 1
		assert embo.get_health() == embo.health + 1
	}

	@Test
	void EmperorCobra_attacks() {
		// Destroy any minion damaged by this minion
		def emp = _play("Emperor Cobra")	// 2/3
		_next_turn()
		def kor = _play("Kor'kron Elite")	// 4/3
		g.player_attacks(kor, p2.hero)
		_next_turn()
		g.player_attacks(emp, kor)
		assert emp.is_dead()
		assert kor.is_dead()	// should have survived otherwise
	}

	@Test
	void EmperorCobra_betrayal() {
		// Destroy any minion damaged by this minion
		def bf1 = _play("Boulderfist Ogre")
		def emp = _play("Emperor Cobra")	// 2/3
		def bf2 = _play("Boulderfist Ogre")
		_next_turn()
		_play("Betrayal", emp) // huh huh
		assert bf1.is_dead()
		assert bf2.is_dead()
	}

	@Test
	void EarthenRingFarseer_play() {
		// Battlecry: Restore 3 Health
		p1.hero.set_health(3)
		_play("Earthen Ring Farseer", p1.hero)
		assert p1.hero.get_health() == 3+3
		def bou = _play("Boulderfist Ogre")
		bou.set_health(4)
		_play("Earthen Ring Farseer", bou)
		assert bou.get_health() == bou.card_definition.max_health // 6
	}

	@Test
	void FacelessManipulator_play() {
		/* Player A */
		def echo1 = _play("Echoing Ooze")
		_play("Blessing of Might", echo1)	/* +3 attack */
		assert echo1.get_attack() == 4
		assert echo1.get_health() == 2
		_play("Blessing of Kings", echo1)	/* +4/+4 */
		assert echo1.get_attack() == 8
		assert echo1.get_health() == 6
		_next_turn()

		/* Player B */
		def fm = _play("Faceless Manipulator", echo1)
		assert fm.name == "Echoing Ooze"
		assert fm.attack == echo1.attack
		assert fm.health == echo1.health
		assert fm.get_attack() == echo1.get_attack()
		assert fm.get_health() == echo1.get_health()
		_next_turn()

		assert p1.minions.size() == 2
		assert p2.minions.size() == 1
	}

	@Test
	void FacelessManipulator_no_copy() {
		/* Player A */
		_play("Ravenholdt Assassin")
		_next_turn()

		/* Player B */
		def fm = _play("Faceless Manipulator")
		assert fm.name == "Faceless Manipulator"
	}

	@Test
	void Feugen_play_no_thaddius() {
		def feugen = _play('Feugen')
		assert !g.stalagg_died
		feugen.dies()
		assert g.feugen_died
		assert p1.minions.find { it.name == 'Thaddius'} == null
	}

	@Test
	void Feugen_play_thaddius() {
		g.stalagg_died = true
		def feugen = _play('Feugen')
		feugen.dies()
		assert p1.minions.find { it.name == 'Thaddius'} != null
	}

	@Test
	void FlesheatingGhoul_play() {
		// Whenever a minion dies, gain +1 Attack
		def flg = _play("Flesheating Ghoul")
		def fae = _play("Faerie Dragon")
		def abo = _play("Abomination")
		_next_turn()
		def lep = _play("Leper Gnome")
		def bou = _play("Boulderfist Ogre")
		_next_turn()
		_attack(abo, bou) // should kill abo, deathrattle deals 2D to all others
		assert abo.is_dead()
		assert !bou.is_dead()
		assert bou.health == bou.max_health - abo.attack - 2
		assert fae.is_dead()
		assert lep.is_dead()
		assert !flg.is_dead()
		assert flg.attack == 2
		assert flg.get_attack() == 2 + 1 + 1 +1
	}

	@Test
	void FrostwolfGrunt_play() {
		def fwg = _play("Frostwolf Grunt")
		assert fwg.has_taunt()
	}

	@Test
	void FrostwolfWarlord_play() {
		// Battlecry: Gain +1/+1 for each other friendly minion on the battlefield
		_play("Elven Archer", p2.hero)
		_next_turn()
		_play("Leper Gnome") 		// should give +1/+1
		_play("Frostwolf Grunt")	// should give +1/+1
		def fww = _play("Frostwolf Warlord")
		assert fww.get_attack() == fww.attack + 2
		assert fww.get_health() == fww.health + 2
	}


	@Test
	void JainaPower_on_Faerie_Dragon_fail() {
		// Can't be targeted by Spells or Hero Powers

		// give Jaina's power to p1 hero
		p1.hero = new JainaProudmoore(g)
		def fae = _play("Faerie Dragon")
		p1.next_choices = [fae]
		_should_fail("not a valid choice") { _use_hero_power() }
	}

	@Test
	void Elven_Archer_on_Faerie_Dragon_ok() {
		// Can't be targeted by Spells or Hero Powers
		def fae = _play("Faerie Dragon")
		_next_turn()
		// fae is not protected against battlecry effects
		_play_and_target("Elven Archer", fae)
		assert fae.health == fae.card_definition.max_health -1
	}

	@Test
	void Assassinate_on_Faerie_Dragon_fails() {
		// Can't be targeted by Spells or Hero Powers
		def fae = _play("Faerie Dragon")
		_next_turn()
		try {
			_play_and_target("Assassinate", fae)
			fail("should have failed: not a valid target")
		}
		catch (Exception e) {
			println e // ok
		}
	}

	@Test
	void GadgetzanAuctioneer_play() {
		// When you cast a spell, draw a card
		def before_hand_size = p1.hand.size()
		_play("Gadgetzan Auctioneer")
		_play("Counterspell")
		assert p1.hand.size() == before_hand_size + 1
		_next_turn()
		_play("Explosive Trap") // should not give a card
		assert p2.hand.size() == before_hand_size + 1
	}

	@Test
	void GelbinMekkatorque_play() {
		_play("Gelbin Mekkatorque")
		assert p1.minions.size() == 2
		assert p1.minions[1].name in ['Repair Bot', 'Poultryizer', 'Homing Chicken', 'Emboldener 3000']
	}

	@Test
	void GnomishInventor_play() {
		// Battlecry: Draw a card
		def before_hand_size = p1.hand.size()
		_play("Gnomish Inventor")
		assert p1.hand.size() == before_hand_size + 1
	}

	@Test
	void GoldshireFootman_play() {
		// Taunt
		def gfm = _play("Goldshire Footman")
		assert gfm.has_taunt()
	}

	@Test
	void GrimscaleOracle_play() {
		// ALL other Murlocs have +1 Attack
		def blu = _play("Bluegill Warrior") // including opponent's murlocs
		def abo = _play("Abomination")
		_next_turn()
		def col = _play("Coldlight Oracle")
		def gri = _play("Grimscale Oracle")
		assert blu.get_attack() == blu.attack + 1
		assert col.get_attack() == col.attack + 1
		assert gri.get_attack() == gri.attack
		assert abo.get_attack() == abo.attack
	}

	@Test
	void Gruul_play() {
		def gruul = _play("Gruul")
		assert gruul.get_attack() == 7
		assert gruul.get_health() == 7
		_next_turn()
		assert gruul.get_attack() == 8
		assert gruul.get_health() == 8
		_next_turn()
		assert gruul.get_attack() == 9
		assert gruul.get_health() == 9
		_next_turn()
		assert gruul.get_attack() == 10
		assert gruul.get_health() == 10
	}

	@Test
	void GurubashiBerserker_play() {
		// Whenever this minion takes damage, gain +3 Attack.
		def abo = _play("Abomination")
		def blu = _play("Bluegill Warrior")
		_next_turn()
		def gur = _play("Gurubashi Berserker")	// 2/7
		_next_turn()
		g.player_attacks(abo, gur)
		assert gur.attack == 2
		assert gur.get_attack() == 5
		assert gur.get_health() == gur.card_definition.max_health - abo.attack
		assert abo.get_health() == 2 // damage dealt by GB before gain +3 ATT
		g.player_attacks(blu, gur)
		assert gur.get_attack() == 8
	}
	
	@Test
	void HarrisonJones_no_weapon() {
		def hj = _play('Harrison Jones')
		assert hj.is_in_play
	}

	@Test
	void HarrisonJones_with_weapon() {
		/* Player A */
		p1.hero.equip_weapon(2, 3)
		_next_turn()
		
		/* Player B */
		def bh = p1.hand.size()
		def hj = _play('Harrison Jones')
		assert hj.is_in_play
		assert p2.hero.weapon == null
		assert p1.hand.size() == bh + 3
	}
	
	@Test
	void HarvestGolem_play() {
		def golem = _play("Harvest Golem")
		golem.dies()
		assert p1.minions().size() == 1
		assert p1.minions[0].name == 'Damaged Golem'
		assert p1.minions[0].attack == 2
		assert p1.minions[0].health == 1
	}
	
	@Test
	void HauntedCreeper_play() {
		def creeper = _play("Haunted Creeper")
		creeper.dies()
		assert p1.minions.size() == 2
		p1.minions.each { Card c ->
			assert c.name == "Spectral Spider"
			assert c.attack == 1
			assert c.health == 1
		}
	}

	@Test
	void Hogger_play() {
		// 'At the end of your turn, summon a 2/2 Gnoll with Taunt
		def hog = _play("Hogger")
		_next_turn()
		assert p2.minions.size() == 2
		assert p2.minions[1].name == 'Gnoll'
		assert p2.minions[1].attack == 2
		assert p2.minions[1].health == 2
		assert p2.minions[1].max_health == 2
		assert p2.minions[1].has_buff(TAUNT)
		_play("Fireball", hog)
		assert hog.is_dead()
		_next_turn()
		_next_turn()
		// check end of turn effect has been removed
		assert p2.minions.size() == 1 // Gnoll
	}

	@Test
	void HomingChicken_play() {
		def chicken = g.summon(p1, "Homing Chicken")
		def before_hand_size = p1.hand.size()
		_next_turn()

		_next_turn()
		assert chicken.is_dead()
		assert p1.hand.size() == before_hand_size + 3 + 1
	}

	@Test
	void HomingChicken_hand_full() {
		def chicken = g.summon(p1, "Homing Chicken")
		def before_hand_size = p1.hand.size()
		p1.draw(10 - before_hand_size)
		_next_turn()

		_next_turn()
		assert chicken.is_dead()
		assert p1.hand.size() == 10
	}

	@Test
	void HungryCrab_play_no_murloc() {
		def crab = _play("Hungry Crab")
		assert crab.get_attack() == 1
		assert crab.get_health() == 2
	}
	
	@Test
	void HungryCrab_play_murloc() {
		def blu = _play("Bluegill Warrior")
		_attack(blu, p2.hero)
		def crab = _play("Hungry Crab", blu)
		assert blu.is_dead()
		assert crab.get_attack() == 3
		assert crab.get_health() == 4
	}
	
	@Test
	void IllidanStormrage_play() {
		// Whenever you play a card, summon a 2/1 Flame of Azzinoth
		_play("Illidan Stormrage")
		assert p1.minions.size() == 1
		_play_and_target("Ice Lance", p2.hero) // should summon a Flame
		assert p1.minions[1].name == "Flame of Azzinoth"
		assert p1.minions[1].attack == 2
		assert p1.minions[1].health == 1
		assert p1.minions.size() == 2
		_play_and_target("Ice Lance", p2.hero)
		assert p1.minions.size() == 3
	}

	@Test
	void ImpMaster_play() {
		def master = _play("Imp Master")
		_next_turn()
		assert master.health == 4
		assert p2.minions.size() == 2
		assert p2.minions[1].name == "Imp"
		assert p2.minions[1].attack == 1
		assert p2.minions[1].health == 1
		
		_next_turn()
		master.health = 1
		
		_next_turn()
		assert master.is_dead()
		assert p2.minions.size() == 2
	}
	
	@Test
	void InjuredBlademaster_comes_in_play() {
		// Battlecry: Deal 4 damage to HIMSELF
		def c = _play("Injured Blademaster")
		assert c.get_health() == c.card_definition.max_health - 4
		assert c.max_health == c.card_definition.max_health
		assert c.is_enraged
	}

	@Test
	void IronbeakOwl_battlecry_no_minion() {
		// Battlecry: Silence a minion
		_play("Ironbeak Owl")
	}

	@Test
	void IronbeakOwl_battlecry_faerie() {
		// Battlecry: Silence a minion
		def fae = _play("Faerie Dragon")
		assert fae.has_buff(CANNOT_BE_TARGETED_BY_SPELL_OR_POWER)
		_next_turn()
		_play_and_target("Ironbeak Owl", fae)
		assert !fae.has_buff(CANNOT_BE_TARGETED_BY_SPELL_OR_POWER)
	}

	@Test
	void IronforgeRifleman_play() {
		// Battlecry: Deal 1 damage
		def ela = _play("Elven Archer", p2.hero)
		_next_turn()
		_play("Ironforge Rifleman", ela)
		assert ela.is_dead()
	}

	@Test
	void IronfurGrizzly_play() {
		// Taunt
		def griz = _play("Ironfur Grizzly")
		assert griz.has_taunt()
	}

	@Test
	void JunglePanther_play() {
		// Taunt
		def jpan = _play("Jungle Panther")
		assert jpan.has_stealth()
	}
	
	@Test
	void KelThuzad_play() {
		/* Player A */
		def abo = _play("Abomination")
		_next_turn()
		
		/* Player B */
		def blu = _play("Bluegill Warrior")
		def rider = _play("Wolfrider")
		def kel = _play("Kel'Thuzad")
		_attack(blu, abo)
		_attack(rider, abo)
		assert blu.is_dead()
		assert rider.is_dead()
		assert abo.is_dead()
		assert !kel.is_dead()
		_next_turn()
		
		assert p2.minions.size() == 3
		assert p2.minions.findAll { it.name == 'Bluegill Warrior' } != null
		assert p2.minions.findAll { it.name == 'Wolfrider' } != null
		assert abo.is_dead()
	}

	@Test
	void KingMukla_play() {
		// Battlecry: Give your opponent 2 Bananas
		assert p2.hand.cards.findAll{it.name == "Bananas"}.size() == 0
		_play("KingMukla")
		assert p2.hand.cards.findAll{it.name == "Bananas"}.size() == 2
	}

	@Test
	void KnifeJuggler_play() {
		// After you summon a minion, deal 1 damage to a random enemy
		_play("Knife Juggler")
		_play("Elven Archer", p2.hero) // should actually deal 2 damage to hero
		assert p2.hero.health == 28
		_play("Explosive Trap") // not a minion, should have no effect
		assert p2.hero.health == 28
		_next_turn()
		_play("Leper Gnome") // minion played by opponent, should have no effect
		assert p1.hero.health == 28
		assert p2.hero.health == 30
	}

	@Test
	void LeeroyJenkins_play() {
		// Charge. Battlecry: Summon two 1/1 Whelps for your opponent.

		def lee = _play("Leeroy Jenkins")
		assert lee.has_buff(CHARGE)
		assert p2.minions.size() == 2
		assert p2.minions[0].name == "Whelp"
		assert p2.minions[0].attack == 1
		assert p2.minions[0].health == 1
		assert p2.minions[1].name == "Whelp"
		assert p2.minions[1].attack == 1
		assert p2.minions[1].health == 1

		_attack(lee, p2.hero)
		assert p2.hero.health == 24
	}

	@Test
	void LeperGnome_deathrattle() {
		// Deathrattle: Deal 2 damage to the enemy hero.
		_play("Leper Gnome").dies() // short lived...
		assert p2.hero.health == 28
	}

	@Test
	void Lightwarden_play() {
		// Whenever a character is healed, gain +2 Attack
		def lwa = _play("Lightwarden")	// 1/2
		p1.hero.health = 12
		def gua = _play("Guardian of Kings") // 5/6, Battlecry: Restore 6 Health to your hero
		assert p1.hero.health == 18
		assert lwa.attack == 1
		assert lwa.get_attack() == 3
		assert lwa.has_buff("+2 Attack")
		_next_turn()
		def bou = _play("Boulderfist Ogre") // 6/7
		_next_turn()
		_attack(gua, bou)
		assert gua.is_dead()
		assert bou.health == 2
		_play("Circle of Healing") // Restore 4 Health to ALL minions
		assert bou.health == 6
		assert lwa.get_attack() == 5
		_attack(lwa, p2.hero)
		assert p2.hero.health == 25
		_next_turn()
		_play("Silence", lwa)
		assert lwa.get_attack() == 1
		assert lwa.get_health() == 2
	}
	
	@Test
	void Loatheb_play() {
		_play("Loatheb")
		_next_turn()
		
		Card frostbolt = g.new_card("Frostbolt")
		p1.hand.add(frostbolt)
		assert frostbolt.get_cost() == 2 + 5
		_next_turn()
		
		_next_turn()
		assert frostbolt.get_cost() == 2	
	}

	@Test
	void LootHoarder_play() {
		// Deathrattle: Draw a card
		def loo1 = _play("Loot Hoarder")
		def loo2 = _play("Loot Hoarder")
		_next_turn()

		// check deathrattle
		def blu = _play("Bluegill Warrior")
		def before_hand_size = p2.hand.size()
		_attack(blu, loo1)		// kill loo1
		assert loo1.is_dead()
		assert p2.hand.size() == before_hand_size + 1

		// check silence removes effect
		def abo = _play("Abomination")
		_play("Silence", loo2) // should lose its deathrattle
		_next_turn()
		before_hand_size = p1.hand.size()
		_attack(loo2, abo)
		assert loo2.is_dead()
		assert p1.hand.size() == before_hand_size
	}

	@Test
	void LorewalkerCho_play() {
		// Whenever a player casts a spell, put a copy into the other player's hand

		// test when its controller casts a spell
		def cho = _play("Lorewalker Cho")
		p1.hand.cards.clear()
		p2.hand.cards.clear()
		_play("The Coin")
		assert p2.hand.size() == 1
		assert p2.hand.cards[0].name == "The Coin"
		_next_turn()

		// test when the opponent casts a spell
		_play("Silence", cho)
		assert p2.hand.size() == 1
		assert p2.hand.cards[0].name == "Silence"

		// check that silence removes the effect
		_play("Counterspell")
		assert p2.hand.size() == 1
		assert p2.hand.cards[0].name == "Silence"
	}

	@Test
	void MadBomber_play() {
		// Battlecry: Deal 3 damage randomly split between all other characters
		def kob = _play("Kobold Geomancer") // not clear what it does, assumes change to Deal 4 damage...

		def mad = _play("Mad Bomber")
		assert mad.health == mad.max_health // (funny name) should not receive damage

		def d1 = 30 - p1.hero.health
		def d2 = 30 - p2.hero.health
		def d3 = kob.is_dead() ? kob.max_health : kob.max_health - kob.health
		assert 4 == d1 + d2 + d3
	}
	
	@Test
	void MadScientist_play_secret() {
		p1.deck.cards.add(g.new_card("Counterspell")) // at least one secret in deck
		def nb_secrets = p1.deck.cards.findAll { Card c -> c.is_a_secret }.size() 
		def mad = _play("Mad Scientist")
		mad.dies()
		assert p1.secrets.size() == 1
		assert p1.deck.cards.findAll { Card c -> c.is_a_secret }.size() == nb_secrets - 1
	}

	@Test
	void MadScientist_play_no_secret() {
		def secrets = p1.deck.cards.findAll { Card c -> c.is_a_secret }
		secrets.each { p1.deck.cards.remove(it) } 
		def mad = _play("Mad Scientist")
		mad.dies()
	}

	@Test
	void ManaAddict_play() {
		// Whenever you cast a spell, gain +2 Attack this turn
		def maa = _play("Mana Addict")
		assert maa.get_attack() == 1
		_play("The Coin")
		assert maa.get_attack() == 3	// first spell
		_play("Explosive Trap")
		assert maa.get_attack() == 5	// second spell
		_next_turn()
		assert maa.get_attack() == 1
		_play("Counterspell") // not you => should not trigger effect
		assert maa.get_attack() == 1
		_next_turn()
		_play("Leper Gnome") // not a spell => should not trigger effect
		assert maa.get_attack() == 1
	}

	@Test
	void ManaWraith_play() {
		// ALL minions cost (1) more
		_play("Mana Wraith")
		def abo = g.new_card("Abomination")		// one of yours
		p1.hand.add(abo)
		assert abo.get_cost() == abo.cost + 1
		def kob = g.new_card("Kobold Geomancer")	// one of opponent's
		p2.hand.add(kob)
		assert kob.get_cost() == kob.cost + 1
		// test with another one
		_play("Mana Wraith")
		assert abo.get_cost() == abo.cost + 2
		assert kob.get_cost() == kob.cost + 2
		_next_turn()
		_play("Mana Wraith")
		assert abo.get_cost() == abo.cost + 3
		assert kob.get_cost() == kob.cost + 3
	}

	@Test
	void MasterSwordsmith_play() {
		// At the end of your turn, give another random friendly Minion +1 Attack
		def fae = _play("Faerie Dragon")

		// only 1 choice
		_play("Master Swordsmith")
		_next_turn()
		assert fae.has_buff("+1 Attack")
		assert fae.get_attack() == fae.attack + 1
		_next_turn()

		// 2 choices
		def lep = _play("Leper Gnome")
		_next_turn()
		if (fae.get_attack() == fae.attack + 1 ) { // lep should have been buffed
			assert lep.has_buff("+1 Attack")
		}
		else {
			assert fae.get_attack() == fae.attack + 2 // fae should have been buffed twice
			assert !lep.has_buff("+1 Attack")
		}
	}

	@Test
	void MillhouseManastorm_play() {
		// Battlecry: Enemy spells cost (0) next turn

		_play("Millhouse Manastorm")

		// check that Millhouse's controller is unaffected
		def c = g.new_card("Fireball")
		p1.hand.add(c)
		assert c.get_cost() > 0
		_next_turn()

		// check that get_cost() == 0 for opponent spell
		def c2 = g.new_card("Fireball")
		p1.hand.add(c2)
		assert c2.get_cost() == 0

		// check that non-spell cards are unaffected
		def m = g.new_card("Abomination") // minion
		p1.hand.add(m)
		assert m.get_cost() > 0

		// check that the spell can be played without mana
		p1.available_mana = 0
		p1.next_choices = [p2.hero]
		p1.play(c2)

		// and another spell (not just once)
		def c3 = g.new_card("Fireball")
		p1.hand.add(c3)
		p1.next_choices = [p2.hero]
		p1.play(c3)

		// check that effect stops at end of turn
		def c4 = g.new_card("Frost Nova")
		p1.hand.add(c4)
		assert c4.get_cost() == 0
		_next_turn() // Millhouse controller
		assert c4.get_cost() > 0
	}

	@Test
	void MindControlTech_play() {
		// Battlecry: If your opponent has 4 or more minions, take control of one at random

		// test #1 : opponent has only 3 minions, no effect
		_play("Leper Gnome")
		_play("Loot Hoarder")
		_play("Faerie Dragon")
		_next_turn()
		assert p2.minions.size() == 3
		def mct = _play("Mind Control Tech")
		assert mct.is_in_play()
		assert p2.minions.size() == 3

		// test #2 : opponent has 4 minions
		_next_turn()
		_play("Kobold Geomancer")
		_next_turn()
		assert p2.minions.size() == 4
		def mct2 = _play("Mind Control Tech")
		assert mct2.is_in_play()
		assert p1.minions.size() == 3	// mct1 + mct2 + controlled minion
		assert p2.minions.size() == 3
	}

	@Test
	void MoltenGiant_costs_14_less() {
		// Costs (1) less for each damage your hero has taken
		p1.hero.health = 16 	// 14 damage taken
		def mog = g.new_card("Molten Giant")
		p1.hand.add(mog)
		assert mog.get_cost() == mog.cost - 14  // 20 - 14
		p1.available_mana = 6
		p1.play(mog) // should not fail
		assert p1.available_mana == 0
	}

	@Test
	void MoltenGiant_costs_25_less() {
		// Costs (1) less for each damage your hero has taken
		p1.hero.health = 5 	// 25 damage taken
		def mog = g.new_card("Molten Giant")
		p1.hand.add(mog)
		assert mog.get_cost() == 0  // 20 - 25 with floor 0
		p1.available_mana = 0
		p1.play(mog) // should not fail
		assert p1.available_mana == 0
	}

	@Test
	void MountainGiant_play() {
		// Costs (1) less for each other card in your hand

		// no other card in hand
		def mtg = g.new_card("Mountain Giant")
		p1.hand.cards.clear()
		p1.hand.add(mtg)
		assert mtg.get_cost() == mtg.cost

		// 4 other cards in hand
		p1.hand.add(g.new_card("Shieldbearer"))
		p1.hand.add(g.new_card("Leper Gnome"))
		p1.hand.add(g.new_card("Molten Giant"))
		p1.hand.add(g.new_card("Execute"))
		assert mtg.get_cost() == mtg.cost -4
	}

	@Test
	void MurlocTidecaller_play() {
		// Whenever a Murloc is summoned, gain +1 Attack.
		def mtc = _play("Murloc Tidecaller")
		_play("Murloc Tidehunter") // Summon a 1/1 Murloc Scout => should give +2 A
		assert mtc.get_attack() == mtc.attack + 2
		// should work also when the opponent summons a murloc
		_next_turn()
		_play("Bluegill Warrior")
		assert mtc.get_attack() == mtc.attack + 3
	}

	@Test
	void MurlocTidehunter_play() {
		// Battlecry: Summon a 1/1 Murloc Scout
		_play("Murloc Tidehunter")
		assert p1.minions.size() == 2
		assert p1.minions[0].name == "Murloc Tidehunter"
		assert p1.minions[1].name == "Murloc Scout"
	}

	@Test
	void MurlocWarleader_play() {
		// All other Murlocs have +2/+1

		// summon the murlocs
		def mtc = _play("Murloc Tidecaller")	// Whenever a Murloc is summoned, gain +1 Attack.
		def mth = _play("Murloc Tidehunter")	// Battlecry: Summon a 1/1 Murloc Scout
		Card msc = p1.minions.find{it.name == 'Murloc Scout'}
		assert msc != null
		def mwl = _play("Murloc Warleader")
		def blu = _play("Bluegill Warrior")		// check permanent effect (summon after mwl)
		def lep = _play("Leper Gnome")			// non murlocn should have no effect
		_next_turn()
		// should also work for opponent's murlocs
		def cls = _play("Coldlight Seer")		// Battlecry: Give ALL other Murlocs +2 Health.'

		assert mtc.get_attack() == mtc.attack + 5 + 2 // +5 summoned murlocs +2 warleader
		assert mtc.get_health() == mtc.health + 1 + 2 // +1 warleader + 2 coldlight seer
		assert mth.get_attack() == mth.attack + 2 // +2 warleader
		assert mth.get_health() == mth.health + 3 // +1 warleader +2 coldlight seer
		assert msc.get_attack() == msc.attack + 2 // +2 warleader
		assert msc.get_health() == msc.health + 3 // +1 warleader +2 coldlight seer
		assert mwl.get_attack() == mwl.attack + 0 // should be unchanged (other murlocs)
		assert mwl.get_health() == mwl.health + 2 //  + 2 coldlight seer
		assert blu.get_attack() == blu.attack + 2 // +2 warleader
		assert blu.get_health() == blu.health + 3 // +1 warleader +2 coldlight seer
		assert lep.get_attack() == lep.attack + 0 // should be unchanged (not a murloc)
		assert lep.get_health() == lep.health + 0 // should be unchanged (not a murloc)
		assert cls.get_attack() == cls.attack + 2 // +2 warleader
		assert cls.get_health() == cls.health + 1 // +1 warleader

		// check effect is removed when the warleader dies
		mwl.dies()
		assert mtc.get_attack() == mtc.attack + 5 // +5 summoned
		assert mtc.get_health() == mtc.health + 2 // +2 coldlight seer
		assert mth.get_attack() == mth.attack + 0 //
		assert mth.get_health() == mth.health + 2 // +2 coldlight seer
		assert blu.get_attack() == blu.attack + 0 //
		assert blu.get_health() == blu.health + 2 // +2 coldlight seer
		assert cls.get_attack() == cls.attack + 0 //
		assert cls.get_health() == cls.health + 0 //

		// check effect is removed when the warleader is silenced
		def mwl2 = _play("Murloc Warleader")
		assert blu.get_attack() == blu.attack + 2 // +2 warleader
		assert blu.get_health() == blu.health + 3 // +1 warleader +2 coldlight seer
		_next_turn()
		_play("Silence", mwl2)
		assert blu.get_attack() == blu.attack + 0 // warleader is silenced
		assert blu.get_health() == blu.health + 2 // +2 coldlight seer
	}

	@Test
	void NatPagle_play() {
		// At the start of your turn, you have a 50% chance to draw an extra card

		def nat = _play("Nat Pagle")
		def before_hand_size = p1.hand.size()
		_next_random_int(1) // next rand will return 1 (-> draw)
		_next_turn()
		_next_turn()
		assert p1.hand.size() == before_hand_size + 2 // normal draw + extra card
		_next_turn()
		// check silence removes effect
		_play("Silence", nat)
		_next_random_int(1)
		_next_turn()
		assert p1.hand.size() == before_hand_size + 3 // + 1 normal draw only
	}
	
	@Test
	void NerubArWeblord_play() {
		def nerub = _play("Nerub'ar Weblord")
		// with battlecry
		Card abu = g.new_card("Abusive Sergeant")
		p1.hand.cards.add(abu)
		assert abu.has_battlecry()
		assert abu.get_cost() == abu.cost + 2
		// without battlecry
		Card blu = g.new_card("Bluegill Warrior")
		p1.hand.cards.add(blu)
		assert blu.get_cost() == blu.cost
		assert !blu.has_battlecry()
		nerub.dies()
		assert abu.get_cost() == abu.cost
	}
	
	@Test
	void NerubianEgg_play() {
		def egg = _play("Nerubian Egg")
		egg.dies()
		assert p1.minions.size() == 1
		assert p1.minions[0].name == "Nerubian"
		assert p1.minions[0].attack == 4
		assert p1.minions[0].health == 4
	}

	@Test
	void Nightblade_play() {
		// Battlecry: Deal 3 damage to the enemy hero
		_play("Nightblade")
		assert p2.hero.health == 30 -3

		_play("Kobold Geomancer") // +1 Spell Damage, should have no effect
		_play("Nightblade")
		assert p2.hero.health == 27 -3

	}

	@Test
	void NoviceEngineer_play() {
		// Battlecry: Draw a card
		def before_hand_size = p1.hand.size()
		def nov = _play("Novice Engineer")
		assert nov.is_in_play()
		assert p1.hand.size() == before_hand_size +1

		// hand full
		while (p1.hand.size() < 10) {
			p1.draw(1)
		}
		assert p1.hand.size() == 10
		_play("Novice Engineer")
		assert p1.hand.size() == 10
	}

	@Test
	void Nozdormu_play() {
		// Players only have 15 seconds to take their turns

		assert g.turn_timeout == 90
		def noz = _play("Nozdormu")
		assert g.turn_timeout == 15
		_next_turn()
		assert g.turn_timeout == 15
		_play("Silence", noz)	// since timeout is evaluated when turn starts, it will be active next turn
		assert g.turn_timeout == 15
		_next_turn()
		assert g.turn_timeout == 90
	}

	@Test
	void OgreMagi_play() {
		// Spell Damage +1
		def oma = _play("Ogre Magi")
		_play("Fireball", p2.hero)
		assert p2.hero.health == 30 -6 -1

		// Check silence
		_next_turn()
		_play("Silence", oma)
		_next_turn()
		_play("Fireball", p2.hero)
		assert p2.hero.health == 30 -6 -1 -6
	}
	
	@Test
	void OldMurkEye_play() {
		/* Player A */
		def ome = _play("Old Murk-Eye")
		assert ome.get_attack() == ome.attack
		def blu = _play("Bluegill Warrior")
		assert ome.get_attack() == ome.attack + 1
		_play("Leper Gnome")
		assert ome.get_attack() == ome.attack + 1
		_next_turn()
		
		/* Player B */
		_play("Coldlight Oracle")
		assert ome.get_attack() == ome.attack + 2
		blu.dies()
		assert ome.get_attack() == ome.attack + 1
		_play("Silence", ome)
		assert ome.get_attack() == ome.attack
		
	}

	@Test
	void Onyxia_play() {
		// Battlecry: Summon 1/1 Whelps until your side of the battlefield is full
		_play("Onyxia")
		assert p1.minions.size() == 7
		assert p1.minions[1].name == "Whelp"
		assert p1.minions[1].attack == 1
		assert p1.minions[1].health == 1
	}

	@Test
	void PintsizedSummoner_play() {
		// The first minion you play each turn costs (1) less

		_play("Pint-sized Summoner")
		// next minion same turn -> no cost reduction
		def abo = g.new_card("Abomination")
		p1.hand.add(abo)
		assert abo.get_cost() == abo.cost
		_next_turn()
		_next_turn()

		// first minion next turn -> cost reduction
		def lep = g.new_card("Leper Gnome")
		p1.hand.add(lep)
		assert lep.get_cost() == lep.cost - 1
		p1.available_mana = 0
		p1.play(lep) // should not fail

		// second minion, should not have its cost reduced
		def abo2 = g.new_card("Abomination")
		p1.hand.add(abo2)
		assert abo2.get_cost() == abo2.cost
		p1.available_mana = abo2.get_cost() -1
		_should_fail("cost cannot be paid") { p1.play(abo2) }
		_next_turn()
		_next_turn() // should reset the counter
		def shb = g.new_card("Shieldbearer")
		p1.hand.add(shb)
		assert shb.get_cost() == shb.cost - 1  // 0
		p1.available_mana = 0
		p1.play(shb) // should not fail
	}

	@Test
	void Poultryizer_play() {
		g.summon(p1, "Poultryizer")
		_next_turn()

		_next_turn()
		assert p1.minions().size() == 1
		assert p1.minions[0].name == 'Chicken'
		assert p1.minions[0].attack == 1
		assert p1.minions[0].health == 1
	}

	@Test
	void PriestessOfElune_play() {
		// Battlecry: Restore 4 Health to your hero
		p1.hero.health = 28
		_play("Priestess of Elune")
		assert p1.hero.health == 30
	}
	
	@Test
	void QuestingAdventurer_play() {
		/* Player A */
		def qa = _play("Questing Adventurer")
		assert qa.get_attack() == qa.attack
		assert qa.get_health() == qa.health
		
		_play("Bluegill Warrior")
		assert qa.get_attack() == qa.attack + 1
		assert qa.get_health() == qa.health + 1
		
		_play("Fireball", p2.hero)
		assert qa.get_attack() == qa.attack + 2
		assert qa.get_health() == qa.health + 2
		
		_next_turn()
		
		/* Player B */
		_play("Leper Gnome") // should have no effect
		assert qa.get_attack() == qa.attack + 2
		assert qa.get_health() == qa.health + 2
		
		_play("Silence", qa)
		assert qa.get_attack() == qa.attack
		assert qa.get_health() == qa.health

	}
	
	/*
	 * class RagingWorgen extends CardDefinition {
	RagingWorgen() {
		name='Raging Worgen'; type='minion'; cost=3; attack=3; max_health=3
		text='Enrage: Windfury and +1 Attack'
		when_enraged(text) {  
			this_minion.gets('+1 Attack') 
			this_minion.gets(WINDFURY) 
		}
		when_enraged_no_more('Remove +1 Attack, Windfury') { 
			this_minion.remove_first_buff('+1 Attack')
			this_minion.remove_first_buff(WINDFURY)
		}
	}
}
	 */
	
	@Test
	void RagingWorgen_play() {
		def worgen = _play("Raging Worgen")
		assert worgen.get_attack() == worgen.attack
		assert !worgen.has_buff(WINDFURY)
		
		_play("Moonfire", worgen)
		assert worgen.is_enraged
		assert worgen.get_attack() == worgen.attack + 1
		assert worgen.has_buff(WINDFURY)
	}

	@Test
	void RagnarostheFirelord_play() {
		// Can't Attack. At the end of your turn, deal 8 damage to a random enemy

		def rag = _play("Ragnaros the Firelord")

		// check end of turn effect
		_next_turn()
		assert p1.hero.health == 22

		// check can't attack
		_next_turn()
		_should_fail("cannot attack") { _attack(rag, p2.hero) }

		// check mind control
		_next_turn()
		_play("Mind Control", rag)
		_next_turn()
		assert p1.hero.health == 22
	}

	@Test
	void RaidLeader_play() {
		// Your other minions have +1 Attack

		// check simple case
		def blu = _play("Bluegill Warrior")
		def lep = _play("Leper Gnome")
		def rai = _play("Raid Leader")
		assert blu.get_attack() == blu.attack + 1
		assert lep.get_attack() == lep.attack + 1
		assert rai.get_attack() == rai.attack // only other minions

		// check mind control
		_next_turn()
		def bou = _play("Boulderfist Ogre")
		_play("Mind Control", rai)
		assert blu.get_attack() == blu.attack
		assert lep.get_attack() == lep.attack
		assert bou.get_attack() == bou.attack + 1

		// check silent
		_next_turn()
		_play("Silence", rai)
		assert blu.get_attack() == blu.attack
		assert lep.get_attack() == lep.attack
		assert bou.get_attack() == bou.attack
	}

	@Test
	void RazorfenHunter_play() {
		// Battlecry: Summon a 1/1 Boar

		_play("Razorfen Hunter")
		assert p1.minions.size() == 2
		assert p1.minions[1].name == "Boar"
		assert p1.minions[1].creature_type == "beast"
		assert p1.minions[1].cost == 1
		assert p1.minions[1].attack == 1
		assert p1.minions[1].health == 1
	}

	@Test
	void RecklessRocketeer_play() {
		// Charge
		def rec = _play("Reckless Rocketeer")
		_attack(rec, p2.hero)
		assert p2.hero.health == 30 - rec.attack
	}

	@Test
	void RepairBot_no_heal() {
		g.summon(p1, "Repair Bot")
		_next_turn()
	}

	@Test
	void RepairBot_heal_hero() {
		g.summon(p1, "Repair Bot")
		p1.hero.health = 15
		_next_turn()

		assert p2.hero.health == 21
	}

	@Test
	void ScarletCrusader_play() {
		// Divine Shield

		def bou = _play("Boulderfist Ogre")
		_next_turn()
		def sca = _play("Scarlet Crusader")
		assert sca.has_buff(DIVINE_SHIELD)
		_next_turn()
		_attack(bou, sca)

		assert !sca.is_dead()
		assert !sca.has_buff(DIVINE_SHIELD)

	}
	
	@Test
	void Secretkeeper_play() {
		/* Player A */
		def sec = _play("Secretkeeper")
		assert sec.get_attack() == sec.attack
		assert sec.get_health() == sec.health
		
		_play("Explosive Trap")
		assert sec.get_attack() == sec.attack + 1
		assert sec.get_health() == sec.health + 1
		
		_play("Fireball", p2.hero) // not a secret
		assert sec.get_attack() == sec.attack + 1
		assert sec.get_health() == sec.health + 1

		_play("Leper Gnome") // not a secret
		assert sec.get_attack() == sec.attack + 1
		assert sec.get_health() == sec.health + 1
		_next_turn()
		
		/* Player B */
		_play("Freezing Trap")
		assert sec.get_attack() == sec.attack + 2
		assert sec.get_health() == sec.health + 2
		
		_play("Silence", sec)
		assert sec.get_attack() == sec.attack
		assert sec.get_health() == sec.health

	}

	@Test
	void SeaGiant_play() {
		// Costs (1) less for each other minion on the battlefield.

		_play("Bluegill warrior")	// 1
		_play("Leper Gnome")		// 2
		_next_turn()
		_play("Faerie Dragon")		// 3
		_play("Abomination")		// 4

		def sgi = g.new_card("Sea Giant")
		p1.hand.add(sgi)
		assert sgi.get_cost() == sgi.cost - 4	// 10 - 4
		p1.available_mana = 6
		p1.play(sgi)	// should not fail

		assert sgi.is_in_play()
		assert sgi.get_cost() == sgi.cost - 4	// 10 - 4 (sgi doesn't count)

		_next_turn()
		_play("Silence", sgi)
		assert sgi.get_cost() == sgi.cost	// 10
	}

	@Test
	void ShadeOfNaxxramas_play() {
		/* Player A */
		def shade = _play("Shade of Naxxramas")
		assert shade.has_stealth()
		assert shade.get_attack() == shade.attack
		assert shade.get_health() == shade.health
		_next_turn()
		
		/* Player B */
		assert shade.get_attack() == shade.attack
		assert shade.get_health() == shade.health
		_next_turn()
		
		/* Player A */
		assert shade.get_attack() == shade.attack + 1
		assert shade.get_health() == shade.health + 1
		_next_turn()

		/* Player B */
		assert shade.get_attack() == shade.attack + 1
		assert shade.get_health() == shade.health + 1
		_next_turn()
		
		/* Player A */
		assert shade.get_attack() == shade.attack + 2
		assert shade.get_health() == shade.health + 2
		_next_turn()
		
	}
	
	@Test
	void SilverbackPatriarch_play() {
		def sil = _play("Silverback Patriarch")
		assert sil.has_buff(TAUNT)
	}

	@Test
	void SilverHandKnight_play() {
		// Battlecry: Summon a 2/2 Squire

		_play("Silver Hand Knight")
		assert p1.minions.size() == 2
		assert p1.minions[1].name == "Squire"
		assert p1.minions[1].cost == 1
		assert p1.minions[1].attack == 2
		assert p1.minions[1].health == 2
	}

	@Test
	void SilvermoonGuardian_play() {
		// divine shield

		def sig = _play("Silvermoon Guardian")
		assert sig.has_buff(DIVINE_SHIELD)
	}

	@Test
	void ShatteredSunCleric_play() {
		// Battlecry: Give a friendly minion +1/+1

		// no friendly minion
		_play("Bluegill Warrior")
		_next_turn()
		_play("Shattered Sun Cleric")

		// 1 friendly minion
		def lep = _play("Leper Gnome")
		_next_turn()
		_next_turn()
		_play("Shattered Sun Cleric", lep)
		assert lep.get_attack() == lep.attack + 1
		assert lep.get_health() == lep.health + 1
	}
	
	@Test
	void SludgeBelcher_play() {
		def slu = _play("Sludge Belcher")
		assert slu.has_taunt()
		slu.dies()
		assert p1.minions.size() == 1
		assert p1.minions[0].name == "Slime"
		assert p1.minions[0].attack == 1
		assert p1.minions[0].health == 2
		assert p1.minions[0].has_taunt()
		
	}

	@Test
	void SouthseaCaptain_play() {
		/* Player A */
		def ss1 = _play("Southsea Deckhand")
		assert ss1.is_a_pirate()
		assert ss1.get_attack() == ss1.attack
		assert ss1.get_health() == ss1.health
		
		def lep = _play("Leper Gnome")
		assert !lep.is_a_pirate()
		assert lep.get_attack() == lep.attack
		assert lep.get_health() == lep.health
		_next_turn()
		
		/* Player B */
		def ss2 = _play("Southsea Deckhand")
		assert ss2.is_a_pirate()
		assert ss2.get_attack() == ss2.attack
		assert ss2.get_health() == ss2.health
		_next_turn()
		
		/* Player A */
		def ssc = _play("Southsea Captain")
		assert ssc.is_a_pirate()
		assert ssc.get_attack() == ssc.attack
		assert ssc.get_health() == ssc.health
		
		assert ss1.get_attack() == ss1.attack + 1
		assert ss1.get_health() == ss1.health + 1
		
		assert ss2.get_attack() == ss2.attack
		assert ss2.get_health() == ss2.health
		
		def ssc2 = _play("Southsea Captain")
		assert ssc2.is_a_pirate()
		assert ssc2.get_attack() == ssc.attack + 1
		assert ssc2.get_health() == ssc.health + 1

		assert ssc.get_attack() == ssc.attack + 1
		assert ssc.get_health() == ssc.health + 1
		
		assert ss1.get_attack() == ss1.attack + 2
		assert ss1.get_health() == ss1.health + 2
		_next_turn()
		
		/* Player B */
		_play("Silence", ssc)
		_play("Silence", ssc2)
		assert ss1.get_attack() == ss1.attack
		assert ss1.get_health() == ss1.health
		assert ss2.get_attack() == ss2.attack
		assert ss2.get_health() == ss2.health
		assert ssc2.get_attack() == ssc.attack
		assert ssc2.get_health() == ssc.health
		assert ssc.get_attack() == ssc.attack
		assert ssc.get_health() == ssc.health
	}

	@Test
	void SouthseaDeckhand_play() {
		// Has Charge while you have a weapon equipped

		// no weapon
		def ss1 = _play("Southsea Deckhand")
		assert p1.hero.weapon == null
		assert !ss1.has_buff(CHARGE)

		// with a weapon
		p1.hero.equip_weapon(2,2)
		assert ss1.has_buff(CHARGE)
	}
	
	@Test
	void SpectralKnight_play() {
		def sk = _play("Spectral Knight")
		assert sk.has_buff(CANNOT_BE_TARGETED_BY_SPELL_OR_POWER)
		_should_fail("no valid target") { _play("Silence", sk) }
		p1.hero = new JainaProudmoore(g)
		p1.next_choices = [sk]
		_should_fail("not a valid choice") { _use_hero_power() }
	}
	
	@Test
	void StormpikeCommando_ok() {
		// Deal 1 damage to a minion for each Armor you have
		def kob = _play("Kobold Geomancer")
		_next_turn()
		_play("Stormpike Commando", kob)
		assert kob.is_dead()
	}
	
	@Test
	void StampedingKodo_no_minion() {
		def kodo = _play("Stampeding Kodo")
		assert kodo.is_in_play
	}

	@Test
	void StampedingKodo_friendly_minion() {
		def blu = _play("Bluegill Warrior")
		def kodo = _play("Stampeding Kodo")
		assert kodo.is_in_play
		assert !blu.is_dead()
	}

	@Test
	void StampedingKodo_enemy_minion() {
		/* Player A */
		def blu = _play("Bluegill Warrior")
		_next_turn()
		
		/* Player B */
		def kodo = _play("Stampeding Kodo")
		assert kodo.is_in_play
		assert blu.is_dead()
	}

	@Test
	void StampedingKodo_too_big_enemy_minion() {
		/* Player A */
		def rider = _play("Wolfrider")
		_next_turn()
		
		/* Player B */
		def kodo = _play("Stampeding Kodo")
		assert kodo.is_in_play
		assert !rider.is_dead()
	}

	@Test
	void StoneskinGargoyle_play() {
		/* Player A */
		def garg = _play("Stoneskin Gargoyle")
		_next_turn()
		
		/* Player B */
		def blu = _play("Bluegill Warrior")
		_attack(blu, garg)
		assert blu.is_dead()
		assert garg.get_health() == 2
		assert garg.is_enraged
		_next_turn()
		
		assert garg.health == garg.max_health
		assert !garg.is_enraged
	}

	@Test
	void StormwindChampion_play() {
		// Your other minions have +1/+1
		def blu = _play("Bluegill Warrior")
		_next_turn()
		Card abo = _play("Abomination")
		Card huf = _play("Huffer")
		assert abo.get_attack() == 4
		assert huf.get_attack() == 4
		assert abo.get_health() == 4
		assert huf.get_health() == 2
		Card sto = _play("Stormwind Champion")
		assert abo.get_attack() == 5
		assert huf.get_attack() == 5
		assert abo.get_health() == 5
		assert huf.get_health() == 3
		assert sto.get_attack() == sto.card_definition.attack // unaffected
		assert sto.get_health() == sto.card_definition.max_health // unaffected
		assert blu.get_attack() == blu.card_definition.attack // unaffected
		assert blu.get_health() == blu.card_definition.max_health // unaffected
		sto.dies()
		assert abo.get_attack() == 4 // +1/+1 removed
		assert huf.get_attack() == 4
		assert abo.get_health() == 4
		assert huf.get_health() == 2
	}

	@Test
	void StormwindChampion_test2() {
		// Your other minions have +1/+1
		def blu = _play("Bluegill Warrior")	// 2/1
		_play("Stormwind Champion")	// blu gets 3/2
		assert blu.get_attack() == 3
		assert blu.get_health() == 2
		assert blu.get_max_health() == 2
		_next_turn()

		_play("Mortal Coil", blu)
		assert blu.health == 0
		assert blu.get_health() == 1
		assert blu.get_max_health() == 2
		assert blu.is_enraged

		_play("Mortal Coil", blu)
		assert blu.is_dead()
	}

	@Test
	void StormwindChampion_test3() {
		// Your other minions have +1/+1
		def blu = _play("Bluegill Warrior")	// 2/1
		Card sto = _play("Stormwind Champion")	// blu gets 3/2
		assert blu.get_attack() == 3
		assert blu.get_health() == 2
		assert blu.get_max_health() == 2
		_next_turn()

		_play("Mortal Coil", blu)
		assert blu.health == 0
		assert blu.get_health() == 1
		assert blu.get_max_health() == 2
		assert blu.is_enraged

		_play("Silence", sto)
		assert blu.is_dead()
		assert ! p2.minions.contains(blu)
	}

	@Test
	void Spellbreaker_no_minion() {
		// Battlecry: Silence a minion

		// no minion
		_play("Spellbreaker")
	}

	@Test
	void Spellbreaker_1_minion() {
		// Battlecry: Silence a minion

		// 1 minion
		def shb = _play("Shieldbearer")
		assert shb.has_buff(TAUNT)
		_next_turn()
		_play("Spellbreaker", shb)
		assert !shb.has_buff(TAUNT)
	}

	@Test
	void Spellbreaker_play() {
		// Battlecry: Silence a minion

		// no minion
		_play("Spellbreaker")

		// 1 minion
		def shb = _play("Shieldbearer")
		assert shb.has_buff(TAUNT)
		_next_turn()
		_play("Spellbreaker", shb)
		assert !shb.has_buff(TAUNT)
	}

	@Test
	void SpitefulSmith_no_weapon() {
		// Enrage: Your weapon has +2 Attack

		// no weapon
		def smi = _play("Spiteful Smith")
		_play("Cruel Taskmaster", smi) // -> smi enraged
		assert p1.hero.weapon == null
	}

	@Test
	void Stalagg_play_no_thaddius() {
		def stalagg = _play('Stalagg')
		assert !g.feugen_died
		assert !g.stalagg_died
		stalagg.dies()
		assert g.stalagg_died
		assert p1.minions.find { it.name == 'Thaddius'} == null
	}

	@Test
	void Stalagg_play_thaddius() {
		g.feugen_died = true
		def stalagg = _play('Stalagg')
		stalagg.dies()
		assert p1.minions.find { it.name == 'Thaddius'} != null
	}


	@Test
	void SpitefulSmith_with_weapon() {
		// Enrage: Your weapon has +2 Attack

		p1.hero.equip_weapon(2,2)
		def smi = _play("Spiteful Smith")
		_play("Cruel Taskmaster", smi) // -> smi enraged
		assert p1.hero.weapon.get_attack() == 4
		assert p1.hero.weapon.has_buff('+2 Attack')

		_next_turn()
		_play("Silence", smi)	// should have no effect
		assert p2.hero.weapon.get_attack() == 4
	}

	@Test
	void SunfuryProtector_no_neighbor() {
		// Battlecry: Give adjacent minions Taunt

		def sun = _play("Sunfury Protector", 1)
		assert !sun.has_buff(TAUNT) // not itself
	}

	@Test
	void SunfuryProtector_1_neighbor() {
		// Battlecry: Give adjacent minions Taunt

		def lep = _play("Leper Gnome")
		def sun = _play("Sunfury Protector", 1)
		assert lep.has_buff(TAUNT)
		assert !sun.has_buff(TAUNT) // not itself
	}

	@Test
	void SunfuryProtector_2_neighbors() {
		// Battlecry: Give adjacent minions Taunt

		def lep = _play("Leper Gnome")
		def fae = _play("Faerie Dragon")
		def bou = _play("Boulderfist Ogre")
		def sun = _play("Sunfury Protector", 1) // between lep and fae
		assert lep.has_buff(TAUNT)
		assert fae.has_buff(TAUNT)
		assert !bou.has_buff(TAUNT)    // only adjacent
		assert !sun.has_buff(TAUNT) // not itself
	}

	@Test
	void SylvanasWindrunner_play() {
		// Deathrattle: Take control of a random enemy minion

		def bou = _play("Boulderfist Ogre")		// 6/7
		_next_turn()
		def syl = _play("Sylvanas Windrunner")	// 5/5
		_next_turn()
		_attack(bou, syl)
		assert syl.is_dead()
		assert !bou.is_dead()
		assert bou.controller == p2
	}

	@Test
	void TaurenWarrior_inner_rage() {
		// Taunt. Enrage: +3 Attack
		def tau = _play("Tauren Warrior")
		assert tau.has_taunt()
		assert tau.get_attack() == tau.card_definition.attack
		_play("Inner Rage", tau) // Deal 1 damage to a minion and give it +2 Attack
		assert tau.is_enraged
		assert tau.get_attack() == tau.card_definition.attack +2 +3
	}

	@Test
	void TheBeast_play() {
		// Deathrattle: Summon a 3/3 Finkle Einhorn for your opponent

		def bou = _play("Boulderfist Ogre") 	// 6/7
		_play("Cruel Taskmaster", bou)			// +2 Attack
		_next_turn()
		def bea = _play("The Beast")			// 9/7
		_next_turn()
		_attack(bou, bea)
		assert bou.is_dead()
		assert bea.is_dead()
		assert p1.minions.size() == 2 // cruel taskmast + finkle
		assert p1.minions[1].name == 'Finkle Einhorn'
		assert p1.minions[1].attack == 3
		assert p1.minions[1].health == 3
	}

	@Test
	void TheBlackKnight_play() {
		// Battlecry: Destroy an enemy minion with Taunt

		def shb = _play("Shieldbearer")
		_play("The Black Knight")
		assert !shb.is_dead() // not an enemy

		_next_turn()
		_play("The Black Knight", shb)
		assert shb.is_dead()
	}

	@Test
	void TinkmasterOverspark_no_minion() {
		// Battlecry: Transform another random minion into a 5/5 Devilsaur or a 1/1 Squirrel

		_play("Tinkmaster Overspark")
		assert p1.minions[0].name == "Tinkmaster Overspark"
	}


	@Test
	void TinkmasterOverspark_1_minion() {
		// Battlecry: Transform another random minion into a 5/5 Devilsaur or a 1/1 Squirrel

		_play("Boulderfist Ogre")
		_next_turn()

		_play("Tinkmaster Overspark")
		assert p2.minions.size() == 1
		assert p2.minions[0].name in ["Devilsaur", "Squirrel"]
	}

	@Test
	void TwilightDrake_play() {
		// Battlecry: Gain +1 Health for each card in your hand

		def hsz = p1.hand.size()
		def twi = _play("Twilight Drake")
		assert twi.has_buff("+${hsz} Health")
		assert twi.get_health() == twi.health + hsz
	}
	
	@Test
	void Undertaker_play() {
		/* Player A */
		def u = _play("Undertaker")
		assert !u.has_deathrattle()
		assert u.get_attack() == u.attack
		assert u.get_health() == u.health
		
		def g = _play("Leper Gnome")
		assert g.has_deathrattle()
		assert u.get_attack() == u.attack + 1
		assert u.get_health() == u.health + 1
		_next_turn()
		
		/* Player B */
		def s = _play("Sylvanas Windrunner")
		assert s.has_deathrattle()
		assert u.get_attack() == u.attack + 1 // not summoned by you
		assert u.get_health() == u.health + 1
		
		_play("Silence", u)
		assert u.get_attack() == u.attack
		assert u.get_health() == u.health
	}
	
	@Test
	void UnstableGhoul_play() {
		/* Player A */
		def ghoul = _play("Unstable Ghoul")
		assert ghoul.has_taunt()
		assert ghoul.has_deathrattle()
		def lep = _play("Leper Gnome")
		assert lep.has_deathrattle()
		_next_turn()
		
		/* Player B */
		def blu = _play("Bluegill Warrior")
		p1.hero.equip_weapon("Fiery War Axe")
		_attack(p1.hero, ghoul)
		assert ghoul.is_dead()
		assert blu.is_dead()
		assert lep.is_dead()
	}

	@Test
	void VentureCoMercenary_play() {
		// Your minions cost (3) more
		_play("Venture Co. Mercenary")
		def blu = g.new_card("Bluegill Warrior")
		p1.hand.add(blu)
		assert blu.get_cost() == blu.cost + 3
	}

	@Test
	void VioletTeacher_play() {
		// Whenever you cast a spell, summon a 1/1 Apprentice

		_play("Violet Teacher")
		assert p1.minions.size() == 1

		def bou = _play("Boulderfist Ogre") // not a spell -> no effect
		assert p1.minions.size() == 2

		_play("The Coin")	// a spell
		assert p1.minions.size() == 3
		assert p1.minions[2].name == "Violet Apprentice"
		assert p1.minions[2].cost == 0
		assert p1.minions[2].attack == 1
		assert p1.minions[2].health == 1

		_next_turn()
		_play("Assassinate", bou) // a spell but not cast by me -> no effect
		assert p2.minions.size() == 2
	}

	@Test
	void VoodooDoctor_play() {
		// Battlecry: Restore 2 Health

		p1.hero.health = 18
		_play("Voodoo Doctor", p1.hero)
		assert p1.hero.health == 20
	}

	@Test
	void WildPyromancer_play() {
		// After you cast a spell, deal 1 damage to ALL minions

		def aci = _play("Acidic Swamp Ooze")	// 3/2
		def lep = _play("Leper Gnome")			// 2/1
		def kob = _play("Kobold Geomancer")		// 2/2
		_next_turn()

		_play("Wild Pyromancer")
		_play("Silence", lep) // should trigger before pyr effect
		assert !aci.is_dead() // kob +1 Spell damage does not apply
		assert aci.health == aci.max_health - 1
		assert lep.is_dead()
		assert p1.hero.health == 30 // lep effect silenced
		assert !kob.is_dead()
		assert kob.health == kob.max_health - 1
	}
	
	@Test
	void WailingSoul_play() {
		def ds = _play("Dancing Swords")
		assert ds.has_deathrattle()
		
		def zombie = _play("Zombie Chow")
		assert zombie.has_deathrattle()
		
		def ws = _play("Wailing Soul")
		assert !ds.has_deathrattle()
		assert ds.text == ''
		assert !zombie.has_deathrattle()
		assert zombie.text == ''
		
		def before = p2.hand.size()
		ds.dies()
		assert p2.hand.size() == before
		
		p2.hero.health = 20
		zombie.dies()
		assert p2.hero.health == 20
		
		assert ws.text != '' // should not silence itself
	}

	@Test
	void WildPyromancer_equality() {
		// After you cast a spell, deal 1 damage to ALL minions

		def aci = _play("Acidic Swamp Ooze")	// 3/2
		def lep = _play("Leper Gnome")			// 2/1
		def kob = _play("Kobold Geomancer")		// 2/2
		_next_turn()

		def pyr = _play("Wild Pyromancer")
		_play("Equality") // Change the Health of ALL minions to 1, should kill everybody
		assert aci.is_dead()
		assert lep.is_dead()
		assert kob.is_dead()
		assert pyr.is_dead()
	}

	@Test
	void WildPyromancer_equality_stormwind_champion() {
		// After you cast a spell, deal 1 damage to ALL minions

		def aci = _play("Acidic Swamp Ooze")	// 3/2
		def lep = _play("Leper Gnome")			// 2/1
		def kob = _play("Kobold Geomancer")		// 2/2
		def sto = _play("Stormwind Champion")	// 7/6, Your other minions have +1/+1
		_next_turn()

		_play("Equality") // Change the Health of ALL minions to 1
		assert aci.get_health() == 2
		assert aci.get_max_health() == 2
		assert lep.get_health() == 2
		assert lep.get_max_health() == 2
		assert kob.get_health() == 2
		assert kob.get_max_health() == 2
		assert sto.get_health() == 1
		assert sto.get_max_health() == 1

		// very weird, cannot decide if what follows is a bug...
		def pyr = _play("Wild Pyromancer")
		_play("Eye For An Eye") // trigger pyro effect
		assert sto.is_dead()			// ok Storwind Champion is not protected by its +1/+1
		assert !aci.is_dead()
		assert aci.get_health() == 1
		assert !lep.is_dead()    // survives the pyro but why should survive when sto +1/+1 is out ?
		assert lep.get_health() == 1
		assert !kob.is_dead()
		assert kob.get_health() == 1
		assert !pyr.is_dead()
		assert pyr.get_health() == 1
	}

	@Test
	void WindfuryHarpy_play() {
		// Windfury

		def win = _play("Windfury Harpy")
		assert win.has_buff(WINDFURY)
		_next_turn()
		_next_turn()
		_attack(win, p2.hero)
		_attack(win, p2.hero)
		assert p2.hero.health == 30 - 2*win.get_attack()
	}

	@Test
	void WorgenInfiltrator_play() {
		// Stealth

		def wor = _play("Worgen Infiltrator")
		assert wor.has_stealth()
		_should_fail("just summoned") {
			_attack(wor, p2.hero)
		}
		_next_turn()
		_next_turn()

		_attack(wor, p2.hero)
		assert p2.hero.health == 30 - wor.get_attack()
		assert ! wor.has_stealth()
	}

	@Test
	void YoungDragonhawk_play() {
		// Windfury

		def yod = _play("Young Dragonhawk")
		assert yod.has_buff(WINDFURY)
		_next_turn()
		_next_turn()
		_attack(yod, p2.hero)
		_attack(yod, p2.hero)
		assert p2.hero.health == 30 - 2*yod.get_attack()
	}

	@Test
	void YoungPriestess_play() {
		// At the end of your turn, give another random friendly minion +1 Health

		// test1 : alone -> no effect
		def yop = _play("YoungPriestess")
		_next_turn()
		assert yop.get_health() == yop.health
		assert !yop.has_buff('+1 Health')

		_play("Leper Gnome") // not friendly

		// test2 : 1 friendly minion
		_next_turn()
		def fae = _play("Faerie Dragon")
		_next_turn()
		assert yop.get_health() == yop.health
		assert !yop.has_buff('+1 Health')
		assert fae.get_health() == fae.health + 1
		assert fae.has_buff('+1 Health')

		// test3 : 2 friendly minions
		_next_turn()
		def kob = _play("Kobold Geomancer")
		_next_turn()
		assert kob.get_health() == kob.health + 1 || fae.get_health() == fae.health + 2
	}

	@Test
	void YouthfulBrewmaster_play() {
		// Battlecry: Return a friendly minion from the battlefield to your hand

		// test 1: no minion
		def yb1 = _play("Youthful Brewmaster")
		assert yb1.is_in_play()

		// test 2 : 1 minion
		def yb2 = _play("Youthful Brewmaster", yb1)
		assert !yb1.is_in_play()
		assert yb2.is_in_play()
		assert p1.hand.contains(yb1)
	}

	@Test
	void Ysera_play() {
		// At the end of your turn, draw a Dream Card

		_play("Ysera")
		p1.hand.cards.clear()
		_next_turn()

		assert p2.hand.size() == 1
		assert p2.hand.cards[0].name in
		['Dream', 'Emerald Drake', 'Laughing Sister', 'Nightmare', 'Ysera Awakens']
	}

	@Test
	void YseraAwakens_play() {
		// Deal 5 damage to all characters except Ysera

		def bou = _play("Boulderfist Ogre")	// 6/7
		_next_turn()

		def kob = _play("Kobold Geomancer") // Spell Damage +1
		def yse = _play("Ysera")
		_play("Ysera Awakens")

		assert bou.health == bou.max_health - 6
		assert kob.is_dead()
		assert p1.hero.health == 30 - 6
		assert p2.hero.health == 30 - 6
		assert yse.health == yse.max_health
	}
	
	@Test
	void ZombieChow_play() {
		def zombie = _play("Zombie Chow")
		assert zombie.has_deathrattle()
		
		p2.hero.health = 20
		zombie.dies()
		assert p2.hero.health == 25

	}
}

