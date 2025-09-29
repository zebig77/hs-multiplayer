package org.zebig.hs.cards

import static org.zebig.hs.mechanics.buffs.BuffType.*
import org.zebig.hs.game.Card

import org.junit.Test
import org.zebig.hs.utils.TestHelper

class TestPaladin extends TestHelper {
	
		@Test
	void ArgentProtector_battlecry() {
		// 'Battlecry: Give a friendly minion Divine Shield
		Card abo = _play("Abomination")
		_play_and_target "Argent Protector", abo
		assert abo.has_buff(DIVINE_SHIELD)
	}

	@Test
	void ArgentProtector_no_friendly_minion() {
		// 'Battlecry: Give a friendly minion Divine Shield
		Card abo = g.summon(p2, "Abomination" )
		_play("Argent Protector")
		assert !abo.has_buff(DIVINE_SHIELD)
	}
	
	@Test
	void Avenge_play() {
		/* Player A */
		def protector1 = _play("Argent Protector")
		def protector2 = _play("Argent Protector", protector1)
		assert protector1.has_divine_shield()
		_play("Avenge")
		assert p1.secrets.size() == 1
		assert p1.secrets[0].name == "Avenge"
		_next_turn()
		
		/* Player B */
		def blu = _play("Bluegill Warrior")
		def before_attack = protector1.get_attack()
		def before_health = protector1.get_health()
		_attack(blu,protector2) // should trigger the secret
		assert p2.secrets.size() == 0
		assert protector1.get_attack() == before_attack + 3  
		assert protector1.get_health() == before_health + 2  
	}
	
	@Test
	void AvengingWrath_hit_only_hero() {
		// Deal 8 damage randomly split among enemy characters
		_play("Avenging Wrath")
		assert p2.hero.get_health() == 22
	}

	@Test
	void AvengingWrath_several_targets() {
		// Deal 8 damage randomly split among enemy characters
		def fad = g.summon(p2, "Faerie Dragon")
		def asq = g.summon(p2, "Argent Squire")
		_play("Avenging Wrath")
		// try to guess how many damages dealt to hero
		def damage_dealt_to_hero = 8
		if (fad.is_dead()) {
			damage_dealt_to_hero -= 2 // Faerie dragon killed : -2
		} else {
			// Faerie hurt or untouched
			damage_dealt_to_hero -= (fad.card_definition.max_health - fad.get_health())
		}
		if (asq.is_dead()) {
			// 1 for shield, 1 for life
			damage_dealt_to_hero -= 2
		} else {
			if (!asq.has_buff(DIVINE_SHIELD)) {
				// 1 for shield
				damage_dealt_to_hero -= 1
			}
		}
		assert p2.hero.get_health() == 30 - damage_dealt_to_hero
	}
	
	@Test
	void BlessedChampion_play() {
		// Double a minion's Attack
		def abo = _play("Abomination")
		_play_and_target("Blessed Champion", abo)
		assert abo.get_attack() == abo.card_definition.attack * 2
		assert abo.get_attack() == abo.attack * 2
	}
	
	@Test
	void BlessingOfKings() {
		// Give a minion +4/+4
		def tiw = _play( "Timber Wolf")
		_play_and_target( "Blessing of Kings", tiw )
		assert tiw.get_attack() == tiw.card_definition.attack + 4
		assert tiw.get_health() == tiw.card_definition.max_health + 4
	}
	
	@Test
	void BlessingOfWisdom_attacks_draw() {
		// Choose a minion. Whenever it attacks, draw a card
		def kke = _play("Kor'kron Elite")
		_play_and_target( "Blessing of Wisdom", kke)
		def before_hand_size = p1.hand.size()
		g.player_attacks(kke, p2.hero)
		assert p1.hand.size() == before_hand_size + 1
		assert p2.hero.health == 26
	}
	
	@Test
	void Consecration_hero_spell_damage_increase() {
		// Deal 2 damage to all enemies
		_play("Kobold Geomancer")
		_play_and_target("Consecration", p2.hero)
		assert p2.hero.get_health() == 27
	}

	@Test
	void Consecration_hero_and_minions_spell_damage_increase() {
		// Deal 2 damage to all enemies
		def abo = _play("Abomination", p2)
		def lep = _play("Leper Gnome", p2)
		_play("Kobold Geomancer")
		_play_and_target("Consecration", p2.hero)
		assert p2.hero.get_health() == 27
		assert abo.get_health() == abo.card_definition.max_health - 3
		assert lep.is_dead()
	}
	
	@Test
	void DivineFavor_play() {
		// Draw cards until you have as many in hand as your opponent
		p1.hand.cards.clear()
		assert p2.hand.size() > 0
		_play("Divine Favor")
		assert p1.hand.size() == p2.hand.size()
	}
	
