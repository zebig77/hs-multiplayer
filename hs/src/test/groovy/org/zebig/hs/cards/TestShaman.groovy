package org.zebig.hs.cards;

import static org.zebig.hs.mechanics.buffs.BuffType.*
import org.zebig.hs.game.Card
import org.junit.Test

import org.zebig.hs.utils.TestHelper

class TestShaman extends TestHelper {

	@Test
	public void AncestralHealing_play() {
	// Restore a minion to full Health
		def amb = _play("Amani Berserker")
		_play_and_target( "Cruel Taskmaster", amb )
		assert amb.get_health() == 2
		assert amb.get_attack() == 7
		assert amb.is_enraged
		_play_and_target( "AncestralHealing", amb )
		assert amb.get_health() == 3
		assert amb.get_attack() == 4
		assert amb.is_enraged == false
		assert amb.has_buff(TAUNT)
	}
	
	
	@Test
	public void AncestralSpirit_return_to_battlefield_when_destroyed() {
		// Give a minion "Deathrattle: Resummon this minion
		
		// very tricky, dont know if it is a bug or correct
		
		def abo = _play("Abomination") // Deathrattle: Deal 2 damage to ALL characters
		_play_and_target("Ancestral Spirit", abo) // Deathrattle: Resummon this minion
		assert abo.has_buff(RETURN_TO_BATTLEFIELD_WHEN_DESTROYED)
		_next_turn()
		def emp = _play("Emperor Cobra") // Destroy any minion damaged by this minion
		_next_turn()
		g.player_attacks(abo, emp)
		assert abo.is_dead() == false
		assert abo.is_in_play() == true
		assert abo.has_buff(RETURN_TO_BATTLEFIELD_WHEN_DESTROYED) == false
	}
	
	@Test
	public void Bloodlust_play() {
		// Give your minions +3 Attack this turn
		
		def shb = _play("Shieldbearer")
		def lep = _play("Leper Gnome")
		_play("Bloodlust")
		assert shb.get_attack() == shb.attack + 3
		assert lep.get_attack() == lep.attack + 3
		assert p1.hero.get_attack() == 0 // only minions
		
		_next_turn()
		assert shb.get_attack() == shb.attack
		assert lep.get_attack() == lep.attack
	}
	
	@Test
	public void Doomhammer_play() {
		// Windfury. Overload: (2)
		p1.max_mana = 5
		p1.available_mana = 5
		p1.overload = 0
		def doh = g.new_card("Doomhammer")
		p1.play(doh)
		assert p1.hero.weapon != null
		assert p1.hero.weapon.name == "Doomhammer"
		// test windfury
		g.player_attacks(p1.hero, p2.hero)
		assert p2.hero.health == 28
		g.player_attacks(p1.hero, p2.hero)
		assert p2.hero.health == 26
		_should_fail("it has already attacked twic") { g.player_attacks(p1.hero, p2.hero) }
		_next_turn()
		// test overload(2)
		_next_turn()
		assert p1.max_mana == 6
		assert p1.available_mana == 6 - 2
	}
	
	@Test
	public void DustDevil_play() {
		def dud = _play("Dust Devil")
		_should_fail("just summoned") {
			g.player_attacks(dud, p2.hero)
		}
		_next_turn()
		_next_turn()
		g.player_attacks(dud, p2.hero)
		g.player_attacks(dud, p2.hero)
		assert p2.hero.health == 24
		_should_fail("already attacked twice") { 
			g.player_attacks(dud, p2.hero) 
		}		
	}
	
	@Test
	public void EarthShock_play() {
		def kob = _play("Kobold Geomancer")
		assert kob.has_buff("Spell Damage +1")
		assert kob.health == 2
		_next_turn()
		_play("Earth Shock", kob)
		assert kob.has_buff("Spell Damage +1") == false
		assert kob.health == 1
	}
	
