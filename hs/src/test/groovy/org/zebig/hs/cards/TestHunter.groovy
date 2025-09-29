package org.zebig.hs.cards;

import org.zebig.hs.game.Card
import org.zebig.hs.mechanics.buffs.BuffType

import org.junit.Test

import org.zebig.hs.utils.TestHelper

class TestHunter extends TestHelper{

	/* Animal companion:  Summon a random Beast Companion */
	
	@Test
	public void AnimalCompanion_summon_Misha() {
		_next_random_int(0)
		_play("Animal Companion")
		assert p1.minions.size() == 1
		assert p1.minions[0].name == "Misha" // taunt
		assert p1.minions[0].has_taunt()
	}

	@Test
	public void AnimalCompanion_summon_Leokk() {
		_next_random_int(1)
		_play("Animal Companion")
		assert p1.minions.size() == 1
		assert p1.minions[0].name == "Leokk" // other friendly minions have +1 Attack
		def lep = _play("Leper Gnome")
		assert lep.attack == 2
		assert lep.get_attack() == 3
	}

	@Test
	public void AnimalCompanion_summon_Huffer() {
		_next_random_int(2)
		_play("Animal Companion")
		assert p1.minions.size() == 1
		assert p1.minions[0].name == "Huffer" // charge
		assert p1.minions[0].has_charge()
	}
	
	@Test
	public void AnimalCompanion_random() {
		_play("Animal Companion")
		assert p1.minions.size() == 1
	}
	
	@Test
	public void ArcaneShot_hero() {
		// Deal 2 damage
		_play_and_target("Arcane Shot", p2.hero)
		assert p2.hero.get_health() == 28
	}

	
	@Test
	public void BestialWrath_fails_no_target() {
		// Give a Beast +2 Attack and Immune this turn
		try {
			_play("Bestial Wrath")
			fail("should have failed: no target")
		}
		catch( Exception e ) {
			println e // OK
		}
	}

	@Test
	public void BestialWrath_fails_no_beast_in_play() {
		// Give a Beast +2 Attack and Immune this turn
		try {
			_play("Abomination")
			_play("Bestial Wrath")
			fail("should have failed: no target")
		}
		catch( Exception e ) {
			println e // OK
		}
	}

	@Test
	public void BestialWrath_ok() {
		// Give a Beast +2 Attack and Immune this turn
		def tiw = _play("Timber Wolf")
		_play_and_target("Bestial Wrath", tiw)
		assert tiw.get_attack() == tiw.card_definition.attack +2
		assert tiw.has_buff(BuffType.IMMUNE)
		g.end_turn()
		assert tiw.get_attack() == tiw.card_definition.attack
		assert tiw.has_buff(BuffType.IMMUNE) == false
	}
	
	@Test
	public void DeadlyShot_random() {
		// Destroy a random enemy minion
		def abo = _play("Abomination")
		_next_turn()
		def blu = _play("Bluegill Warrior")
		_play("Deadly Shot") // quite stupid, should kill indirectly blu
		assert abo.is_dead()
		assert blu.is_dead() // because of abo deathrattle
	}

	@Test
	public void EaglehornBow_play() {
		// Whenever a friendly Secret is revealed, gain +1 Durability
		def eag = _play("Eaglehorn Bow")
		def eag_weapon = p1.hero.weapon
		_next_turn()
		_play("Counterspell")
		_next_turn()
		_play("Arcane Shot", p2.hero) // should be countered
		assert p2.hero.health == 30
		assert p2.secrets.size() == 0
		// not one of your secrets => should have no effect
		assert eag_weapon.get_durability() == eag.card_definition.max_health
		_play("Explosive Trap")
		_next_turn()
		def blu = _play("Bluegill Warrior")
		g.player_attacks(blu, p2.hero) // should trigger explosive trap
		// one of your secrets => should increase weapon durability
		assert eag_weapon.get_durability() == eag.card_definition.max_health + 1
	}
	
	@Test
	public void ExplosiveShot_play_no_neighbor() {
		// Deal 5 damage to a minion and 2 damage to adjacent ones
		def abo = _play("Abomination")
		_next_turn()
		_play("Explosive Shot", abo)
		assert abo.is_dead()
	}

	
	@Test
	public void ExplosiveShot_play_1_right_neighbor() {
		// Deal 5 damage to a minion and 2 damage to adjacent ones
		def abo = _play("Abomination")
		def blu = _play("Bluegill Warrior")
		_next_turn()
		_play("Explosive Shot", abo)
		assert abo.is_dead()
		assert blu.is_dead()
	}

	@Test
	public void ExplosiveShot_play_1_left_neighbor() {
		// Deal 5 damage to a minion and 2 damage to adjacent ones
		def blu = _play("Bluegill Warrior")
		def abo = _play("Abomination")
		_next_turn()
		_play("Explosive Shot", abo)
		assert abo.is_dead()
		assert blu.is_dead()
	}
	
