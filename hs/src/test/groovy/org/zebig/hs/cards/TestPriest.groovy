package org.zebig.hs.cards


import static org.junit.Assert.fail;
import static org.zebig.hs.mechanics.buffs.BuffType.*
import org.zebig.hs.game.AnduinWrynn
import org.zebig.hs.game.Game
import org.zebig.hs.game.MalfurionStormrage
import org.zebig.hs.game.Player

import org.junit.Before
import org.junit.Test

import org.zebig.hs.utils.TestHelper
import org.zebig.hs.decks.AnduinDeck1
import org.zebig.hs.decks.MalfurionDeck1

class TestPriest extends TestHelper {
	
	@Before
	void newGame() {
		_create_game( 
			"Didier", AnduinWrynn.class, AnduinDeck1.class,
			"Titou",  MalfurionStormrage.class, MalfurionDeck1.class)	
	}
	
	@Test
	void AuchenaiSoulpriest_ok() {
		// Your cards and powers that restore Health now deal damage instead
		def asp = _play("Auchenai Soulpriest")
		p1.hero.set_health(20)
		_play_and_target("Healing Touch", p1.hero)  // normally restore hero health for 8
		assert p1.hero.get_health() == 12 // damage dealt instead of healing
	}

	@Test
	void AuchenaiSoulpriest_dies_remove_effect() {
		// Your cards and powers that restore Health now deal damage instead
		def asp = _play("Auchenai Soulpriest")
		p1.hero.set_health(20)
		_play_and_target("Healing Touch", p1.hero)
		assert p1.hero.get_health() == 12 // damage dealt instead of healing
		asp.dies()
		_play_and_target("Healing Touch", p1.hero)		// normal healing
		assert p1.hero.get_health() == 20
	}
	
	@Test
	void CabalShadowPriest_no_target() {
		// Battlecry: Take control of an enemy minion that has 2 or less Attack
		def bfo = _play("Boulderfist Ogre") // 6/7
		def tiw = _play("Timber Wolf")		// 1/1
		_next_turn()
		_play("Cabal Shadow Priest", tiw)
		assert tiw.controller == p1
		assert bfo.controller == p2
	}

	@Test
	void CabalShadowPriest_take_control() {
		// Battlecry: Take control of an enemy minion that has 2 or less Attack
		def tiw = _play("Timber Wolf")
		def bfo = _play("Boulderfist Ogre")
		_next_turn()
		def csp =_play("Cabal Shadow Priest", tiw)
		assert tiw.controller == p1
		assert bfo.controller == p2
		assert p1.minions.contains(tiw)
		assert p1.minions.contains(csp)
		assert !p2.minions.contains(tiw)
	}
	
	@Test
	void DarkCultist_play() {
		def gnome = _play("Leper Gnome")
		def blu = _play("Bluegill warrior")
		def cultist = _play("Dark Cultist")
		cultist.dies()
		assert gnome.get_health() == gnome.health+3 || blu.get_health() == blu.health+3
		assert gnome.get_health() == gnome.health || blu.get_health() == blu.health
	}	

	
	@Test
	void DivineSpirit_play() {
		// Double a minion's Health
		def blu = _play("Bluegill Warrior")
		_play("Divine Spirit", blu)
		assert blu.get_health() == blu.card_definition.max_health * 2
		assert blu.has_buff("+1 Health")
	}
	
	@Test
	void HolyFire_play() {
		// Deal 5 damage. Restore 5 Health to your hero
		p1.hero.health=27
		_play("Kobold Geomancer")  // +1 Spell Damage
		_play("Holy Fire", p2.hero)
		assert p1.hero.health == 30
		assert p2.hero.health == 24
	}
	