	@Test
	public void FarSight_play() {
		def elem = g.new_card("Unbound Elemental")
		assert elem.get_cost() == 3
		p1.deck.cards.add(0, elem)
		_play("Far Sight")
		assert p1.hand.cards.contains(elem)
		assert elem.get_cost() == 0
	}
	
	@Test
	public void FeralSpirit_play() {
		// Summon two 2/3 Spirit Wolves with Taunt. Overload: (2)
		_play("Feral Spirit")
		assert p1.overload == 2
		assert p1.minions.size() == 2
		assert p1.minions[0].name == "Spirit Wolf"
		assert p1.minions[0].attack == 2
		assert p1.minions[0].health == 3
		assert p1.minions[0].has_buff(TAUNT)
		assert p1.minions[1].name == "Spirit Wolf"
		assert p1.minions[1].attack == 2
		assert p1.minions[1].health == 3
		assert p1.minions[1].has_buff(TAUNT)
		_play("Dust Devil")
		assert p1.overload == 2+2
		p1.available_mana = 0
		p1.max_mana = 4
		_next_turn()
		_next_turn()
		assert p1.max_mana == 5
		assert p1.available_mana == 5-2-2
	}
	
	@Test
	public void FireElemental_play() {
		def abo = _play("Abomination")
		// Battlecry: Deal 3 damage
		_play("Fire Elemental", p2.hero)
		assert p2.hero.health == 27
	}
	
	@Test
	public void FlametongueTotem_play() {
		// Adjacent minions have +2 Attack.
		def lep = _play("Leper Gnome")			// x=0
		def flt = _play("Flametongue Totem")	// x=1
		def blu = _play("Bluegill Warrior")		// x=2
		assert lep.get_attack() == lep.attack +2
		assert blu.get_attack() == blu.attack +2
		// insert abomination between flt and blu
		def abo = _play("Abomination", 2) // should have the +2 Attack instead of blu
		assert blu.get_attack() == blu.attack	// x=3 -> no +2 Attack
		assert abo.get_attack() == abo.attack +2
		abo.dies() // deathrattle 'Deal 2 damage to ALL characters'
		assert lep.is_dead()
		assert blu.is_dead()
		assert flt.is_dead() == false // moved to x=0
		def kor = _play("Kor'kron Elite") // placed at x=1
		assert kor.get_attack() == kor.attack +2
		flt.dies()
		assert kor.get_attack() == kor.attack
	}
	
	@Test
	public void ForkedLightning_fail_not_enough_target() {
		// Deal 2 damage to 2 random enemy minions. Overload: (2)
		_should_fail("not enough targets") { _play("Forked Lightning") }
		_next_turn()
		_play("Blood Imp")
		_next_turn()
		_should_fail("not enough targets") { _play("Forked Lightning") }
	}
	
	@Test
	public void ForkedLightning_play() {
		// Deal 2 damage to 2 random enemy minions. Overload: (2)
		def bim = _play("Blood Imp")
		def bou = _play("Boulderfist Ogre")
		def abo = _play("Abomination")
		// Deal 2 damage to 2 random enemy minions. Overload: (2)
		_next_turn()
		def kob = _play("Kobold Geomancer") // check spell damage increase
		_play("Forked Lightning")
		assert bim.is_dead() || bou.health == bou.max_health - 3
		assert bim.is_dead() || abo.health == abo.max_health - 3
		assert p1.overload == 2
	}
	
	@Test
	public void FrostShock_play() {
		// Deal 1 damage to an enemy character and Freeze it
		def thf = _play("Thrallmar Farseer")
		_next_turn()
		_play("Frost Shock", thf)
		assert thf.is_frozen()
		assert thf.health == thf.max_health -1
		_next_turn()
		_play("Ironbeak Owl", thf)
		assert thf.is_frozen() == false
	}
	