	@Test
	public void ExplosiveShot_play_2_neighbors() {
		// Deal 5 damage to a minion and 2 damage to adjacent ones
		def blu = _play("Bluegill Warrior")
		def abo = _play("Abomination")
		def fae = _play("Faerie Dragon")
		def lep = _play("Leper Gnome")
		_next_turn()
		_play("Explosive Shot", abo)
		assert abo.is_dead()
		assert blu.is_dead()
		assert fae.is_dead()
		assert lep.is_dead() // killed by abo deathrattle
	}
	
	@Test
	public void ExplosiveTrap_play() {
		// Secret: When your hero is attacked, deal 2 damage to all enemies
		_play("Explosive Trap")
		_next_turn()
		def blu = _play("Bluegill Warrior")
		g.player_attacks(blu, p2.hero) // should trigger explosive trap
		assert p2.hero.get_health() == 30 // blu should be dead before have dealing its damage
		assert blu.is_dead()
		assert p1.hero.get_health() == 28 // because of trap
		assert p2.secrets.size() == 0
	}
	
	@Test
	public void Flare_play() {
		// All minions lose Stealth. Destroy all enemy secret. Draw a card
		def bim = _play("Blood Imp")
		_play("Explosive trap")
		_next_turn()
		_play("Explosive trap")
		def bim2 = _play("Blood Imp")
		assert p1.secrets.size() != 0
		assert p2.secrets.size() != 0
		assert bim.has_buff(BuffType.STEALTH)
		assert bim2.has_buff(BuffType.STEALTH)
		def before_hand_size = p1.hand.size()
		_play("Flare")
		assert bim.has_buff(BuffType.STEALTH) == false
		assert bim2.has_buff(BuffType.STEALTH) == false
		assert p1.secrets.size() != 0
		assert p2.secrets.size() == 0
		assert p1.hand.size() == before_hand_size + 1
	}
	
	@Test
	public void FreezingTrap_play() {
		// Secret: When an enemy attacks, return it to its owner's hand and it cost (2) more
		_play("Freezing Trap")
		assert p1.secrets.size() == 1
		_next_turn()
		def blu = _play("Bluegill Warrior")
		_attack(blu, p2.hero)
		assert p2.hero.health == 30
		assert p2.secrets.size() == 0
		assert blu.is_in_play() == false
		assert p1.hand.contains(blu)
		assert blu.get_cost() == blu.cost + 2
		p1.available_mana = blu.card_definition.cost
		_should_fail("cost cannot be paid"){ p1.play(blu) }
		p1.available_mana = blu.card_definition.cost + 2
		p1.play(blu)
		assert blu.is_in_play()
	}
	
	@Test
	public void GladiatorsLongbow_play() {
		// Your hero is Immune while attacking
		def bou = _play("Boulderfist Ogre")
		_next_turn()
		_play("Gladiator's Longbow")
		assert p1.hero.weapon != null
		assert p1.hero.weapon.name == "Gladiator's Longbow"
		_attack(p1.hero, bou) // p1.hero should be immune
 		assert p1.hero.health == 30
		assert bou.health == bou.max_health - p1.hero.get_attack()
		_next_turn()
		_next_turn()
		p1.hero.equip_weapon(2, 2)
		_attack(p1.hero, bou) // p1.hero should NOT be immune
		assert p1.hero.health == 30 - bou.attack		
	}
	
	@Test
	public void Houndmaster_play() {
		// Battlecry: Give a friendly Beast +2/+2 and Taunt
		def owl = _play("Ironbeak Owl")
		_play("Houndmaster", owl)
		assert owl.has_buff("+2/+2")
		assert owl.has_taunt()
	}
	
	@Test
	public void HuntersMark_play() {
		// Change a minion's Health to 1 
		def c = _play("Abomination")
		assert c.get_health() == 4
		_play_and_target("Hunter's Mark", c)
		assert c.get_health() == 1
		assert c.has_buff("change health to 1")
		g.end_turn()
		assert c.get_health() == 1
	}
	
	@Test
	public void KillCommand_play() {
		// Deal 3 damage. If you have a Beast, deal 5 damage instead
		_play("Kobold Geomancer") // +1 Spell Damage
		
		// no beast
		_play("Kill Command", p2.hero)
		assert p2.hero.health == 30 - 3 - 1
		
		// with beast
		_play("Timber Wolf")
		_play("Kill Command", p2.hero)
		assert p2.hero.health == 30 - 3 - 1 - 5 - 1
	}
	
	@Test
	public void KingKrush_play() {
		// Charge
		def kkr = _play("King Krush")
		assert kkr.has_charge()
		_attack(kkr, p2.hero)
	}
	