	@Test
	void HolyNova_play() {
		// Deal 2 damage to all enemies. Restore 2 Health to all friendly characters
		def shb = _play("Shieldbearer")
		def fae = _play("Faerie Dragon")
		_next_turn()
		def abo = _play("Abomination")
		def sh2 = _play("Shieldbearer")
		def kob = _play("Kobold Geomancer") // +1 Spell damage
		abo.health = abo.max_health -1
		sh2.health = sh2.max_health -2
		p1.hero.health = 20
		_play("Holy Nova")	
		assert shb.health == shb.max_health -3
		assert fae.is_dead()
		assert p2.hero.health == 27
		assert p1.hero.health == 22
		assert abo.health == abo.max_health
		assert sh2.health == sh2.max_health	
	}
	
	@Test
	void HolySmite_play() {
		// Deal 2 damage
		def bou = _play("Boulderfist Ogre")
		_next_turn()
		def kob = _play("Kobold Geomancer") // +1 Spell damage
		_play("Holy Smite", p2.hero)
		assert p2.hero.health == 27
		_play("Holy Smite", bou)
		assert bou.health == bou.max_health - 3
	}
	
	@Test
	void InnerFire_play() {
		// Change a minion's Attack to be equal to it's Health
		def c = _play("Alarm-O-Bot")
		assert c.get_attack() < c.get_health()
		_play_and_target("Inner Fire", c)
		assert c.get_attack() == c.get_health()
	}

	@Test
	void InnerFire_fail_no_minion() {
		// Change a minion's Attack to be equal to it's Health
		try {
			_play("Inner Fire")
			fail("should have failed: no target")
		}
		catch( Exception e) {
			println e // OK
		}
	}
	
	@Test
	void Lightspawn_play() {
		// This minion's Attack is always equal to it's Health
		def lsp = _play("Lightspawn") // 0*/5
		assert lsp.get_health() == 5
		assert lsp.health == 5
		assert lsp.get_max_health() == 5
		assert lsp.max_health == 5
		assert lsp.get_attack() == 5		// = get_health()
		assert lsp.attack == 0
		_next_turn()
		// health reduced by combat damage
		def arp = _play("Argent Protector") // 2/2
		_next_turn()
		_attack(lsp, arp)
		assert lsp.get_health() == 3
		assert lsp.health == 3
		assert lsp.get_max_health() == 5
		assert lsp.max_health == 5
		assert lsp.get_attack() == 3
		assert lsp.attack == 0
		// health increased by spell
		_play("Divine Spirit", lsp) // Double a minion's Health -> gets a +3 Health
		assert lsp.get_health() == 6		// 3 + 3
		assert lsp.health == 3
		assert lsp.get_max_health() == 8 	// 5 + 3
		assert lsp.max_health == 5
		assert lsp.get_attack() == 6		// = get_health()
		assert lsp.attack == 0
		// health increased by buff
		_play("Bananas", lsp) // Give a minion +1/+1
		assert lsp.get_health() == 7		// 3 + 3 + 1
		assert lsp.health == 3
		assert lsp.get_max_health() == 9	// 5 + 3 + 1
		assert lsp.max_health == 5
		assert lsp.get_attack() == 8		// get_health() + 1 (Bananas)
		assert lsp.attack == 0
		// health reduced by spell
		_play("Holy Smite", lsp) // Deal 2 damage
		assert lsp.get_health() == 5		// 3 + 3 + 1 - 2
		assert lsp.health == 1
		assert lsp.get_max_health() == 9	// 5 + 3 + 1
		assert lsp.max_health == 5
		assert lsp.get_attack() == 6		// get_health() + 1
		assert lsp.attack == 0
		// silence
		_play("Silence", lsp)
		assert lsp.get_health() == 1
		assert lsp.health == 1
		assert lsp.get_max_health() == 5
		assert lsp.max_health == 5
		assert lsp.get_attack() == 0		// = base attack
		assert lsp.attack == 0
	}
	