	@Test
	void Equality_play() {
		// Change the Health of ALL minions to 1
		def abo = _play("Abomination")			// 4/4
		_play("Divine Spirit", abo)				// 4/4 + '+4 Health' buff => 4/8
		_next_turn()
		
		def fae = _play("Faerie Dragon")		// 3/2
		def abu = _play("Abusive Sergeant", fae)
		_play("Equality")
		assert fae.get_attack() == fae.attack + 2
		assert fae.get_health() == 1
		assert fae.get_max_health() == 1
		assert abo.get_health() == 5			// 4/4 + '+4 Health' buff + change health to 1
		assert abo.get_max_health() == 5			// 4/4 + '+4 Health' buff + change health to 1
		assert abu.get_health() == 1
		assert abu.get_max_health() == 1
		
		_next_turn()
		_play("Mortal Coil", abu)
		assert abu.is_dead()
	}
	
	@Test
	void EyeforanEye_play() {
		// Secret: When your hero takes damage, deal that much damage to the enemy hero
		_play("Eye for an Eye")
		_next_turn()
		def blu = _play("Bluegill Warrior")
		assert p2.hero.health == 30
		assert p1.hero.health == 30
		g.player_attacks(blu, p2.hero) // should trigger eye for an eye
		assert p2.hero.health == 28
		assert p1.hero.health == 28
	}
	
	@Test
	void GuardianOfKings_play() {
		// Battlecry: Restore 6 Health to your hero
		p1.hero.health = 25
		_play("GuardianOfKings")
		assert p1.hero.health == 30
	}
	
	@Test
	void HammerOfWrath_play() {
		// Deal 3 damage. Draw a card
		def bou = _play("Boulderfist Ogre")
		_next_turn()
		def before_hand_size = p1.hand.size()
		_play("Kobold Geomancer") // +1 Spell Damage
		_play("Hammer of Wrath", bou)
		assert p1.hand.size() == before_hand_size + 1
		assert bou.health == bou.max_health - 4
		_play("Hammer of Wrath", p2.hero)
		assert p2.hero.health == 26
		assert p1.hand.size() == before_hand_size + 2
	}
	
	@Test
	void HandOfProtection_play() {
		// Give a minion Divine Shield
		
		// invalid target
		def fae = _play("Faerie Dragon")
		_should_fail("no valid target") { _play("Hand of Protection", fae) }
		
		// valid target
		def blu = _play("Bluegill Warrior")
		assert !blu.has_divine_shield()
		_play("Hand of Protection", blu)
		assert blu.has_divine_shield()
    }
	
	@Test
	void HolyLight_play() {
		// Restore 6 Health
		p1.hero.health = 1
		_play("Holy Light", p1.hero)
		assert p1.hero.health == 7
		def abo = _play("Abomination")
		abo.set_health(1)
		assert abo.is_enraged
		_play("Holy Light", abo)
		assert abo.health == abo.max_health
		assert !abo.is_enraged
	}
	
	@Test
	void HolyWrath_play() {
		// Draw a card and deal damage equal to its cost
		def arm = g.new_card("Archmage")
		p1.deck.cards.add(0, arm) // to draw
		_play("Kobold Geomancer") // +1 Spell Damage
		_play("Holy Wrath", p2.hero)
		assert p1.hand.contains(arm)
		assert !p1.deck.cards.contains(arm)
		assert p2.hero.health == 30 - arm.cost -1
	}
	
	@Test
	void Humility_play() {
		// Change a minion's attack to 1
		def c = _play("Abomination")
		assert c.get_attack() == 4
		_play_and_target("Humility", c)
		assert c.get_attack() == 1
		assert c.has_buff("change attack to 1")
		g.end_turn()
		assert c.get_attack() == 1
	}
	
	@Test
	void LayOnHands_play() {
		// Restore 8 Health. Draw 3 cards
		p1.hero.health = 7
		def before_hand_size = p1.hand.size()
		_play("Lay on Hands", p1.hero)
		assert p1.hero.health == 7+8
		assert p1.hand.size() == before_hand_size + 3
	}
	
	@Test
	void LightsJustice_play() {
		// cost=1; attack=1; max_health=4
		_play("Light's Justice")
		assert p1.hero.weapon != null
		assert p1.hero.weapon.attack == 1
		assert p1.hero.weapon.durability == 4
		_attack(p1.hero, p2.hero)
		assert p1.hero.weapon.durability == 3
		assert p2.hero.health == 29
	}
	
	@Test
	void NobleSacrifice_play() {
		// Secret: When an enemy attacks, summon a 2/1 Defender as the new target.
		_play("Noble Sacrifice")
		_next_turn()
		def blu = _play("Bluegill Warrior")
		_attack(blu,p2.hero)
		assert blu.is_dead()
		assert p2.hero.health == 30
		assert p2.minions.size() == 0
		assert p2.secrets.size() == 0
	}
	
