package org.zebig.hs.cards;

import static org.zebig.hs.mechanics.buffs.BuffType.*
import static org.junit.Assert.*
import org.zebig.hs.game.Game
import org.zebig.hs.mechanics.buffs.BuffType

import org.junit.Before
import org.junit.Test

import org.zebig.hs.utils.TestHelper

class TestWarrior extends TestHelper {
	
	@Before
	public void newGame() {
		_create_game_Garrosh_vs_Jaina()
	}
	
	@Test
	public void ArathiWeaponsmith_play() {
	// Battlecry: Equip a 2/2 Weapon
		_play("Arathi Weaponsmith")
		assert p1.hero.weapon != null
		assert p1.hero.weapon.attack == 2
		assert p1.hero.weapon.durability == 2
	}
	
	@Test
	public void ArcaniteReaper_play() {
		// 5/2 weapon reserved for Warrior
		assert p1.hero.weapon == null
		def arr = _play("Arcanite Reaper")
		assert p1.hero.weapon != null
		assert p1.hero.weapon.attack == 5
		assert p1.hero.weapon.durability == 2
		assert p1.hero.get_attack() == 5
		g.player_attacks(p1.hero, p2.hero)
		assert p2.hero.get_health() == 25
	}
	
	@Test
	public void Armorsmith_gain_armor() {
		// Whenever a friendly minion takes damage, gain 1 Armor
		_play("Armorsmith")
		def abo = _play("Abomination")
		def before_armor = p1.hero.armor
		_play_and_target("Cruel Taskmaster", abo)
		assert p1.hero.armor == before_armor + 1
	}
	
	@Test
	public void BattleRage_nobody_hurt() {
		// Draw a card for each damaged friendly character
		def before_hand_size = p1.hand.size()
		_play("Battle Rage")
		assert p1.hand.size() == before_hand_size
	}

	@Test
	public void BattleRage_hero_hurt() {
		// Draw a card for each damaged friendly character
		p1.hero.set_health(29)
		def before_hand_size = p1.hand.size()
		_play("Battle Rage")
		assert p1.hand.size() == before_hand_size + 1
	}

	@Test
	public void BattleRage_1_minion_hurt() {
		// Draw a card for each damaged friendly character
		def fad = _play("Faerie Dragon") // not hurt
		def abo = _play("Abomination")
		abo.set_health( abo.get_health() -1 )
		def before_hand_size = p1.hand.size()
		_play("Battle Rage")
		assert p1.hand.size() == before_hand_size + 1
	}
	
	@Test
	public void BattleRage_2_minions_hurt() {
		// Draw a card for each damaged friendly character
		def fad = _play("Faerie Dragon")
		fad.set_health( fad.get_health() -1 )
		def abo = _play("Abomination")
		abo.set_health( abo.get_health() -1 )
		def before_hand_size = p1.hand.size()
		_play("Battle Rage")
		assert p1.hand.size() == before_hand_size + 2
	}
	
	@Test
	public void BattleRage_hero_and_1_minion_hurt() {
		// Draw a card for each damaged friendly character
		def fad = _play("Faerie Dragon") // not hurt
		def abo = _play("Abomination")
		abo.set_health( abo.get_health() -1 )
		p1.hero.set_health(29)
		def before_hand_size = p1.hand.size()
		_play("Battle Rage")
		assert p1.hand.size() == before_hand_size + 2
	}
	
	@Test
	public void Brawl_no_minion() {
		// Destroy all minions except one. (chosen randomly)
		_play("Brawl")
	}

	@Test
	public void Brawl_1_minion() {
		// Destroy all minions except one. (chosen randomly)
		def abo = _play("Abomination")
		_play("Brawl")
	}

	@Test
	public void Brawl_2_minions() {
		// Destroy all minions except one. (chosen randomly)
		def abo = _play("Abomination")
		def bfo = _play("Boulderfist Ogre")
		assert p1.minions.size() == 2
		_play("Brawl")
		assert p1.minions.size() == 1
	}
	
	@Test
	public void Charge_play() {
		// Give a friendly minion +2 Attack and Charge
		
		// check targets
		_play("Leper Gnome")
		_next_turn()
		_play("Faerie Dragon")
		_should_fail("no valid target") { _play("Charge") }
		
		def abo = _play("Abomination")
		_play("Charge", abo)
		assert abo.get_attack() == abo.attack + 2	// 4 + 2
		assert abo.has_buff(CHARGE)
		_attack(abo, p2.hero)
		assert p2.hero.health == 24
		
		_next_turn()
		assert abo.get_attack() == abo.attack + 2	//permanent effect
	}
	