	@Test
	void Lightwell_play() {
		// At the start of your turn, restore 3 Health to a damaged friendly character
		/* PLAYER A */
		def lsp = _play("Lightspawn")
		def liw = _play("Lightwell")
		def lwa = _play("Lightwarden")
		_next_turn()
		/*----- PLAYER B */
			def arp = _play("Argent Protector") // 2/2
			_next_turn()
		/* PLAYER A */
		_attack(lsp, arp) // Lifespawn should get 2 damage -> 3/3
		_next_turn() // should NOT trigger lightwell effect
		/*----- PLAYER B */
		assert lsp.get_health() == 3
		def abo = _play("Abomination")
		_next_turn() // should trigger lightwell effect
		/* PLAYER A */
		assert lsp.get_health() == 5 // should be healed by lightwell
		assert lwa.get_attack() == 1 + 2 // base attack + gain 2 attack
		_attack(lsp, abo)
		assert abo.is_dead()	// deathrattle: deal 2 damage to ALL characters
		assert lsp.is_dead()	// -4 combat damage -2 abo deathrattle
		assert lwa.is_dead()	// -2 abo deathrattle
		assert liw.health == liw.max_health - 2 // -2 abo deathrattle
		assert p1.hero.health == 28
		assert p2.hero.health == 28
	}
	
	@Test
	void MassDispel_play() {
		// Silence all enemy minions. Draw a card.
		def lep = _play("Leper Gnome")
		def fae = _play("Faerie Dragon")
		_next_turn()
		def before_hand_size = p1.hand.size()
		assert lep.text != ''
		assert fae.text != ''
		_play("Mass Dispel")
		assert lep.text == ''
		assert fae.text == ''
		_play("Holy Smite", lep) 		// Deal 2 damage
		assert lep.is_dead()
		assert p1.hero.health == 30		// Leper Gnome deathrattle should have been removed
		_play("Holy Smite", fae)		// fae should be now a valid target
		assert fae.is_dead()
		assert p1.hand.size() == before_hand_size + 1 // draw effect
	}

	@Test
	void MindBlast_play() {
		// Deal 5 damage to the enemy hero
		_play("Mind Blast")
		assert p2.hero.health == 25
		
		// check +x Spell Damage
		_play("Kobold Geomancer")
		_play("Mind Blast")
		assert p2.hero.health == 25 -5 -1
	}
	
	@Test
	void MindControl_play() {
		// Take control of an enemy minion
		
		// Simple minion
		def abu = _play("Abusive Sergeant")
		def ang = _play("Angry chicken") 	// to test mc + attack later
		_next_turn()
		def lep = _play("Leper Gnome") 		// just to check abu place will be 1
		assert abu.controller == p2
		assert p2.minions.contains(abu)
		_play("Mind Control", abu) 	// abu battlecry effect doesn't trigger
		assert abu.controller == p1
		assert p1.minions.contains(abu)
		assert !p2.minions.contains(abu)
		assert abu.place == 1 		// right of abu
		
		// check abu cannot attack (just summoned)
		_should_fail("cannot attack") { _attack(abu, p2.hero) }
		
		// minion with permanent effect
		_next_turn()
		def loo = _play("Loot Hoarder")
		def cha = _play("Stormwind Champion") // Your other minions have +1/+1
		assert loo.get_attack() == loo.attack + 1
		assert loo.get_health() == loo.health + 1
		_next_turn()
		_play("Mind Control", cha)
		assert loo.get_attack() == loo.attack
		assert loo.get_health() == loo.health
		assert abu.get_attack() == abu.attack + 1
		assert abu.get_health() == abu.health + 1
		
		// minion with position effect
		_next_turn()
		def tot = _play("Flametongue Totem") // 'Adjacent minions have +2 Attack', right of Loot Hoarder
		assert loo.get_attack() == loo.attack + 2
		_next_turn()
		_play("Mind Control", tot) // right of flametongue totem
		assert loo.get_attack() == loo.attack
		assert cha.get_attack() == cha.attack + 2
		
		// silence a controlled minion will not cancel the mind control effect
		_next_turn()
		_play("Silence", cha)
		assert cha.controller == p2
		
		// check mc with attack counter = 0 can attack as soon as mind-controlled
		assert ang.attack_counter == 0
		assert !ang.just_summoned
		_next_turn()
		_play("Mind control", ang)
		_attack(ang, p2.hero)
		assert p2.hero.health == 30 - ang.get_attack()
		assert ang.attack_counter == 1

		// return to current controller's hand, not initial
		_next_turn()
		 p1.hand.cards.clear() // to avoid exceeding 10 cards
		 p2.hand.cards.clear() // to avoid exceeding 10 cards
		 _play("Vanish") // Return all minions to their owner's hand
		 assert p1.minions.size() == 0
		 assert p2.minions.size() == 0
		 assert abu.controller == p2 // mind controlled
		 assert p2.hand.contains(abu)
		 assert lep.controller == p2 
		 assert p2.hand.contains(lep)
		 assert loo.controller == p1 // not mind controlled
		 assert p1.hand.contains(loo)
		 assert cha.controller == p2 // mind controlled
		 assert p2.hand.contains(cha)
		 assert tot.controller == p2 // mind controlled
		 assert p2.hand.contains(tot)
		 
		 // a silenced then mind-controlled minion will not trigger its 'coming in play' effect
		 def shb = _play("Shieldbearer")
		 assert shb.has_taunt()
		 _next_turn()
		 _play("Silence", shb)
		 assert !shb.has_taunt()
		 _play("Mind control", shb)
		 assert !shb.has_taunt()
	}
	