	@Test
	public void Misdirection_no_target() {
		// Secret: When a character attacks your hero, instead he attacks another random character
		
		// no minion in play
		_play("Misdirection")
		assert p1.secrets.size() == 1
		assert p1.secrets[0].name == "Misdirection"
		_next_turn()
		p1.hero.equip_weapon(2,2)
		_attack(p1.hero, p2.hero)	// should NOT trigger Misdirection
		assert p1.hero.health == 30
		assert p2.hero.health == 28
		assert p2.secrets.size() != 0	// secret should still be in place
	}
	
	@Test
	public void Misdirection_one_target() {
		// Secret: When a character attacks your hero, instead he attacks another random character
		
		// only 1 possible target: attacker's hero
		_play("Misdirection")
		assert p1.secrets.size() == 1
		assert p1.secrets[0].name == "Misdirection"
		_next_turn()
		def blu = _play("Bluegill Warrior")
		_attack(blu, p2.hero) // should trigger Misdirection
		assert p1.hero.health == 30 - blu.attack
		assert p2.hero.health == 30
		assert p2.secrets.size() == 0
	}
	
	@Test
	public void Misdirection_2_targets() {
		// Secret: When a character attacks your hero, instead he attacks another random character

		def lep = _play("Leper Gnome") // another possible target		
		_play("Misdirection")
		assert p1.secrets.size() == 1
		assert p1.secrets[0].name == "Misdirection"
		_next_turn()
		def blu = _play("Bluegill Warrior")
		_attack(blu, p2.hero) // should trigger Misdirection
		assert p1.hero.health == 28 // either blu combat damage or lep deathrattle effect
		assert p2.hero.health == 30
	}
	
	@Test
	public void MultiShot_play() {
		// Deal 3 damage to two random enemy minions
		
		_play("Kobold Geomancer") // +1 Spell Damage
		_should_fail("not enough targets") { _play("Multi-Shot") }
		_next_turn()
		
		def fae = _play("Faerie Dragon")
		_next_turn()
		_should_fail("not enough targets") { _play("Multi-Shot") }
		_next_turn()

		def shb = _play("Shieldbearer")
		_next_turn()
		_play("Multi-Shot")
		assert fae.is_dead()
		assert shb.is_dead()
	}
	
	@Test
	public void SavannahHighmane_play() {
		// Deathrattle: Summon two 2/2 Hyenas
		
		def sav = _play("Savannah Highmane")	// 6/5
		_next_turn()
		def bou = _play("Boulderfist Ogre")		// 6/7
		_next_turn()
		_attack(sav, bou)	// sav should be killed
		assert sav.is_dead()
		assert p1.minions.size() == 2
		assert p1.minions[0].name == "Hyena"
		assert p1.minions[0].cost == 2
		assert p1.minions[0].attack == 2
		assert p1.minions[0].health == 2
		assert p1.minions[1].name == "Hyena"
		assert p1.minions[1].cost == 2
		assert p1.minions[1].attack == 2
		assert p1.minions[1].health == 2
	}
	
	@Test
	public void ScavengingHyena_play() {
		// Whenever a friendly Beast dies, gain +2/+1
		
		def owl = _play("Ironbeak Owl")
		def bou = _play("Boulderfist Ogre")		// 6/7
		_next_turn()
		def sca = _play("Scavenging Hyena")
		def tim = _play("Timber Wolf")
		
		// not a beast killed -> no effect
		def blu = _play("Bluegill warrior")
		_attack(blu, bou)
		assert blu.is_dead()
		assert sca.get_attack() == sca.attack + 1	// because of timber wolf
		assert sca.get_health() == sca.health
		
		// opponent kill a beast -> effect
		_next_turn()
		_attack(bou, tim)
		assert tim.is_dead()
		assert sca.get_attack() == sca.attack + 2 	
		assert sca.get_health() == sca.health + 1	
		
		// beast controlled by opponent (owl) killed -> no effect
		_next_turn()
		def blu2 = _play("Bluegill warrior")
		_attack(blu2, owl)
		assert owl.is_dead()
		assert sca.get_attack() == sca.attack + 2
		assert sca.get_health() == sca.health + 1
	}
	
	@Test
	public void SnakeTrap_play() {
		// Secret: When one of your minions is attacked, summon three 1/1 Snakes
		
		_play("Snake Trap")
		assert p1.secrets.size() == 1
		assert p1.secrets[0].name == "Snake Trap"
		
		def lep = _play("Leper Gnome")
		_next_turn()
		
		def blu = _play("Bluegill Warrior")
		_attack(blu, lep) // should trigger the snake trap
		assert blu.is_dead()
		assert lep.is_dead()
		assert p2.secrets.size() == 0
		assert p2.minions.size() == 3 // the 3 snakes
		(0..2).each {
			assert p2.minions[it].name == "Snake"
			assert p2.minions[it].cost == 0
			assert p2.minions[it].creature_type == "beast"
			assert p2.minions[it].attack == 1
			assert p2.minions[it].health == 1
			assert p2.minions[it].place == it
		}
	}
	