	@Test
	public void Cleave_play() {
		// Deal 2 damage to two random enemy minions
		
		// set targets
		def shb = _play("Shieldbearer")
		def fae = _play("Faerie Dragon")
		_next_turn()

		_play("Kobold Geomancer") // Spell damage + 1		
		_play("Cleave")
		
		assert shb.get_health() == shb.max_health - 3
		assert fae.is_dead()
	}
	
	@Test
	public void CommandingShout_play_after_minion() {
		// Your minions can't be reduced below 1 Health this turn. Draw a card
		def abu1 = _play("Abusive Sergeant")
		assert abu1.has_buff(BuffType.CANNOT_BE_REDUCED_BELOW_1_HEALTH) == false
		_play("Commanding Shout")
		assert abu1.has_buff(BuffType.CANNOT_BE_REDUCED_BELOW_1_HEALTH) == true
		_play_and_target("Cruel Taskmaster", abu1)
		assert abu1.is_dead() == false
	}

	@Test
	public void CommandingShout_play_before_minion() {
		// Your minions can't be reduced below 1 Health this turn. Draw a card
		_play("Commanding Shout")
		def abu1 = _play("Abusive Sergeant")
		assert abu1.has_buff(BuffType.CANNOT_BE_REDUCED_BELOW_1_HEALTH) == true
		_play_and_target("Cruel Taskmaster", abu1)
		assert abu1.is_dead() == false
		def lep = _play("Leper Gnome")
		def abu2 = _play_and_target("Abusive Sergeant", lep)
		assert lep.is_dead() == false
	}

	@Test
	public void CommandingShout_end_of_turn() {
		// Your minions can't be reduced below 1 Health this turn. Draw a card
		_play("Commanding Shout")
		def lep = _play("Leper Gnome")
		assert lep.has_buff(BuffType.CANNOT_BE_REDUCED_BELOW_1_HEALTH)
		_next_turn()
		assert lep.has_buff(BuffType.CANNOT_BE_REDUCED_BELOW_1_HEALTH) == false
	}
	
	@Test
	public void CruelTaskMaster_play_and_deals_damage() {
		/*
		 * 	test 'Battlecry: Deal 1 damage to a minion and give it +2 Attack.'
		 */
		def abo = _play("Abomination")
		def ctm = _play_and_target("Cruel Taskmaster", abo)
		assert abo.get_attack() == abo.card_definition.attack + 2
		assert abo.get_health() == abo.card_definition.max_health - 1
	}
	
	
	@Test
	public void Execute_fail_no_target() {
		// Destroy a damaged enemy minion.
		// test 1 : pas de cible du tout
		try {
			def exe = _play("Execute")
			fail("aurait d� planter : pas de cible")
		}
		catch (Exception e) {
			println e // OK
		}
	}

	@Test
	public void Execute_fail_no_hurt_target() {
		// Destroy a damaged enemy minion.
		// test 2 : une cible pas bless�e
		def abo = _play("Abomination", p2)
		try {
			def exe = _play_and_target("Execute", abo)
			fail("aurait d� planter : pas de cible valide")
		}
		catch (Exception e) {
			println e // OK
		}
	}
	
	@Test
	public void DeathsBite_play() {
		_play("Death's Bite")
		_attack(p1.hero, p2.hero)
		assert p2.hero.health == 26
		assert p1.hero.weapon != null
		assert p1.hero.weapon.durability == 1
	}

	@Test
	public void DeathsBite_destroy() {
		/* Player A */
		def archer1 = _play("Elven Archer", p2.hero)
		_play("Death's Bite")
		_attack(p1.hero, p2.hero)
		assert p2.hero.health == 25
		assert p1.hero.weapon != null
		assert p1.hero.weapon.durability == 1
		_next_turn()
		
		/* Player B */
		def archer2 = _play("Elven Archer", p2.hero)
		_next_turn()
		
		/* Player A */
		_attack(p1.hero, p2.hero)
		assert p2.hero.health == 21
		assert p1.hero.weapon == null
		assert archer1.is_dead()
		assert archer2.is_dead()
		
	}