	@Test
	void Mindgames_one_minion_in_opponent_deck() {
		// Put a copy of a random minion from your opponent's deck into the battlefield
		def c1 = g.new_card("Arcanite Reaper") // not a minion
		def c2 = g.new_card("Execute") // not a minion
		def c3 = g.new_card("Molten Giant") // a minion ;)
		p1.deck.cards.clear()
		p1.deck.add(c1)
		p1.deck.add(c2)
		p1.deck.add(c3)
		_next_turn()
		_play("Mindgames")
		assert p2.deck.cards.size() == 3 // create a copy, do not steal
		assert p1.minions.size() == 1
		assert p1.minions[0].name == "Molten Giant" // lucky
	}
	
	@Test
	void Mindgames_no_minion_in_opponent_deck() {
		// Put a copy of a random minion from your opponent's deck into the battlefield		
		def c1 = g.new_card("Arcanite Reaper") // not a minion
		def c2 = g.new_card("Execute") // not a minion
		p1.deck.cards.clear()
		p1.deck.add(c1)
		p1.deck.add(c2)
		_next_turn()
		_play("Mindgames")
		assert p2.deck.cards.size() == 2 // no change
		assert p1.minions.size() == 1
		assert p1.minions[0].name == "Shadow of Nothing" // not lucky ;)
	}
	
	@Test
	void MindVision_play() {
		// Put a copy of a random card in your opponent's hand into your hand
		
		// no card in opponent's hand
		p2.hand.cards.clear()
		_should_fail("no card in opponent's hand") { _play("Mind Vision") }
		
		// test with 1 card in hand
		p1.hand.cards.clear()
		p2.hand.cards.add  g.new_card("The Coin")
		_play("Mind Vision")
		assert p1.hand.size() == 1
		assert p2.hand.size() == 1 // copy, not steal
		assert p1.hand.cards[0].name == "The Coin"

		// test with 2 cards in hand
        p1.hand.cards.clear()
		p2.hand.cards.clear()
		p2.hand.cards.add g.new_card("The Coin")
        p2.hand.cards.add g.new_card("Abomination")
		_play("Mind Vision")
		assert p1.hand.size() == 1
		assert p2.hand.size() == 2 // copy, not steal
		assert p1.hand.cards[0].name in [ "The Coin", "Abomination" ]
	}
	