	@Test
	public void HealingTotem_play() {
		// At the end of your turn, restore 1 Health to all friendly minions
		def abo = _play("Abomination")
		def kob = _play("Kobold Geomancer")
		p1.hero.health = 27
		abo.health = abo.max_health - 2
		kob.health = kob.max_health - 1
		def hto = _play("Healing Totem")
		assert abo.health == abo.max_health - 2 // no effect yet 
		assert kob.health == kob.max_health - 1 // no effect yet
		hto.health = hto.max_health - 1
		_next_turn()
		assert abo.health == abo.max_health - 1 
		assert kob.health == kob.max_health
		assert hto.health == hto.max_health
		assert p2.hero.health == 27 // only minions should be healed
		_play("Fireball", hto) // bye bye
		assert hto.is_dead()
		_next_turn()
		assert abo.health == abo.max_health - 1
		_next_turn()
		// should not be healed anymore
		assert abo.health == abo.max_health - 1
	}
	
	@Test
	public void Hex_play() {
		// Transform a minion into a 0/1 Frog with Taunt
		def gro = _play("Grommash Hellscream")
		def old_id = gro.id
		def old_controller = gro.controller
		_next_turn()
		_play("Hex", gro)
		assert gro.name == "Frog"
		assert gro.cost == 0
		assert gro.attack == 0
		assert gro.health == 1
		assert gro.max_health == 1
		assert gro.has_charge() == false
		assert gro.has_taunt() == true
		assert gro.is_a_beast()
		assert gro.card_definition.name == "Frog"
		assert gro.id == old_id
		assert gro.controller == old_controller
	}
	
	@Test
	public void LavaBurst_play() {
		// Deal 5 damage. Overload: (2)
		_play("Kobold Geomancer") // +1 Spell Damage
		_play("Lava Burst", p2.hero)
		assert p2.hero.health == 30 -5 -1
		assert p1.overload == 2
	}
	
	@Test
	public void LightningBolt_play() {
		// Deal 3 damage. Overload: (1)
		_play("Kobold Geomancer") // +1 Spell Damage
		_play("Lightning Bolt", p2.hero)
		assert p2.hero.health == 30 -3 -1
		assert p1.overload == 1
	}
	
	@Test
	public void LightningStorm_play() {
		// Deal 2-3 damage to all enemy minions. Overload: (2)'
		def bou1 = _play("Boulderfist Ogre")
		def bou2 = _play("Boulderfist Ogre")
		def abo1 = _play("Abomination")
		def abo2 = _play("Abomination")
		_next_turn()
		def shb = _play ("Shieldbearer") // should be unaffected
		_play("Lightning Storm")
		assert bou1.max_health - bou1.health in [2, 3]
		assert bou2.max_health - bou1.health in [2, 3]
		assert abo1.max_health - abo1.health in [2, 3]
		assert abo2.max_health - abo2.health in [2, 3]
		assert shb.max_health == shb.health
		assert p1.overload == 2
		assert p1.hero.health == 30
		assert p2.hero.health == 30
	}
	
	@Test
	public void ManaTideTotem_play() { //TODO test with mind control
		// At the end of your turn, draw a card.
		def mtt = _play("Mana Tide Totem")
		def before_hand_size = p1.hand.size()
		_next_turn()
		assert p2.hand.size() == before_hand_size + 1
		_play("Silence", mtt)
		_next_turn()
		before_hand_size = p1.hand.size()
		_next_turn()
		assert p2.hand.size() == before_hand_size // should not draw since silenced
	}
	
	@Test
	public void Reincarnate_leper_gnome() {
		def gnome = _play('Leper Gnome')
		_play("Reincarnate", gnome)
		assert p2.hero.health == 28
		assert p1.minions.size() == 1
		assert p1.minions[0].name == 'Leper Gnome'
		assert p1.minions[0] != gnome
	}
	