	@Test
	public void Execute_fail_no_enemy_target() {
		// Destroy a damaged enemy minion.
		// test 2 : une cible pas bless�e
		def abo = _play("Abomination", p1)
		abo.set_health( abo.get_max_health() -1 ) // hurt
		try {
			def exe = _play_and_target("Execute", abo)
			fail("aurait d� planter : pas de cible valide")
		}
		catch (Exception e) {
			println e // OK
		}
	}

	@Test
	public void Execute_ok() {
		// Destroy a damaged enemy minion.
		// test 3 : une cible bless�e
		def abo = _play("Abomination", p2)
		abo.set_health( abo.get_max_health() -1 ) // hurt
		def exe = _play_and_target("Execute", abo)
		assert abo.is_dead()
	}
	
	@Test
	public void FrothingBerserker_receive_damage() {
		// Whenever a minion takes damage, gain +1 Attack
		def fro = _play("Frothing Berserker")
		def ctm = _play_and_target("Cruel Taskmaster", fro) // 1 dam +2 Att
		assert fro.health == fro.card_definition.max_health -1
		assert fro.get_attack() == fro.card_definition.attack + 3
		_play("Whirlwind") // 1 dam to ALL minions
		assert fro.health == fro.card_definition.max_health -2
		assert fro.get_attack() == fro.card_definition.attack + 5
	}

	@Test
	public void Gorehowl_play() {
		// Attacking a minion costs 1 Attack instead of 1 Durability
		def bou = _play("Boulderfist Ogre")
		_next_turn()
		_play("Gorehowl")
		def gor = p1.hero.weapon
		assert gor.name == "Gorehowl"
		assert gor.attack == 7
		assert gor.durability == 1
		// test attacking a minion
		_attack(p1.hero, bou)
		assert bou.is_dead()
		assert gor.attack == 6
		assert gor.durability == 1
		_next_turn()
		_next_turn()
		// test attacking a hero
		_attack(p1.hero, p2.hero)
		assert p1.hero.weapon == null // 0 durability 
	}
	
	@Test
	public void GrommashHellscream_play() {
		// Charge. Enrage: +6 Attack
		def shb = _play("Shieldbearer")
		def lep = _play("Leper Gnome")
		_next_turn()
		def gro = _play("Grommash Hellscream")
		assert gro.has_charge()
		assert gro.has_buff("+6 Attack") == false
		assert gro.get_attack() == gro.attack
		_attack(gro, shb) // check charge
		assert shb.is_dead()
		assert gro.get_attack() == gro.attack
		_next_turn()
		_next_turn()
		_attack(gro, lep)
		assert lep.is_dead()
		assert gro.health < gro.max_health
		assert gro.has_buff("+6 Attack") == true // check enrage effect
		_play("Ancestral Healing", gro) // full health, should remove enrage effect	
		assert gro.health == gro.max_health
		assert gro.has_buff("+6 Attack") == false
	}
	
	@Test
	public void HeroicStrike_play() {
		// Give your hero +4 Attack this turn
		try {
			g.player_attacks(p1.hero, p2.hero)
			fail("should have failed: no attack power")
		}
		catch( Exception e ) {
			println e // ok
		}
		_play("Heroic Strike")
		g.player_attacks(p1.hero, p2.hero)
		assert p2.hero.health == 26
	}
	
	@Test
	public void InnerRage_ok() {
		// Deal 1 damage to a minion and give it +2 Attack
		def abo = _play("Abomination")
		_play_and_target("Inner Rage", abo)
		assert abo.health == abo.card_definition.max_health - 1
		assert abo.attack == abo.card_definition.attack
		assert abo.get_attack() == abo.card_definition.attack + 2
		assert abo.has_buff("+2 Attack")
	}
	
	@Test
	public void MortalStrike_deal_4_damage() {
		// Deal 4 damage. If your hero has 12 or less Health, deal 6 damage instead
		p1.hero.health = 13
		_play("Kobold Geomancer") // +1 Spell Damage
		_play("Mortal Strike", p2.hero)
		assert p2.hero.health == 30 -4 -1
	}
	