	@Test
	void ShadowMadness_take_control() {
		// Gain control of an enemy minion with 3 or less Attack until end of turn
		def lep = _play("Leper Gnome")
		def first_controller = lep.controller
		_next_turn()
		_play_and_target( "Shadow Madness", lep )
		assert lep.controller != first_controller
		assert !first_controller.minions.contains(lep)
		assert p1.minions.contains(lep)
		try {
			Game.player_attacks(lep, first_controller.hero)
			fail("should fail: just summoned")
		}
		catch ( Exception e ) {
			println e // ok
		}
	}
	
	@Test
	void ShadowWordPain_play() {
		// Destroy a minion with 3 or less Attack
		
		// no valid target
		_play("Jungle Panther") 	// stealth, not a valid target
		_play("Faerie Dragon")		// cannot be targeted
		_play("Boulderfist Ogre")	// attack > 3
		_next_turn()
		_should_fail("no valid target") { _play("Shadow Word: Pain") }
		_next_turn()
		
		// 1 valid target
		def aci = _play("Acidic Swamp Ooze")	// 3/2
		_next_turn()
		_play("Shadow Word: Pain", aci)
		assert aci.is_dead()
	}
	
	@Test
	void NorthshireCleric_play() {
		if (p1.hero.name != 'Anduin Wrynn') {
			_next_turn()
			assert p1.hero.name == 'Anduin Wrynn'
		}
		// Whenever a minion is healed, draw a card
		_play("Northshire Cleric")
		def abo = _play("Abomination")
		abo.health = abo.max_health - 1
		p1.next_choices = [ abo ]
		def before_hand_size = p1.hand.size()
		_use_hero_power()
		assert abo.health == abo.max_health
		assert p1.hand.size() == before_hand_size + 1
		
		// check that healing a hero doesn't trigger the effect
		_next_turn()
		_next_turn()
		p1.hero.health = 20
		p1.next_choices = [ p1.hero ]
		before_hand_size = p1.hand.size()
		_use_hero_power()
		assert p1.hero.health == 22
		assert p1.hand.size() == before_hand_size // no draw
	}
	
	@Test
	void PowerWordShield_play() {
		// Give a minion +2 Health. Draw a card
		
		// no valid target
		_play("Faerie Dragon")	// cannnot be targeted by spells
		_should_fail("no valid target") { _play("Power Word: Shield") }
		
		// 1 valid target
		def blu = _play("Bluegill Warrior")
		def before_hand_size = p1.hand.size()
		_play("Power Word: Shield", blu)
		assert blu.get_max_health() == blu.max_health + 2
		assert blu.get_health() == blu.health + 2
		assert p1.hand.size() == before_hand_size + 1
	}

	@Test
	void ShadowMadness_end_of_turn() {
		// Gain control of an enemy minion with 3 or less Attack until end of turn
		def lep = _play("Leper Gnome")
		def first_controller = lep.controller
		_next_turn()
		_play_and_target( "Shadow Madness", lep )
		_next_turn() // first controller should regain control
		assert lep.controller == first_controller
		assert first_controller.minions.contains(lep)
	}
	
	@Test
	void Silence_single_buff() {
		// Silence a minion
		def abo = _play("Abomination")
		assert abo.has_taunt()
		_play_and_target("Silence", abo)
		assert !abo.has_taunt()
	}

	@Test
	void Silence_multiple_buffs() {
		// Silence a minion
		def aaw = _play("Al'Akir the Windlord")
		assert aaw.has_taunt()
		assert aaw.has_buff( WINDFURY )
		assert aaw.has_charge()
		assert aaw.has_divine_shield()
		assert aaw.has_taunt()
		_play("Silence", aaw)
		assert !aaw.has_taunt()
		assert !aaw.has_buff(WINDFURY)
		assert !aaw.has_charge()
		assert !aaw.has_divine_shield()
		assert !aaw.has_taunt()
	}
	