	@Test
	void Redemption_play() {
		// Secret: When one of your minions dies, return it to life with 1 Health

		// killed when attacking doesn't work : secret is not active		
		def bou = _play("Boulderfist Ogre")	// 6/7
		_next_turn()
		_play("Redemption")
		def rec = _play("Reckless Rocketeer") // 5/2
		_attack(rec, bou)
		assert rec.is_dead()
		assert p1.secrets.size() != 0
		
		// killed when defending : secret should activate
		def shb = _play("Shieldbearer")
		_next_turn()
		_attack(bou, shb)
		assert !shb.is_dead()
		assert shb.is_in_play()
		assert shb.get_health() == 1
		assert p1.secrets.size() == 0
	}
	
	@Test
	void Repentance_play() {
		// Secret: When your opponent plays a minion, reduce its health to 1
		
		_play("Repentance")
		_next_turn()
		def rec = _play("Reckless Rocketeer") // 5/2
		assert rec.health == 1
		assert rec.get_health() == 1
	}
	
	@Test
	void Repentance_Twilight_Drake() {
		// Secret: When your opponent plays a minion, reduce its health to 1
		
		_play("Repentance")
		_next_turn()
		
		def td = _play("Twilight Drake") // Battlecry: Gain +1 Health for each card in your hand.
		// Battlecries take priority over secret, so secret should be played last
		// But this battlecry is a buff (permanent effect) so it comes back!
		assert td.health == 1
		assert td.get_health() == 1 + p1.hand.size()
	}
	
	@Test
	void SwordOfJustice_play() {
		// Whenever you summon a minion, give it +1/+1 and this loses 1 Durability
		
		// re-create game to have Uther
		g.end_transaction()		
		_create_game_Uther_vs_Uther()
		before_tree = g.buildGameTree().clone()
		g.begin_transaction()
		
		_play("Sword of Justice")
		assert p1.hero.weapon != null
		def dur = p1.hero.weapon.get_durability()
		_use_hero_power() // summon a 1/1 Silver Hand Recruit
		assert p1.minions.size() == 1
		def shr = p1.minions[0]
		assert shr.get_attack() == shr.attack + 1
		assert shr.get_health() == shr.health + 1
		assert p1.hero.weapon.get_durability() == dur - 1	// 4
		
		_next_turn()
		_play("Abomination") // should have no effect
		assert p2.hero.weapon.get_durability() == dur - 1
		
		_next_turn()
		def shb = _play("Shieldbearer")
		assert shb.get_attack() == shb.attack + 1
		assert shb.get_health() == shb.health + 1
		assert p1.hero.weapon.get_durability() == dur - 2	// 3
		
		def lep = _play("Leper Gnome")
		assert lep.get_attack() == lep.attack + 1
		assert lep.get_health() == lep.health + 1
		assert p1.hero.weapon.get_durability() == dur - 3	// 2
		
		def sh2 = _play("Shieldbearer")
		assert sh2.get_attack() == sh2.attack + 1
		assert sh2.get_health() == sh2.health + 1
		assert p1.hero.weapon.get_durability() == dur - 4	// 1
		
		def le2 = _play("Leper Gnome")
		assert le2.get_attack() == le2.attack + 1
		assert le2.get_health() == le2.health + 1
		assert p1.hero.weapon == null						// 0 durability
	}
	
	@Test
	void TirionFordring_play() {
		// Divine Shield. Taunt. Deathrattle: Equip a 5/3 Ashbringer
		
		def tir = _play("Tirion Fordring")
		assert tir.has_buff(DIVINE_SHIELD)
		assert tir.has_buff(TAUNT)
		_next_turn()
		
		_play("Assassinate", tir)
		assert p2.hero.weapon != null
		assert p2.hero.weapon.name == "Ashbringer"
		assert p2.hero.weapon.attack == 5
		assert p2.hero.weapon.durability == 3
		_next_turn()
		
		_attack(p1.hero, p2.hero)
		assert p2.hero.health == 30 - 5
	}
	
	@Test
	void TruesilverChampion_play() {
		// Whenever your hero attacks, restore 2 Health to it
		
		p1.hero.health = 20
		_play("Truesilver Champion")
		_attack(p1.hero, p2.hero)
		assert p2.hero.health == 30 - 4
		assert p1.hero.health == 20 + 2
		
		_next_turn()
		def lig = _play("Lightwarden") // 1.2, Whenever a character is healed, gain +2 Attack
		
		_next_turn()
		_attack(p1.hero, lig) // lightwarden should get +2A before attack
		assert p1.hero.health == 22 + 2 - 1 -2
	}

}