	@Test
	public void Reincarnate_nerubian_egg() {
		def egg = _play('Nerubian Egg')
		_play("Reincarnate", egg)
		assert p1.minions.size() == 2
		assert p1.minions[0].name == 'Nerubian'
		assert p1.minions[1].name == 'Nerubian Egg'
	}
	
	@Test
	public void Reincarnate_sylvanas() {
		/* Player A */
		def abo = _play("Abomination")
		_next_turn()
		
		/* Player B */
		def syl = _play("Sylvanas Windrunner")
		_play("Reincarnate", syl)
		assert p1.minions.size() == 2
		assert p1.minions[0].name == 'Abomination'
		assert p1.minions[1].name == 'Sylvanas Windrunner'
	}
	
	@Test
	public void RockbiterWeapon_play() {
		// Give a friendly character +3 Attack this turn
		
		// check on hero
		_play("Rockbiter Weapon", p1.hero)
		assert p1.hero.get_attack() == 3
		assert p1.hero.has_buff("+3 Attack")
		_attack(p1.hero, p2.hero)
		assert p2.hero.health == 27
		
		// check on minion
		def blu = _play("Bluegill Warrior")
		_play("Rockbiter Weapon", blu)
		assert blu.get_attack() == blu.attack + 3
		_attack(blu, p2.hero)
		assert p2.hero.health == 27 - blu.get_attack()
		
		// check end of turn removes effect
		_next_turn()
		assert p2.hero.get_attack() == 0
		assert blu.get_attack() == blu.attack
	}
	
	@Test
	public void TotemicMight_play() {
		List<Card> totems = []
		totems << _play("Wrath of Air Totem")
		totems << _play("Mana Tide Totem")
		totems << _play("Searing Totem")
		totems << _play("Healing Totem")
		totems << _play("Stoneclaw Totem")
		totems << _play("Flametongue Totem")
		
		totems.each {
			assert it.is_a_totem()
			assert it.get_health() == it.health
		}
		
		_play("Totemic Might")	
		totems.each {
			assert it.get_health() == it.health + 2
		}	
	}
	
	@Test
	public void UnboundElemental_play() {
		// Whenever you play a card with Overload, gain +1/+1
		
		def unb = _play("Unbound Elemental")
		def shb = _play("Shieldbearer") // no overload
		assert unb.get_attack() == unb.attack
		assert unb.has_buff('+1/+1') == false
		
		_play("Doomhammer")	// Windfury. Overload: (2)
		assert unb.get_attack() == unb.attack + 1
		assert unb.has_buff('+1/+1') == true
		
		_play("Unbound Elemental") // should have no effect
		assert unb.get_attack() == unb.attack + 1
		assert unb.has_buff('+1/+1') == true
	}
	
	@Test
	public void Windfury_play() {
		// Give a minion Windfury	
	
		_play("Faerie Dragon")
		_should_fail("no valid target") {
			_play("Windfury")
		}
		
		def blu = _play("Bluegill Warrior")
		_play("Windfury", blu)
		assert blu.has_buff(WINDFURY)
		
		_attack(blu, p2.hero)
		_attack(blu, p2.hero)
		assert p2.hero.health == 30 - 2*blu.get_attack()
		
		_should_fail("already attacked") {
			_attack(blu, p2.hero)
		}
	}
	
	@Test
	public void Windspeaker_play() {
		// Battlecry: Give a friendly minion Windfury
		
		// test 1: no minion
		def wi1 = _play("Windspeaker")
		assert wi1.has_buff(WINDFURY) == false
		
		// test 2: give windfury
		def wi2 = _play("Windspeaker", wi1)
		assert wi1.has_buff(WINDFURY)
	}
	
	@Test
	public void WrathOfAirTotem_play() {
		// Spell Damage +1
		
		_play("Wrath Of Air Totem")
		_play("Frost Shock", p2.hero)	// Deal 1 damage to an enemy character and Freeze it
		assert p2.hero.is_frozen()
		assert p2.hero.health == 28
	}
	
}