	@Test
	void Shadowform_play() {
		// Your Hero Power becomes 'Deal 2 damage'. If already in Shadowform: 3 damage
		
		/*
		 * 1. You start with Lesser Heal (2 Heal).
		 * 2. After using Shadowform, it becomes Mind Spike (2 Damage).
		 * 3. After using Shadowform again, it becomes Mind Shatter (3 Damage).
		 * Because the 3 hero powers are considered DIFFERENT hero powers,
		 * you can use Lesser Heal, cast Shadowform, THEN use Mind Spike all on the same turn.
		 */
		p1 = new Player( g, "Didou", new AnduinWrynn(g), new AnduinDeck1(g) )
		g.players = [p1, p2]
		g.active_player = p1
		g.passive_player = p2
		
		// check lesser heal is ok
		assert p1.hero.power.name == "Lesser Heal"
		p1.hero.health = 18
		p1.next_choices = [ p1.hero ]
		_use_hero_power()
		assert p1.hero.health == 20
		p1.next_choices = [ p1.hero ]
		_should_fail("Cannot use power") { _use_hero_power() }
		
		// from lesser heal to mind spike
		_play("Shadowform")
		assert p1.hero.power.name == "Mind Spike"
		p1.next_choices = [ p2.hero ]
		_use_hero_power()
		assert p2.hero.health == 28

		
		// from mind spike to mind shatter
		_play("Shadowform")
		assert p1.hero.power.name == "Mind Shatter"
		p1.next_choices = [ p2.hero ]
		_use_hero_power()
		assert p2.hero.health == 28 - 3
	}
	
	@Test
	void ProphetVelen_play() {
		// Double the damage and healing of your spells and hero power
		
		// check Priest's healing power
		if (p1.hero.name != 'Anduin Wrynn') {
			_next_turn()
			assert p1.hero.name == 'Anduin Wrynn'
		}
		_play("Prophet Velen")
		p1.hero.health = 10
		p1.next_choices = [ p1.hero ]
		_use_hero_power() // heals himself
		assert p1.hero.health == 10 + 2*2
		
		// check spell healing and spell damage
		_play("Holy Fire", p2.hero) // Deal 5 damage. Restore 5 Health to your hero
		assert p2.hero.health == 30 - 5*2
		assert p1.hero.health == 14 + 5*2
		
		// +1 Spell Damage, applied before
		// explanation: +x Spell damage buffs are checked before triggers
		_play("Kobold Geomancer")
		_play("Holy Fire", p2.hero)
		assert p2.hero.health == 20 - ((5 + 1) *2) // and not 20 - ((5 * 2) + 1)		
	}
	
	@Test
	void TempleEnforcer_play() {
		// Battlecry: Give a friendly minion +3 Health
		
		// no minion
		def te1 = _play("Temple Enforcer")
		assert te1.is_in_play()
		
		// 1 other minion
		def bou = _play("Boulderfist Ogre")
		def te2 = _play("Temple Enforcer", bou)
		assert bou.get_max_health() == bou.max_health + 3
		assert bou.get_health() == bou.health + 3
	}
	
	@Test
	void Thoughtsteal_empty_deck() {
		// Copy 2 cards from your opponents deck and put them into your hand
		
		p2.deck.cards.clear()
		_should_fail("not enough cards in opponent's deck") { _play("Thoughtsteal") }
	}
	
	@Test
	void Thoughtsteal_non_empty_deck() {
		// Copy 2 cards from your opponents deck and put them into your hand

		p2.deck.cards.clear()
		def c1 = g.new_card("Assassinate")		
		def c2 = g.new_card("Headcrack")		
		p2.deck.cards.add(0,c1)
		p2.deck.cards.add(0,c2)
		p1.hand.cards.clear()
		_play("Thoughtsteal")
		assert p1.hand.size() == 2
		assert p1.hand.cards[0].name in [ "Assassinate", "Headcrack" ]
		assert p1.hand.cards[1].name in [ "Assassinate", "Headcrack" ]
	}
	
}