	@Test
	public void Snipe_play() {
		// Secret: When your opponent plays a minion, deal 4 damage to it
		
		_play("Snipe")
		assert p1.secrets.size() == 1
		assert p1.secrets[0].name == "Snipe"
		
		def blu = _play("Bluegill Warrior") // should not be affected
		assert blu.is_dead() == false
		
		// opponent plays a minion
		_next_turn()
		def abo = _play("Abomination") // should receive 4 damage
		assert abo.is_dead()
		assert p2.secrets.size() == 0
	}
	
	@Test
	public void StarvingBuzzard_play() {
		// Whenever you summon a Beast, draw a card
		
		_play("Starving Buzzard")
		def before_hand_size = p1.hand.size()
		
		_play("Arcane Shot", p2.hero)	// not a minion -> should not trigger
		assert p1.hand.size() == before_hand_size
		
		_play("Leper Gnome")	// not a beast -> should not trigger
		assert p1.hand.size() == before_hand_size
		
		_play("Timber Wolf")	// should trigger
		assert p1.hand.size() == before_hand_size + 1
		
		_play("Animal Companion")	// should trigger
		assert p1.hand.size() == before_hand_size + 2
		
		_next_turn()
		before_hand_size = p2.hand.size()
		_play("River Crocolisk")	// a beast but not summoned by you
		assert p2.hand.size() == before_hand_size

		//TODO test with unleash the hounds
	}
	
	@Test
	public void TimberWolf_play() {
		// Your other Beasts have +1 Attack
		Card abo = _play("Abomination") // non-beast
		Card huf = _play("Huffer") // a beast
		assert abo.get_attack() == 4
		assert huf.get_attack() == 4
		Card two = _play("Timber Wolf")
		assert abo.get_attack() == 4
		assert huf.get_attack() == 5
		assert two.get_attack() == 1
		two.dies()
		assert abo.get_attack() == 4
		assert huf.get_attack() == 4
	}
	
	@Test
	public void Tracking_play() {
		// Look at the top three cards of your deck. Draw one and discard the others.
		
		p1.deck.cards.clear()
		_should_fail("not enough cards in your deck") {
			_play("Tracking")
		}
		
		def c1 = g.new_card("Timber Wolf")
		def c2 = g.new_card("Leper Gnome")
		def c3 = g.new_card("Explosive Trap")
		p1.deck.cards.add(0,c1)
		p1.deck.cards.add(0,c2)
		p1.deck.cards.add(0,c3)
		
		p1.hand.cards.clear()
		
		_play("Tracking", c3)
		assert p1.deck.isEmpty()
		assert p1.hand.size() == 1
		assert p1.hand.contains(c3)
	}

	@Test
	public void TundraRhino_play() {
		// Your Beasts have Charge
		
		def cro = _play("River Crocolisk")	// for opponent
		assert cro.is_a_beast()
		assert cro.has_charge() == false
		_next_turn()
		
		def tim = _play("Timber Wolf")
		assert tim.is_a_beast()
		assert tim.has_charge() == false
		
		def tun = _play("Tundra Rhino")
		assert tim.has_charge() == true
		assert cro.has_charge() == false 	// not one of your beasts
		
		def lep = _play("Leper Gnome")	// not a beast
		assert lep.has_charge() == false
		
		_next_turn()
		_play("Silence", tun)
		assert tun.has_charge() == false
		assert tim.has_charge() == false
	}
	
	@Test
	public void UnleashtheHounds_play() {
		// For each enemy minion, summon a 1/1 Hound with Charge
		
		// summon enemy minions
		_play("Faerie Dragon")
		_play("Leper Gnome")
		_play("Blood Imp")
		_next_turn()
		
		_play("Unleash the Hounds")
		assert p1.minions.size() == p2.minions.size()
		p1.minions.each { Card minion ->
			assert minion.name == "Hound"
			assert minion.cost == 1
			assert minion.creature_type == "beast"
			assert minion.attack == 1
			assert minion.health == 1
			assert minion.has_charge()
		}
	}
	
	@Test
	public void Webspinner_play() {
		/* Player A */
		def webspinner = _play("Webspinner")
		p1.hand.cards.clear()
		_next_turn()
		
		/* Player B */
		def blu = _play("Bluegill Warrior")
		_attack(blu,webspinner)
		assert webspinner.is_dead()

		assert p2.hand.size() == 1
		assert p2.hand.cards[0].creature_type == "beast"		
	}
}