	@Test
	public void MortalStrike_deal_6_damage() {
		// Deal 4 damage. If your hero has 12 or less Health, deal 6 damage instead
		p1.hero.health = 12
		_play("Kobold Geomancer") // +1 Spell Damage
		_play("Mortal Strike", p2.hero)
		assert p2.hero.health == 30 -6 -1
	}
	
	@Test
	public void Rampage_play() {
		// Give a damaged minion +3/+3
		
		def kor = _play("Kor'kron Elite")
		_should_fail("no valid target") { _play("Rampage") } 
		
		def ctm = _play("Cruel Taskmaster", kor) // Battlecry: Deal 1 damage to a minion and give it +2 Attack
		assert kor.get_attack() == kor.attack + 2
		assert kor.get_health() == kor.max_health - 1
		
		_play("Rampage", kor)
		assert kor.get_attack() == kor.attack + 2 + 3
		assert kor.get_health() == kor.max_health - 1 + 3
		assert kor.get_max_health() == kor.max_health + 3
		assert kor.has_buff("+3/+3")
	}
	
	@Test
	public void ShieldBlock_play() {
		// Gain 5 Armor. Draw a card
		
		p1.hero.armor = 1
		def before_hand_size = p1.hand.size()
		_play("Shield Block")
		assert p1.hero.armor == 6
		assert p1.hand.size() == before_hand_size + 1
	}
	
	@Test
	public void ShieldSlam_no_weapon() {
		// Deal 1 damage to a minion for each Armor you have
		_play("Bluegill Warrior")
		_next_turn()
		try {
			_play("Shield Slam")
			fail("Should have failed: no armor")
		}
		catch (Exception e) {
			println e // ok
		}
	}

	@Test
	public void ShieldSlam_ok() {
		// Deal 1 damage to a minion for each Armor you have
		if (p1.hero.name == "Garrosh Hellscream") {
			_next_turn()
		}
		def kob = _play("Kobold Geomancer")
		_next_turn()
		// Garrosh
		_use_hero_power()
		assert p1.hero.armor == 2
		_play("Shield Slam", kob)
		assert kob.is_dead()
	}
	
	@Test
	public void Slam_play() {
		// Deal 2 damage to a minion. If it survives, draw a card
		
		// no valid target
		def fae = _play("Faerie Dragon")
		_next_turn()
		_should_fail("no valid target") { _play("Slam") }
		_next_turn()
		
		// valid targets
		def nor = _play("Northshire Cleric")	// health==3
		def abo = _play("Abomination")			// health==4
		_next_turn()
		
		_play("Kobold Geomancer")	// +1 Spell damage
		
		// do not survive
		def before_hand_size = p1.hand.size()
		_play("Slam", nor)		
		assert nor.is_dead()
		assert p1.hand.size() == before_hand_size
		
		// survive
		_play("Slam", abo)
		assert abo.is_dead() == false
		assert p1.hand.size() == before_hand_size + 1
	}
	
	@Test
	public void Upgrade_no_weapon() {
		// If you have a weapon, give it +1/+1. Otherwise equip a 1/3 weapon
		assert p1.hero.weapon == null
		_play("Upgrade!")
		assert p1.hero.weapon != null
		assert p1.hero.weapon.get_attack() == 1
		assert p1.hero.weapon.get_durability() == 3
	}

	@Test
	public void Upgrade_with_weapon() {
		// If you have a weapon, give it +1/+1. Otherwise equip a 1/3 weapon
		p1.hero.equip_weapon(2,3)
		assert p1.hero.weapon != null
		assert p1.hero.weapon.get_attack() == 2
		assert p1.hero.weapon.get_durability() == 3
		_play("Upgrade!")
		assert p1.hero.weapon != null
		assert p1.hero.weapon.get_attack() == 3
		assert p1.hero.weapon.get_durability() == 4
	}
	
	@Test
	public void WarsongCommander_play() {
		// Whenever you summon a minion with 3 or less Attack, give it Charge
		
		def war = _play("Warsong Commander")
		
		def abo = _play("Abomination")	// more that 4 Attack -> no effect
		assert abo.has_buff(CHARGE) == false
		
		def lep = _play("Leper Gnome")	// 2 attack -> effect
		assert lep.has_buff(CHARGE)
		_attack(lep, p2.hero)
		
		_next_turn()
		def voo = _play("Voodoo Doctor", p1.hero)
		assert p1.hero.health == 30
		assert voo.has_buff(CHARGE) == false // only your minions
	}
}
