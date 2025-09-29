package org.zebig.hs.cards

import org.zebig.hs.game.Card
import org.zebig.hs.game.CardDefinition
import org.zebig.hs.logger.Log
import org.zebig.hs.mechanics.buffs.BuffType

import static org.zebig.hs.mechanics.buffs.BuffType.CANNOT_BE_TARGETED_BY_SPELL_OR_POWER
import static org.zebig.hs.mechanics.buffs.BuffType.CHARGE

import org.zebig.hs.mechanics.events.ItIsDestroyed


class Abomination extends CardDefinition {
	Abomination() {
		name='Abomination'; type='minion'; cost=5; attack=4; max_health=4
		text='Taunt. Deathrattle: Deal 2 damage to ALL characters.'
		when_coming_in_play('Taunt') { 
			this_minion.gains(BuffType.TAUNT)
		}
		when_it_is_destroyed('Deathrattle: Deal 2 damage to ALL characters') { 
			this_minion.deal_damage(2, all_characters) 
		}
	}
}

class AbusiveSergeant extends CardDefinition {
	AbusiveSergeant() {
		name='Abusive Sergeant'; type='minion'; cost=1; attack=2; max_health=1
		text='Battlecry: Give a friendly minion +2 Attack this turn.'
		get_targets=[ { your_minions } ]
		when_played(text) {
			select_target(your_minions)?.gains("+2 Attack")?.until_end_of_turn()
		}
	}
}

class AcidicSwampOoze extends CardDefinition {
	AcidicSwampOoze() {
		name='Acidic Swamp Ooze'; type='minion'; cost=2; attack=3; max_health=2
		text="Battlecry: Destroy your opponent's weapon."
		when_played(text) {
			this_minion.destroy(opponent_hero.weapon)
		}
	}
}

class AcolyteOfPain extends CardDefinition {
	AcolyteOfPain() {
		name='Acolyte of Pain'; type='minion'; cost=3; attack=1; max_health=3
		text='Whenever this minion takes damage, draw a card.'
		when_it_takes_damage(text) {
			this_minion.controller.draw(1)
		}
	}
}

class AlarmOBot extends CardDefinition {
	AlarmOBot() {
		name='Alarm-O-Bot'; type='minion'; cost=3; attack=0; max_health=3
		text='At the start of your turn, swap this minion with a random one in your hand.'
		when_its_controller_turn_starts(text) {
			def m = random_card(you.hand.cards.findAll{(it as Card).type == 'minion'})
			if (m != null) {
				this_minion.return_to_hand()
				you.hand.remove(m)
				game.summon(you, m)
			}
		}
	}
}

class Alexstrasza extends CardDefinition {
	Alexstrasza() {
		name='Alexstrasza'; type='minion'; creature_type='dragon'; cost=9; attack=8; max_health=8
		text="Battlecry: Set a hero's remaining Health to 15."
		get_targets=[ { [your_hero, opponent_hero] } ]
		when_played(text) { 
			select_target([your_hero, opponent_hero]).set_health(15) 
		}
	}
}

class AmaniBerserker extends CardDefinition {
	AmaniBerserker() {
		name='Amani Berserker'; type='minion'; cost=2; attack=2; max_health=3
		text='Enrage: +3 Attack.'
		when_enraged(text) {  
			this_minion.gains('+3 Attack')
		}
		when_enraged_no_more('Remove +3 Attack buff') { 
			this_minion.remove_first_buff('+3 Attack') 
		}
	}
}

class AncientBrewmaster extends CardDefinition {
	AncientBrewmaster() {
		name='Ancient Brewmaster'; type='minion'; cost=4; attack=5; max_health=4
		text='Battlecry: Return a friendly minion from the battlefield to your hand.'
		get_targets=[ { your_minion_targets } ]
		when_played(text) {
			select_card(your_minion_targets)?.return_to_hand()
		}
	}
}

class AncientMage extends CardDefinition {
	AncientMage() {
		name='Ancient Mage'; type='minion'; cost=4; attack=2; max_health=5
		text='Battlecry: Give adjacent minions Spell Damage +1.'
		when_played(text) {
			this_minion.neighbors()*.have('Spell Damage +1')
		}
	}
}

class AncientWatcher extends CardDefinition {
	AncientWatcher() {
		name='Ancient Watcher'; type='minion'; cost=2; attack=4; max_health=5
		text="Can't Attack."
		when_coming_in_play(text) { this_minion.gains(BuffType.CANNOT_ATTACK) }
	}
}

class AngryChicken extends CardDefinition {
	AngryChicken() {
		name='Angry Chicken'; type='minion'; creature_type='beast'; cost=1; attack=1; max_health=1
		text='Enrage: +5 Attack.'
		when_enraged(text) { this_minion.gains('+5 Attack') }
		when_enraged_no_more('Remove +5 Attack buff') { 
			this_minion.remove_first_buff('+5 Attack') 
		}
	}
}

class ArcaneGolem extends CardDefinition {
	ArcaneGolem() {
		name='Arcane Golem'; type='minion'; cost=3; attack=4; max_health=2
		text='Charge. Battlecry: Give your opponent a Mana Crystal.'
		when_coming_in_play(text) { this_minion.gains(BuffType.CHARGE) }
		when_played(text) { opponent.add_max_mana(1) }
	}
}

class Archmage extends CardDefinition {
	Archmage() {
		name='Archmage'; type='minion'; cost=6; attack=4; max_health=7
		text='Spell Damage +1'
		when_coming_in_play(text) { 
			this_minion.gains('Spell Damage +1')
		}
	}
}

class ArgentCommander extends CardDefinition {
	ArgentCommander() {
		name='Argent Commander'; type='minion'; cost=6; attack=4; max_health=2
		text='Charge, Divine Shield'
		when_coming_in_play(text) {
			this_minion.gains(BuffType.CHARGE)
			this_minion.gains(BuffType.DIVINE_SHIELD)
		}
	}
}

class ArgentSquire extends CardDefinition {
	ArgentSquire() {
		name='Argent Squire'; type='minion'; cost=1; attack=1; max_health=1
		text='Divine Shield'
		when_coming_in_play(text) { 
			this_minion.gains( BuffType.DIVINE_SHIELD )
		}
	}
}

class AzureDrake extends CardDefinition {
	AzureDrake() {
		name='Azure Drake'; type='minion'; creature_type='dragon'; cost=5; attack=4; max_health=4
		text='Spell Damage +1. Battlecry: Draw a card.'
		when_coming_in_play("Spell damage +1") { 
			this_minion.gains('spell damage +1')
		}
		when_played(text) { 
			you.draw(1) 
		}
	}
}

class BaineBloodhoof extends CardDefinition {
	BaineBloodhoof() {
		name='Baine Bloodhoof'; type='minion'; cost=4; attack=4; max_health=5
	}
}

class Bananas extends CardDefinition {
	Bananas() {
		name='Bananas'; type='spell'; cost=1
		text='Give a minion +1/+1'
		collectible=false
		get_targets=[ { all_minion_targets } ]
		when_played(text) {
			select_spell_target(all_minion_targets).gains("+1/+1")
		}
	}
}

class BaronGeddon extends CardDefinition {
	BaronGeddon() {
		name='Baron Geddon'; type='minion'; cost=7; attack=7; max_health=5
		text='At the end of your turn, deal 2 damage to ALL other characters'
		when_coming_in_play(text) {
			def baron_geddon = this_minion
			this_minion.when_its_controller_turn_ends(text) {
				baron_geddon.deal_damage(2, all_characters - this_minion ) 
			}
		}
	}
}

class BaronRivendare extends CardDefinition {
	BaronRivendare() {
		name='Baron Rivendare'; type='minion'; cost=4; attack=1; max_health=7
		text='Your minions trigger their Deathrattles twice.'
		when_coming_in_play("adding $text") {
			def baron = this_minion
			baron.when_a_minion_dies("check $text") {
				new ItIsDestroyed(that_minion).check()
			}
		}
	}
}

class BigGameHunter extends CardDefinition {
	BigGameHunter() {
		name='Big Game Hunter'; type='minion'; cost=3; attack=4; max_health=2
		text='Battlecry: Destroy a minion with an Attack of 7 or more.'
		get_targets=[ { all_minions.findAll{it.get_attack() >= 7} } ]
		when_played(text) {
			this_minion.destroy( select_card(all_minions.findAll{it.get_attack() >= 7} ) )
		}
	}
}

class BloodfenRaptor extends CardDefinition {
	BloodfenRaptor() {
		name='Bloodfen Raptor'; type='minion'; creature_type='beast'; cost=2; attack=3; max_health=2
   }
}

class BloodKnight extends CardDefinition {
	BloodKnight() {
		name='Blood Knight'; type='minion'; cost=3; attack=3; max_health=3
		text='Battlecry: All minions lose Divine Shield. Gain +3/+3 for each Shield lost.'
		when_played(text) {
			all_minions.findAll{ it.has_buff(BuffType.DIVINE_SHIELD) }.each { Card minion ->
				minion.remove_all_buff(BuffType.DIVINE_SHIELD) // only 1 allowed anyway
				this_minion.gains('+3/+3')
			}
		}
	}
}

class BloodmageThalnos extends CardDefinition {
	BloodmageThalnos() {
		name='Bloodmage Thalnos'; type='minion'; cost=2; attack=1; max_health=1
		text='Spell Damage +1. Deathrattle: Draw a card.'
		when_coming_in_play(text) { 
			this_minion.gains('Spell Damage +1')
		}
		when_it_is_destroyed("Deathrattle: Draw a card") { 
			this_minion.controller.draw(1) 
		}
	}
}

class BloodsailCorsair extends CardDefinition {
	BloodsailCorsair() {
		name='Bloodsail Corsair'; type='minion'; creature_type='pirate'; cost=1; attack=1; max_health=2
		text="Battlecry: Remove 1 Durability from your opponent's weapon."
		when_played(text) {
			opponent.hero.weapon?.add_durability(-1)
		}
	}
}

class BloodsailRaider extends CardDefinition {
	BloodsailRaider() {
		name='Bloodsail Raider'; type='minion'; creature_type='pirate'; cost=2; attack=2; max_health=3
		text='Battlecry: Gain Attack equal to the Attack of your weapon.'
		when_played(text) {
			if (your_hero.weapon != null) {
				this_minion.gains("+${your_hero.weapon.get_attack()} Attack")
			}
		}
	}
}

class BluegillWarrior extends CardDefinition {
	BluegillWarrior() {
		name='Bluegill Warrior'; type='minion'; creature_type='murloc'; cost=2; attack=2; max_health=1
		text='Charge.'
		when_coming_in_play(text) { this_minion.gains(CHARGE) }
   }
}

class BootyBayBodyguard extends CardDefinition {
	BootyBayBodyguard() {
		name='Booty Bay Bodyguard'; type='minion'; cost=5; attack=5; max_health=4
		text='Taunt'
		when_coming_in_play(text) { this_minion.gains(BuffType.TAUNT) }
   }
}

class BoulderfistOgre extends CardDefinition {
	BoulderfistOgre() {
		name='Boulderfist Ogre'; type='minion'; cost=6; attack=6; max_health=7
    }
}

class CairneBloodhoof extends CardDefinition {
	CairneBloodhoof() {
		name='Cairne Bloodhoof'; type='minion'; cost=6; attack=4; max_health=5
		text='Deathrattle: Summon a 4/5 Baine Bloodhoof.'
		when_coming_in_play(text) {
			this_minion.when_it_is_destroyed(text) {
				game.summon( this_minion.controller, 'Baine Bloodhoof' )
			}
		}
    }
}

class CaptainGreenskin extends CardDefinition {
	CaptainGreenskin() {
		name='Captain Greenskin'; type='minion'; creature_type='pirate'; cost=5; attack=5; max_health=4
		text='Battlecry: Give your weapon +1/+1.'
		when_played(text) {
			if (your_hero.weapon != null) {
				your_hero.weapon.add_attack(1)
				your_hero.weapon.add_durability(1)
			}
		}
	}
}

class CaptainsParrot extends CardDefinition {
	CaptainsParrot() {
		name="Captain's Parrot"; type='minion'; creature_type='beast'; cost=2; attack=1; max_health=1
		text='Battlecry: Put a random Pirate from your deck into your hand.'
		when_played(text) {
			def pirates = you.deck.cards.findAll{(it as Card).creature_type == "pirate"}
			if (pirates.size() > 0) {
				Collections.shuffle(pirates)
				def pirate_card = pirates[0]
				you.deck.cards.remove(pirate_card)
				you.hand.add(pirate_card)
				Log.info "   - $this_minion added $pirate_card to ${you}'s hand"
			}
		}
   }
}

class Chicken extends CardDefinition {
	Chicken() {
		name='Chicken'; type='minion'; creature_type='beast'; cost=0; attack=1; max_health=1
	}
}

class ChillwindYeti extends CardDefinition {
	ChillwindYeti() {
		name='Chillwind Yeti'; type='minion'; cost=4; attack=4; max_health=5
	}
}

class ColdlightOracle extends CardDefinition {
	ColdlightOracle() {
		name='Coldlight Oracle'; type='minion'; creature_type='murloc'; cost=3; attack=2; max_health=2
		text='Battlecry: Each player draws 2 cards.'
		when_played(text) { 
			you.draw(2); 
			opponent.draw(2) 
		}
    }
}

class ColdlightSeer extends CardDefinition {
	ColdlightSeer() {
		name='Coldlight Seer'; type='minion'; creature_type='murloc'; cost=3; attack=2; max_health=3
		text='Battlecry: Give ALL other Murlocs +2 Health.'
		when_played(text) {
			def all_other_murlocs =
				(all_minions - this_minion).findAll{it.creature_type == 'murloc'}
			all_other_murlocs*.have('+2 Health')
		}
	}
}

class CoreHound extends CardDefinition {
	CoreHound() {
		name='Core Hound'; type='minion'; creature_type='beast'; cost=7; attack=9; max_health=5
   }
}

class CrazedAlchemist extends CardDefinition {
	CrazedAlchemist() {
		name='Crazed Alchemist'; type='minion'; cost=2; attack=2; max_health=2
		text='Battlecry: Swap the Attack and Health of a minion.'
		get_targets=[ { all_minions } ]
		when_played(text) {
			def m = select_target(all_minions)
			if (m != null) {
				def x = m.get_attack()
				m.set_attack( m.get_health() )
				m.set_max_health( x )
				m.set_health( x )
			}
		}
	}
}

class CultMaster extends CardDefinition {
	CultMaster() {
		name='Cult Master'; type='minion'; cost=4; attack=4; max_health=2
		text='Whenever one of your other minions dies, draw a card.'
		when_coming_in_play(text) {
			this_minion.when_a_minion_dies(text) {
				if (that_minion.controller == this_minion.controller && that_minion != this_minion) {
					you.draw(1)
				}
			}
		}
	}
}

class DalaranMage extends CardDefinition {
	DalaranMage() {
		name='Dalaran Mage'; type='minion'; cost=3; attack=1; max_health=4
		text='Spell Damage +1'
		when_coming_in_play(text) { 
			this_minion.gains('Spell Damage +1')
		}
    }
}

class DamagedGolem extends CardDefinition {
	DamagedGolem() {
		name='Damaged Golem'; type='minion'; cost=1; attack=2; max_health=1
	}
}

class DancingSwords extends CardDefinition {
	DancingSwords() {
		name='Dancing Swords'; type='minion'; cost=3; attack=4; max_health=4
		text='Deathrattle: Your opponent draws a card.'
		when_it_is_destroyed(text) {
			opponent_of(this_minion.controller).draw(1)
		}
	}
}

class DarkIronDwarf extends CardDefinition {
	DarkIronDwarf() {
		name='Dark Iron Dwarf'; type='minion'; cost=4; attack=4; max_health=4
		text='Battlecry: Give a minion +2 Attack this turn.'
		get_targets=[ { all_minion_targets } ]
		when_played(text) {
			select_target(all_minion_targets)?.gains('+2 Attack')?.until_end_of_turn()
		}
	}
}

class DarkscaleHealer extends CardDefinition {
	DarkscaleHealer() {
		name='Darkscale Healer'; type='minion'; cost=5; attack=4; max_health=5
		text='Battlecry: Restore 2 Health to all friendly characters.'
		when_played(text) { 
			this_minion.restore_health(2, your_hero + your_minions) 
		}
    }
}

class DeathsBite extends CardDefinition {
	DeathsBite() {
		name="Death's Bite"; type='weapon'; cost=4; attack=4; max_health=2
		text='Deathrattle: Deal 1 damage to all minions.'
		reserved_to="Warrior"
		when_played("add $text") {
			def deaths_bite = your_hero.weapon
			deaths_bite.when_it_is_destroyed(text) {
				deaths_bite.deal_damage(1, all_minions)
			}
		}
	}
}

class Deathlord extends CardDefinition {
	Deathlord() {
		name='Deathlord'; type='minion'; cost=3; attack=2; max_health=8
		text='Taunt. Deathrattle: Your opponent puts a minion from their deck into the battlefield.'
		when_it_is_destroyed("Deathrattle: Your opponent puts a minion from their deck into the battlefield.") {
			def _opponent = opponent_of(this_minion.controller)
			def c = random_card(_opponent.deck.cards.findAll{(it as Card).type == 'minion'})
			if (c != null && _opponent.minions.size() < 7) {
				_opponent.deck.cards.remove(c)
				game.summon(_opponent, c)
			}
		}
	}
}

class Deathwing extends CardDefinition {
	Deathwing() {
		name='Deathwing'; type='minion'; creature_type='dragon'; cost=10; attack=12; max_health=12
		text='Battlecry: Destroy all other minions and discard your hand.'
		when_played(text) {
			this_minion.destroy( all_minions - this_minion )
			you.hand.cards.clear()
		}
	}
}

class DefenderOfArgus extends CardDefinition {
	DefenderOfArgus() {
		name='Defender of Argus'; type='minion'; cost=4; attack=2; max_health=3
		text='Battlecry: Give adjacent minions +1/+1 and Taunt.'
		when_played(text) {
			this_minion.neighbors()*.have('+1/+1')
			this_minion.neighbors()*.have(BuffType.TAUNT)
		}
	}
}

class Demolisher extends CardDefinition {
	Demolisher() {
		name='Demolisher'; type='minion'; cost=3; attack=1; max_health=4
		text='At the start of your turn, deal 2 damage to a random enemy.'
		when_its_controller_turn_starts(text) {
			this_minion.deal_damage(2, random_pick( opponent_hero + enemy_minions ) )
		}
	}
}

class DireWolfAlpha extends CardDefinition {
	DireWolfAlpha() {
		name='Dire Wolf Alpha'; type='minion'; creature_type='beast'; cost=2; attack=2; max_health=2
		text='Adjacent minions have +1 Attack.'
		when_coming_in_play(text) {
			def dwa = this_minion
			dwa.when_attack_is_evaluated(text) {
				if (that_minion.controller == dwa.controller) {
					if (that_minion.place == dwa.place +1 || that_minion.place == dwa.place -1) {
						attack_increase += 1
					}
				}
			}
		}
	}
}

class Doomsayer extends CardDefinition { // Junit tested, check def
	Doomsayer() {
		name='Doomsayer'; type='minion'; cost=2; attack=0; max_health=7
		text='At the start of your turn, destroy ALL minions.'
		when_coming_in_play(text) { 
			this_minion.gains(BuffType.DESTROY_ALL_MINIONS_AT_START_OF_TURN)
		}
	}
}

class DragonlingMechanic extends CardDefinition {
	DragonlingMechanic() {
		name='Dragonling Mechanic'; type='minion'; cost=4; attack=2; max_health=4
		text='Battlecry: Summon a 2/1 Mechanical Dragonling.'
		when_played(text) { 
			game.summon(you, 'Mechanical Dragonling')
		}
	}
}

class DreadCorsair extends CardDefinition {
	DreadCorsair() {
		name='Dread Corsair'; type='minion'; creature_type='pirate'; cost=4; attack=3; max_health=3
		text='Taunt. Costs (1) less per Attack of your weapon.'
		when_a_cost_is_evaluated(text) {
			if (your_hero.weapon != null) {
				cost_increase -= your_hero.weapon.get_attack()
			}
		}
		when_coming_in_play(text) { 
			this_minion.gains(BuffType.TAUNT)
		}
   }
}

class Dream extends CardDefinition {
	Dream() {
		name='Dream'; type='spell'; cost=0
		text="Return a minion to its owner's hand."
		collectible=false
		get_targets=[ { all_minion_targets } ]
		when_played(text) {
			(select_spell_target(all_minion_targets) as Card).return_to_hand()
		}
	}
}

class EchoingOoze extends CardDefinition {
	EchoingOoze() {
		name='Echoing Ooze'; type='minion'; cost=2; attack=1; max_health=2
		text='Battlecry: Summon an exact copy of this minion at the end of the turn.'
		when_coming_in_play("add $text") {
			def _ooze = this_minion
			_ooze.when_its_controller_turn_ends("Summon an exact copy of this minion") {
			game.summon(_ooze.controller, _ooze.get_copy())
			}.run_once()
		}
	}
}

class EarthenRingFarseer extends CardDefinition {
	EarthenRingFarseer() {
		name='Earthen Ring Farseer'; type='minion'; cost=3; attack=3; max_health=3
		text='Battlecry: Restore 3 Health.'
		get_targets=[ { all_targets } ]
		when_played(text) {
			this_minion.restore_health(3, select_target(all_targets))
		}
	}
}

class EdwinVanCleef extends CardDefinition {
	EdwinVanCleef() {
		name='Edwin VanCleef'; type='minion'; cost=3; attack=2; max_health=2
		text='Combo: Gain +2/+2 for each card played earlier this turn.'
		reserved_to="Rogue"
		when_played(text) {
			if (you.nb_cards_played_this_turn > 0) {
				def x = 2*you.nb_cards_played_this_turn
				this_minion.gains("+${x}/+${x}")
			}
		}
	}
}

class ElvenArcher extends CardDefinition {
	ElvenArcher() {
		name='Elven Archer'; type='minion'; cost=1; attack=1; max_health=1
		text='Battlecry: Deal 1 damage.'
		get_targets=[ { all_characters } ]
		when_played(text) {
			this_minion.deal_damage(1, select_target(all_characters) )
		}
	}
}

class EmeraldDrake extends CardDefinition {
	EmeraldDrake() {
		name='Emerald Drake'; type='minion'; creature_type='dragon'; cost=4; attack=7; max_health=6
		collectible=false
	}
}

class EmperorCobra extends CardDefinition {
	EmperorCobra() {
		name='Emperor Cobra'; type='minion'; creature_type='beast'; cost=3; attack=2; max_health=3
		text='Destroy any minion damaged by this minion.'
		when_coming_in_play(text) {
			this_minion.when_it_deals_damage(text) {
				if (damaged_target.is_a_minion()) {
					this_minion.destroy(damaged_target)
				}
			}
		}
	}
}

class FacelessManipulator extends CardDefinition {
	FacelessManipulator() {
		name='Faceless Manipulator'; type='minion'; cost=5; attack=3; max_health=3
		text='Battlecry: Choose a minion and become a copy of it.'
		when_played("check $text") {
			Card.copy(select_target(all_minion_targets), this_minion)
		}
	}
}

class FaerieDragon extends CardDefinition {
	FaerieDragon() {
		name='Faerie Dragon'; type='minion'; creature_type='dragon'; cost=2; attack=3; max_health=2
		text="Can't be targeted by Spells or Hero Powers."
		when_coming_in_play(text) { 
			this_minion.gains(CANNOT_BE_TARGETED_BY_SPELL_OR_POWER)
		}
	}
}


class FlameOfAzzinoth extends CardDefinition {
	FlameOfAzzinoth() {
		name='Flame of Azzinoth'; type='minion'; cost=1; attack=2; max_health=1
	}
}

class FlesheatingGhoul extends CardDefinition {
	FlesheatingGhoul() {
		name='Flesheating Ghoul'; type='minion'; cost=3; attack=2; max_health=3
		text='Whenever a minion dies, gain +1 Attack.'
		when_coming_in_play(text) {
			def flg = this_minion
			flg.when_a_minion_dies(text) {
				flg.gains("+1 Attack")
			}
		}
	}
}

class FrostElemental extends CardDefinition {
	FrostElemental() {
		name='Frost Elemental'; type='minion'; cost=6; attack=5; max_health=5
		text='Battlecry: Freeze a character.'
		get_targets=[ { all_characters } ]
		when_coming_in_play(text) { 
			this_minion.freeze( select_target(all_characters)) 
		}
	}
}

class FrostwolfGrunt extends CardDefinition {
	FrostwolfGrunt() {
		name='Frostwolf Grunt'; type='minion'; cost=2; attack=2; max_health=2
		text='Taunt'
		when_coming_in_play(text) {
			this_minion.gains(BuffType.TAUNT)
		}
	}
}

class FrostwolfWarlord extends CardDefinition {
	FrostwolfWarlord() {
		name='Frostwolf Warlord'; type='minion'; cost=5; attack=4; max_health=4
		text='Battlecry: Gain +1/+1 for each other friendly minion on the battlefield.'
		when_played(text) {
			def x = your_minions.size()
			this_minion.gains("+$x/+$x")
		}
	}
}

class GadgetzanAuctioneer extends CardDefinition {
	GadgetzanAuctioneer() {
		name='Gadgetzan Auctioneer'; type='minion'; cost=5; attack=4; max_health=4
		text='Whenever you cast a spell, draw a card.'
		when_played(text) {
			def gad = this_minion
			gad.when_a_spell_is_played(text) {
				if (that_spell.controller == gad.controller) {
					gad.controller.draw(1)
				}
			}
		}
	}
}

class GnomishInventor extends CardDefinition {
	GnomishInventor() {
		name='Gnomish Inventor'; type='minion'; cost=4; attack=2; max_health=4
		text='Battlecry: Draw a card.'
		when_played(text) {
			you.draw(1)
		}
   }
}

class GoldshireFootman extends CardDefinition {
	GoldshireFootman() {
		name='Goldshire Footman'; type='minion'; cost=1; attack=1; max_health=2
		text='Taunt'
		when_coming_in_play(text) {
			this_minion.gains(BuffType.TAUNT)
		}
	}
}

class GrimscaleOracle extends CardDefinition {
	GrimscaleOracle() {
		name='Grimscale Oracle'; type='minion'; creature_type='murloc'; cost=1; attack=1; max_health=1
		text='ALL other Murlocs have +1 Attack.'
		when_coming_in_play(text) {
			def grimscale_oracle = this_minion
			grimscale_oracle.when_attack_is_evaluated(text) {
				if (that_target.is_a_minion() &&
				that_minion.is_a_murloc() &&
				that_minion != grimscale_oracle)
				{
					attack_increase += 1
				}
			}
		}
	}
}

class Gruul extends CardDefinition {
	Gruul() {
		name='Gruul'; type='minion'; cost=8; attack=7; max_health=7
		text='At the end of each turn, gain +1/+1.'
		when_coming_in_play("add $text") {
			def gruul = this_minion
			gruul.when_a_turn_ends(text) {
				gruul.gains('+1/+1')
			}
		}
	}
}

class GurubashiBerserker extends CardDefinition {
	GurubashiBerserker() {
		name='Gurubashi Berserker'; type='minion'; cost=5; attack=2; max_health=7
		text='Whenever this minion takes damage, gain +3 Attack.'
		when_it_takes_damage(text) { 
			this_minion.gains('+3 Attack')
		}
	}
}
class GelbinMekkatorque extends CardDefinition {
	GelbinMekkatorque() {
		name='Gelbin Mekkatorque'; type='minion'; cost=6; attack=6; max_health=6
		text='Battlecry: Summon an AWESOME invention.'
		when_played(text) {
			game.summon(you, random_pick([
				'Repair Bot',
				'Poultryizer',
				'Homing Chicken',
				'Emboldener 3000'
			]))
		}
	}
}

class RepairBot extends CardDefinition {
	RepairBot() {
		name='Repair Bot'; type='minion'; cost=1; attack=0; max_health=3
		text='At the end of your turn, restore 6 Health to a damaged character.'
		when_its_controller_turn_ends(text) {
			this_minion.restore_health(6, random_pick(all_characters.findAll{ it.health < it.max_health }))
		}
	}
}

class Poultryizer extends CardDefinition {
	Poultryizer() {
		name='Poultryizer'; type='minion'; cost=1; attack=0; max_health=3
		text='At the start of your turn, transform a random minion into a 1/1 Chicken.'
		when_its_controller_turn_starts(text) {
			game.transform( random_card(all_minions), 'Chicken' )
		}
	}
}

class HomingChicken extends CardDefinition {
	HomingChicken() {
		name='Homing Chicken'; type='minion'; cost=1; attack=0; max_health=1
		text='At the start of your turn, destroy this minion and draw 3 cards.'
		when_its_controller_turn_starts(text) {
			this_minion.dies()
			you.draw(3)
		}
	}
}

class Emboldener3000 extends CardDefinition {
	Emboldener3000() {
		name='Emboldener 3000'; type='minion'; cost=1; attack=0; max_health=4
		text='At the end of your turn, give a random minion +1/+1.'
		when_its_controller_turn_ends(text) {
			random_card(all_minions).gains('+1/+1')
		}
	}
}

class Gnoll extends CardDefinition {
	Gnoll() {
		name='Gnoll'; type='minion'; cost=2; attack=2; max_health=2
		text='Taunt'
		collectible=false
		when_coming_in_play(text) { this_minion.gains(BuffType.TAUNT) }
	}
}

class HarrisonJones extends CardDefinition {
	HarrisonJones() {
		name='Harrison Jones'; type='minion'; cost=5; attack=5; max_health=4
		text="Battlecry: Destroy your opponent's weapon and draw cards equal to its Durability."
		when_played(text) {
			if (opponent.hero.weapon != null) {
				def _d = opponent.hero.weapon.get_durability()
				opponent.hero.weapon.demolish()
				you.draw(_d)
			}
		}
	}
}

class Hogger extends CardDefinition {
	Hogger() {
		name='Hogger'; type='minion'; cost=6; attack=4; max_health=4
		text='At the end of your turn, summon a 2/2 Gnoll with Taunt.'
		when_coming_in_play(text) {
			def hogger = this_minion
			hogger.when_its_controller_turn_ends(text) {
				game.summon(hogger.controller, "Gnoll")
			}
		}
	}
}

class HarvestGolem extends CardDefinition {
	HarvestGolem() {
		name='Harvest Golem'; type='minion'; cost=3; attack=2; max_health=3
		text='Deathrattle: Summon a 2/1 Damaged Golem.'
		when_it_is_destroyed(text) {
			game.summon(this_minion.controller, "Damaged Golem")
		}
	}
}

class HauntedCreeper extends CardDefinition {
	HauntedCreeper() {
		name='Haunted Creeper'; type='minion'; creature_type='beast'; cost=2; attack=1; max_health=2
		text='Deathrattle: Summon two 1/1 Spectral Spiders.'
		when_it_is_destroyed(text) {
			2.times {
				game.summon(this_minion.controller, "Spectral Spider")
			}
		}
	}
}

class HungryCrab extends CardDefinition {
	HungryCrab() {
		name='Hungry Crab'; type='minion'; creature_type='beast'; cost=1; attack=1; max_health=2
		text='Battlecry: Destroy a Murloc and gain +2/+2.'
		when_played(text) {
			Card _victim = select_card(all_minions.findAll { it.creature_type == "murloc" })
			if (_victim != null) {
				_victim.dies()
				this_minion.gains('+2/+2')
			}
		}
	}
}

class IllidanStormrage extends CardDefinition {
	IllidanStormrage() {
		name='Illidan Stormrage'; type='minion'; creature_type="demon"
		cost=6; attack=7; max_health=5
		text='Whenever you play a card, summon a 2/1 Flame of Azzinoth.'
		when_its_controller_plays_a_card(text) {
			game.summon(you, "Flame of Azzinoth")
		}
	}
}

class Imp extends CardDefinition {
	Imp() {
		name='Imp'; type='minion'; creature_type='demon'; cost=1; attack=1; max_health=1
	}
}

class ImpMaster extends CardDefinition {
	ImpMaster() {
		name='Imp Master'; type='minion'; cost=3; attack=1; max_health=5
		text='At the end of your turn, deal 1 damage to this minion and summon a 1/1 Imp.'
		when_its_controller_turn_ends(text) {
			game.summon(this_minion.controller, "Imp")
			this_minion.deal_damage(1, this_minion)
		}
	}
}

class InjuredBlademaster extends CardDefinition {
	InjuredBlademaster() {
		name='Injured Blademaster'; type='minion'; cost=3; attack=4; max_health=7
		text='Battlecry: Deal 4 damage to HIMSELF.'
		when_coming_in_play(text) { this_minion.deal_damage(4, this_minion) }
	}
}

class IronbeakOwl extends CardDefinition {
	IronbeakOwl() {
		name='Ironbeak Owl'; type='minion'; creature_type='beast'; cost=2; attack=2; max_health=1
		text='Battlecry: Silence a minion.'
		get_targets=[ { all_minions } ]
		when_played(text) {
			def possible_targets = all_minions - this_minion
			if (possible_targets.size() > 0) {
				this_minion.silence( select_target(possible_targets) )
			}
		}
	}
}

class IronforgeRifleman extends CardDefinition {
	IronforgeRifleman() {
		name='Ironforge Rifleman'; type='minion'; cost=3; attack=2; max_health=2
		text='Battlecry: Deal 1 damage.'
		get_targets=[ { all_targets } ]
		when_played(text) {
			this_minion.deal_damage(1, select_target(all_targets))
		}
	}
}

class IronfurGrizzly extends CardDefinition {
	IronfurGrizzly() {
		name='Ironfur Grizzly'; type='minion'; creature_type='beast'; cost=3; attack=3; max_health=3
		text='Taunt'
		when_coming_in_play(text) {
			this_minion.gains(BuffType.TAUNT)
		}
	}
}

class JunglePanther extends CardDefinition {
	JunglePanther() {
		name='Jungle Panther'; type='minion'; creature_type='beast'; cost=3; attack=4; max_health=2
		text='Stealth'
		when_coming_in_play(text) {
			this_minion.gains(BuffType.STEALTH)
		}
	}
}

class KelThuzad extends CardDefinition {
	KelThuzad() {
		name="Kel'Thuzad"; type='minion'; cost=8; attack=6; max_health=8
		text='At the end of each turn, summon all friendly minions that died this turn.'
		when_coming_in_play("add $text") {
			def _kel = this_minion
			def _dead_tracker = []
			_kel.when_a_minion_dies("record dead minion's name") {
				if (that_minion.controller == _kel.controller) {
					_dead_tracker << that_minion.name
				}
			}
			_kel.when_its_controller_turn_ends(text) {
				_dead_tracker.each{ String card_name ->
					game.summon(_kel.controller, card_name)
				}
				_dead_tracker = []
			}
		}
	}
}

class KingMukla extends CardDefinition {
	KingMukla() {
		name='King Mukla'; type='minion'; creature_type='beast'; cost=3; attack=5; max_health=5
		text='Battlecry: Give your opponent 2 Bananas.'
		when_played(text) {
			2.times { opponent.hand.add(game.new_card("Bananas")) }
		}
	}
}

class KnifeJuggler extends CardDefinition {
	KnifeJuggler() {
		name='Knife Juggler'; type='minion'; cost=2; attack=3; max_health=2
		text='After you summon a minion, deal 1 damage to a random enemy.'
		when_coming_in_play(text) {
			def knife_juggler = this_minion
			knife_juggler.when_its_controller_plays_a_card(text) {
				if (that_card.is_a_minion()) {
					def all_enemies = opponent.minions + opponent.hero
					knife_juggler.deal_damage(1, random_pick(all_enemies))
				}
			}
		}
	}
}

class KoboldGeomancer extends CardDefinition { // tested with consecration
	KoboldGeomancer() {
		name='Kobold Geomancer'; type='minion'; cost=2; attack=2; max_health=2
		text='Spell Damage +1'
		when_coming_in_play(text) { 
			this_minion.gains('Spell Damage +1')
		}
    }
}

class LaughingSister extends CardDefinition {
	LaughingSister() {
		name='Laughing Sister'; type='minion'; cost=3; attack=3; max_health=5
		text="Can't be targeted by Spells or Hero Powers."
		when_coming_in_play(text) {
			this_minion.gains(CANNOT_BE_TARGETED_BY_SPELL_OR_POWER)
		}
	}
}

class LeeroyJenkins extends CardDefinition {
	LeeroyJenkins() {
		name='Leeroy Jenkins'; type='minion'; cost=5; attack=6; max_health=2
		text='Charge. Battlecry: Summon two 1/1 Whelps for your opponent.'
		when_coming_in_play('Charge') {
			this_minion.gains(CHARGE)
		}
		when_played(text) {
			2.times { 
				game.summon(opponent, "Whelp") 
			}
		} 
	}
}

class LeperGnome extends CardDefinition {
	LeperGnome() {
		name='Leper Gnome'; type='minion'; cost=1; attack=2; max_health=1
		text='Deathrattle: Deal 2 damage to the enemy hero.'
		when_it_is_destroyed(text) {
			this_minion.deal_damage(2, opponent_of(this_minion.controller).hero)
		}
	}
}

class Lightwarden extends CardDefinition {
	Lightwarden() {
		name='Lightwarden'; type='minion'; cost=1; attack=1; max_health=2
		text='Whenever a character is healed, gain +2 Attack.'
		when_coming_in_play(text) {
			def lightwarden = this_minion
			lightwarden.when_a_character_is_healed(text) {
				lightwarden.gains('+2 Attack')
			}
		}
	}
}

class Loatheb extends CardDefinition {
	Loatheb() {
		name='Loatheb'; type='minion'; cost=5; attack=5; max_health=5
		text='Battlecry: Enemy spells cost (5) more next turn.'
		when_played(text) {
			def _the_enemy = opponent
			_the_enemy.when_a_cost_is_evaluated(text) {
				if (that_card.controller == _the_enemy && that_card.is_a_spell()) {
					cost_increase += 5
				}
			}.until_end_of_turn()
		}
	}
}

class LootHoarder extends CardDefinition {
	LootHoarder() {
		name='Loot Hoarder'; type='minion'; cost=2; attack=2; max_health=1
		text='Deathrattle: Draw a card.'
		when_it_is_destroyed(text) {
			this_minion.controller.draw(1)
		}
	}
}

class LordOfTheArena extends CardDefinition {
	LordOfTheArena() {
		name='Lord of the Arena'; type='minion'; cost=6; attack=6; max_health=5
		text='Taunt'
		when_coming_in_play(text) { this_minion.gains(BuffType.TAUNT) }
	}
}

class LorewalkerCho extends CardDefinition {
	LorewalkerCho() {
		name="Lorewalker Cho"; type='minion'; cost=2; attack=0; max_health=4
		text="Whenever a player casts a spell, put a copy into the other player's hand."
		when_coming_in_play(text) {
			this_minion.when_a_spell_is_played(text) {
				// Note: triggers event if spell is countered
				Card c = game.new_card(that_spell.name)
				game.opponent_of(that_spell.controller).hand.add(c)
			}
		}
	}
}

class MadBomber extends CardDefinition {
	MadBomber() {
		name='Mad Bomber'; type='minion'; cost=2; attack=3; max_health=2
		text='Battlecry: Deal 3 damage randomly split between all other characters.'
		when_played(text) {
			for (i in 1..you.get_spell_damage(3)) {
				def all_other_characters = all_characters - this_minion
				def t = random_pick(all_other_characters)
				this_spell.deal_damage(1, t)
			}
		}
	}
}

class MadScientist extends CardDefinition {
	MadScientist() {
		name='Mad Scientist'; type='minion'; cost=2; attack=2; max_health=2
		text='Deathrattle: Put a Secret from your deck into the battlefield.'
		when_it_is_destroyed(text) {
			def s = random_card( this_minion.controller.deck.cards.findAll { Card c -> c.is_a_secret } )
			if (s != null) {
				this_minion.controller.create_secret(s)
				this_minion.controller.deck.cards.remove(s)
			}
		}
	}
}

class Maexxna extends CardDefinition {
	Maexxna() {
		name='Maexxna'; type='minion'; creature_type='beast'; cost=6; attack=2; max_health=8
		text='Destroy any minion damaged by this minion.'
		when_coming_in_play(text) {
			this_minion.when_it_deals_damage(text) {
				if (damaged_target.is_a_minion()) {
					this_minion.destroy(damaged_target)
				}
			}
		}
	}
}

class MagmaRager extends CardDefinition {
	MagmaRager() {
		name='Magma Rager'; type='minion'; cost=3; attack=5; max_health=1
	}
}

class ManaAddict extends CardDefinition {
	ManaAddict() {
		name='Mana Addict'; type='minion'; cost=2; attack=1; max_health=3
		text='Whenever you cast a spell, gain +2 Attack this turn.'
		when_coming_in_play(text) {
			def mana_addict = this_minion
			this_minion.when_its_controller_plays_a_card(text) {
				if (that_card.is_a_spell()) {
					mana_addict.gains("+2 Attack").until_end_of_turn()
				}
			}
		}
	}
}

class ManaWraith extends CardDefinition {
	ManaWraith() {
		name='Mana Wraith'; type='minion'; cost=2; attack=2; max_health=2
		text='ALL minions cost (1) more.'
		when_coming_in_play(text) {
			this_minion.when_a_cost_is_evaluated(text) {
				if (that_card.is_a_minion()) {
					cost_increase += 1
				}
			}
		}
	}
}

class MasterSwordsmith extends CardDefinition {
	MasterSwordsmith() {
		name='Master Swordsmith'; type='minion'; cost=2; attack=1; max_health=3
		text='At the end of your turn, give another random friendly Minion +1 Attack.'
		when_coming_in_play(text) {
			def master_swordsmith = this_minion
			this_minion.when_its_controller_turn_ends(text) {
				if (your_minions.size() > 0) {
					random_card(your_minions - master_swordsmith)?.gains('+1 Attack')
				}
			}
		}
	}
}

class MechanicalDragonling extends CardDefinition {
	MechanicalDragonling() {
		name='Mechanical Dragonling'; type='minion'; cost=1; attack=2; max_health=1
		collectible=false
	}
}

class MillhouseManastorm extends CardDefinition {
	MillhouseManastorm() {
		name='Millhouse Manastorm'; type='minion'; cost=2; attack=4; max_health=4
		text='Battlecry: Enemy spells cost (0) next turn.'
		when_played(text) {
			def _the_enemy = opponent
			_the_enemy.when_a_cost_is_evaluated(text) {
				if (that_card.controller == _the_enemy && that_card.is_a_spell()) {
					cost_change = 0
				}
			}.until_end_of_turn() // effect removed at the end of opponent's turn
		}
	}
}

class MindControlTech extends CardDefinition {
	MindControlTech() {
		name='Mind Control Tech'; type='minion'; cost=3; attack=3; max_health=3
		text='Battlecry: If your opponent has 4 or more minions, take control of one at random.'
		when_played(text) {
			if (opponent.minions.size() >= 4) {
				def m = game.random_pick(opponent.minions)
				you.take_control(m)
			}
		}
	}
}

class MogushanWarden extends CardDefinition {
	MogushanWarden() {
		name="Mogu'shan Warden"; type='minion'; cost=4; attack=1; max_health=7
		text='Taunt'
		when_coming_in_play(text) { 
			this_minion.gains(BuffType.TAUNT)
		}
	}
}

class MoltenGiant extends CardDefinition {
	MoltenGiant() {
		name='Molten Giant'; type='minion'; cost=20; attack=8; max_health=8
		text='Costs (1) less for each damage your hero has taken.'
		when_its_cost_is_evaluated(text) {
			cost_increase = this_minion.controller.hero.health - 30
		}
	}
}

class MountainGiant extends CardDefinition {
	MountainGiant() {
		name='Mountain Giant'; type='minion'; cost=12; attack=8; max_health=8
		text='Costs (1) less for each other card in your hand.'
		when_its_cost_is_evaluated(text) {
			cost_increase = -((this_minion.controller.hand.cards - this_minion).size())
		}
	}
}

class MurlocRaider extends CardDefinition {
	MurlocRaider() {
		name='Murloc Raider'; type='minion'; creature_type='murloc'; cost=1; attack=2; max_health=1
   }
}

class MurlocTidecaller extends CardDefinition {
	MurlocTidecaller() {
		name='Murloc Tidecaller'; type='minion'; creature_type='murloc'; cost=1; attack=1; max_health=2
		text='Whenever a Murloc is summoned, gain +1 Attack.'
		when_coming_in_play(text) {
			def tidecaller = this_minion
			this_minion.when_a_minion_is_summoned(text) {
				if (that_minion.is_a_murloc()) {
					tidecaller.gains('+1 Attack')
				}
			}
		}
	}
}

class MurlocScout extends CardDefinition {
	MurlocScout() {
		name='Murloc Scout'; type='minion'; creature_type='murloc'; cost=0; attack=1; max_health=1
	}
}

class MurlocTidehunter extends CardDefinition {
	MurlocTidehunter() {
		name='Murloc Tidehunter'; type='minion'; creature_type='murloc'; cost=2; attack=2; max_health=1
		text='Battlecry: Summon a 1/1 Murloc Scout.'
		when_played(text) {
			if (your_minions.size() < 7) {
				game.summon(you, "Murloc Scout")
			}
		}
	}
}

class MurlocWarleader extends CardDefinition {
	MurlocWarleader() {
		name='Murloc Warleader'; type='minion'; creature_type='murloc'; cost=3; attack=3; max_health=3
		text='All other Murlocs have +2/+1'
		when_coming_in_play(text) {
			def warleader = this_minion
			warleader.when_attack_is_evaluated(text) {
				if (that_target.is_a_minion() &&
				that_minion.is_a_murloc() &&
				that_minion != warleader)
				{
					attack_increase += 2
					Log.info "   - $warleader gives attack_increase=+$attack_increase"
				}
			}
			warleader.when_health_is_evaluated(text) {
				if (that_target.is_a_minion() &&
				that_minion.is_a_murloc() &&
				that_minion != warleader)
				{
					health_increase += 1
                    Log.info "   - $warleader gives health_increase=+$health_increase"
				}
			}
		}
	}
}

class NatPagle extends CardDefinition {
	NatPagle() {
		name='Nat Pagle'; type='minion'; cost=2; attack=0; max_health=4
		text='At the start of your turn, you have a 50% chance to draw an extra card.'
		when_coming_in_play(text) {
			def nat = this_minion
			this_minion.when_its_controller_turn_starts('you have a 50% chance to draw an extra card') {
				if (game.get_random_int(2) == 1) { // 0 or 1
					nat.controller.draw(1)
				}
			}
		}
	}
}

class NerubArWeblord extends CardDefinition {
	NerubArWeblord() {
		name="Nerub'ar Weblord"; type='minion'; cost=2; attack=1; max_health=4
		text='Minions with Battlecry cost (2) more.'
		when_coming_in_play(text) {
			this_minion.when_a_cost_is_evaluated(text) {
				if (that_card.is_a_minion() && that_card.has_battlecry()) {
					cost_increase += 2
				}
			}
		}
	}
}

class Nerubian extends CardDefinition {
	Nerubian() {
		name="Nerubian"; type='minion'; cost=3; attack=4; max_health=4
		collectible=false
	}
}

class NerubianEgg extends CardDefinition {
	NerubianEgg() {
		name='Nerubian Egg'; type='minion'; cost=2; attack=0; max_health=2
		text='Deathrattle: Summon a 4/4 Nerubian.'
		when_it_is_destroyed(text) {
			game.summon(this_minion.controller, "Nerubian")
		}
	}
}

class Nightblade extends CardDefinition {
	Nightblade() {
		name='Nightblade'; type='minion'; cost=5; attack=4; max_health=4
		text='Battlecry: Deal 3 damage to the enemy hero.'
		when_played(text) {
			this_minion.deal_damage(3, opponent_hero)
		}
	}
}

class Nightmare extends CardDefinition {
	Nightmare() {
		name='Nightmare'; type='spell'; cost=0
		text="Give a minion +5/+5. At the start of your next turn, destroy it."
		collectible=false
		get_targets=[ { all_minion_targets } ]
		when_played(text) {
			def _m = select_spell_target(all_minion_targets)
			_m.gains('+5/+5')
			_m.when_its_controller_turn_starts("destroy it") {
				_m.dies()
			}
		}
	}
}

class NoviceEngineer extends CardDefinition {
	NoviceEngineer() {
		name='Novice Engineer'; type='minion'; cost=2; attack=1; max_health=1
		text='Battlecry: Draw a card.'
		when_played(text) {
			you.draw(1) // card will be destroyed if there are already 10 cards in hand
		}
	}
}

class Nozdormu extends CardDefinition {
	Nozdormu() {
		name='Nozdormu'; type='minion'; creature_type='dragon'; cost=9; attack=8; max_health=8
		text='Players only have 15 seconds to take their turns.'
		when_coming_in_play("add $text") {
			game.turn_timeout = 15
			this_minion.when_a_turn_starts(text) { // reset by game to normal when each turn starts
				game.turn_timeout = 15
			}
		}
	}
}

class OasisSnapjaw extends CardDefinition {
	OasisSnapjaw() {
		name='Oasis Snapjaw'; type='minion'; creature_type='beast'; cost=4; attack=2; max_health=7
	}
}

class OgreMagi extends CardDefinition {
	OgreMagi() {
		name='Ogre Magi'; type='minion'; cost=4; attack=4; max_health=4
		text='Spell Damage +1'
		when_coming_in_play(text) {
			this_minion.gains('Spell Damage +1')
		}
	}
}

class OldMurkEye extends CardDefinition {
	OldMurkEye() {
		name='Old Murk-Eye'; type='minion'; creature_type='murloc'; cost=4; attack=2; max_health=4
		text='Charge. Has +1 Attack for each other Murloc on the battlefield.'
		when_coming_in_play(text) {
			this_minion.gains(BuffType.CHARGE)
			def ome = this_minion
			ome.when_attack_is_evaluated('Has +1 Attack for each other Murloc on the battlefield.') {
				if (that_minion == ome) {
					attack_increase += all_minions.findAll{it.creature_type == "murloc" && it != ome}.size()
				}
			}
		}
	}
}

class Onyxia extends CardDefinition {
	Onyxia() {
		name='Onyxia'; type='minion'; creature_type='dragon'; cost=9; attack=8; max_health=8
		text='Battlecry: Summon 1/1 Whelps until your side of the battlefield is full.'
		when_played(text) {
			// note: I used 'you.minions' instead of 'your_minions' because the latest excludes
			// the card being played (Onyxia here).
			(7 - you.minions.size()).times {
				game.summon(you, "Whelp")
			}
		}
	}
}

class PintSizedSummoner extends CardDefinition {
	PintSizedSummoner() {
		name='Pint-sized Summoner'; type='minion'; cost=2; attack=2; max_health=2
		text='The first minion you play each turn costs (1) less.'
		when_coming_in_play(text) {
			int use_counter = 1 // next minion you play this turn will not have its cost reduced
			def summoner = this_minion
			this_minion.when_a_cost_is_evaluated(text) {
				if (that_target.is_a_minion() && that_target.controller == summoner.controller) {
					if (use_counter == 0) { // only the first time
						cost_increase -= 1
					}
				}
			}
			this_minion.when_a_minion_is_played {
				if (that_minion.controller == summoner.controller && use_counter == 0) {
					use_counter = 1
				}
			}
			this_minion.when_its_controller_turn_starts {
				use_counter = 0
			}
		}
	}
}

class PriestessOfElune extends CardDefinition {
	PriestessOfElune() {
		name='Priestess of Elune'; type='minion'; cost=6; attack=5; max_health=4
		text='Battlecry: Restore 4 Health to your hero.'
		when_played(text) {
			this_minion.restore_health(4, your_hero)
		}
	}
}

class QuestingAdventurer extends CardDefinition {
	QuestingAdventurer() {
		name='Questing Adventurer'; type='minion'; cost=3; attack=2; max_health=2
		text='Whenever you play a card, gain +1/+1.'
		when_coming_in_play(text) {
			def _adv = this_minion
			_adv.when_its_controller_plays_a_card('gain +1/+1') {
				_adv.gains('+1/+1')
			}
		}
	}
}

class RagingWorgen extends CardDefinition {
	RagingWorgen() {
		name='Raging Worgen'; type='minion'; cost=3; attack=3; max_health=3
		text='Enrage: Windfury and +1 Attack'
		when_enraged(text) {
			this_minion.gains('+1 Attack')
			this_minion.gains(BuffType.WINDFURY)
		}
		when_enraged_no_more('Remove +1 Attack, Windfury') {
			this_minion.remove_first_buff('+1 Attack')
			this_minion.remove_first_buff(BuffType.WINDFURY)
		}
	}
}

class RagnarosTheFirelord extends CardDefinition {
	RagnarosTheFirelord() {
		name='Ragnaros the Firelord'; type='minion'; cost=8; attack=8; max_health=8
		text="Can't Attack. At the end of your turn, deal 8 damage to a random enemy."
		when_coming_in_play("add $text") {
			def ragnaros = this_minion
			this_minion.gains(BuffType.CANNOT_ATTACK)
			this_minion.when_its_controller_turn_ends("deal 8 damage to a random enemy") {
				def all_enemies = opponent.minions + opponent.hero
				ragnaros.deal_damage(8, random_pick(all_enemies))
			}
		}
	}
}

class RaidLeader extends CardDefinition {
	RaidLeader() {
		name='Raid Leader'; type='minion'; cost=3; attack=2; max_health=2
		text='Your other minions have +1 Attack.'
		when_coming_in_play("add $text") {
			def raid_leader = this_minion
			this_minion.when_attack_is_evaluated(text) {
				if (that_target.is_a_minion() &&
					that_target.controller == raid_leader.controller &&
					that_target != raid_leader)
				{
					attack_increase += 1
				}
			}
		}
	}
}

class RavenholdtAssassin extends CardDefinition {
	RavenholdtAssassin() {
		name='Ravenholdt Assassin'; type='minion'; cost=7; attack=7; max_health=5
		text='Stealth'
		when_coming_in_play(text) { this_minion.gains(BuffType.STEALTH) }
	}
}

class RecklessRocketeer extends CardDefinition {
	RecklessRocketeer() {
		name='Reckless Rocketeer'; type='minion'; cost=6; attack=5; max_health=2
		text='Charge'
		when_coming_in_play(text) {
			this_minion.gains(BuffType.CHARGE)
		}
	}
}

class Boar extends CardDefinition {
	Boar() {
		name="Boar"; type='minion'; creature_type="beast"; cost=1; attack=1; max_health=1
		collectible=false
	}
}

class RazorfenHunter extends CardDefinition {
	RazorfenHunter() {
		name='Razorfen Hunter'; type='minion'; cost=3; attack=2; max_health=3
		text='Battlecry: Summon a 1/1 Boar.'
		when_played(text) {
			game.summon(you, "Boar")
		}
	}
}

class RiverCrocolisk extends CardDefinition {
	RiverCrocolisk() {
		name='River Crocolisk'; type='minion'; creature_type='beast'; cost=2; attack=2; max_health=3
	}
}

class ScarletCrusader extends CardDefinition {
	ScarletCrusader() {
		name='Scarlet Crusader'; type='minion'; cost=3; attack=3; max_health=1
		text='Divine Shield'
		when_coming_in_play(text) {
			this_minion.gains(BuffType.DIVINE_SHIELD)
		}
	}
}

class SeaGiant extends CardDefinition {
	SeaGiant() {
		name='Sea Giant'; type='minion'; cost=10; attack=8; max_health=8
		text='Costs (1) less for each other minion on the battlefield.' // all minions
		when_its_cost_is_evaluated(text) {
			def other_minions = all_minions - this_minion
			cost_increase = -(other_minions.size())
		}
	}
}

class Secretkeeper extends CardDefinition {
	Secretkeeper() {
		name='Secretkeeper'; type='minion'; cost=1; attack=1; max_health=2
		text='Whenever a Secret is played, gain +1/+1.'
		when_coming_in_play(text) {
			def _secretkeeper = this_minion
			_secretkeeper.when_a_spell_is_played("check $text") {
				if (that_spell.is_a_secret) {
					_secretkeeper.gains('+1/+1')
				}
			}
		}
	}
}

class SenJinShieldmasta extends CardDefinition {
	SenJinShieldmasta() {
		name="Sen'jin Shieldmasta"; type='minion'; cost=4; attack=3; max_health=5
		text='Taunt'
		when_coming_in_play(text) {
			this_minion.gains(BuffType.TAUNT)
		}
	}
}

class ShadeOfNaxxramas extends CardDefinition {
	ShadeOfNaxxramas() {
		name='Shade of Naxxramas'; type='minion'; cost=3; attack=2; max_health=2
		text='Stealth. At the start of your turn, gain +1/+1.'
		when_coming_in_play("add $text") {
			def _shade = this_minion
			this_minion.gains(BuffType.STEALTH)
			_shade.when_its_controller_turn_starts("gain +1/+1") {
				_shade.gains("+1/+1")
			}
		}
	}
}

class ShatteredSunCleric extends CardDefinition {
	ShatteredSunCleric() {
		name='Shattered Sun Cleric'; type='minion'; cost=3; attack=3; max_health=2
		text='Battlecry: Give a friendly minion +1/+1.'
		get_targets=[ { your_minions } ]
		when_played(text) {
			if (your_minions.size() > 0) {
				select_target(your_minions).gains('+1/+1')
			}
		}
	}
}

class Shieldbearer extends CardDefinition {
	Shieldbearer() {
		name='Shieldbearer'; type='minion'; cost=1; attack=0; max_health=4
		text='Taunt'
		when_coming_in_play(text) { this_minion.gains(BuffType.TAUNT) }
	}
}

class SilverbackPatriarch extends CardDefinition {
	SilverbackPatriarch() {
		name='Silverback Patriarch'; type='minion'; creature_type='beast'; cost=3; attack=1; max_health=4
		text='Taunt'
		when_coming_in_play(text) {
			this_minion.gains(BuffType.TAUNT)
		}
	}
}

class Squire extends CardDefinition {
	Squire() {
		name='Squire'; type='minion'; cost=1; attack=2; max_health=2
		collectible=false
	}
}

class SilverHandKnight extends CardDefinition {
	SilverHandKnight() {
		name='Silver Hand Knight'; type='minion'; cost=5; attack=4; max_health=4
		text='Battlecry: Summon a 2/2 Squire.'
		when_played(text) {
			game.summon(you, "Squire")
		}
	}
}

class SilvermoonGuardian extends CardDefinition {
	SilvermoonGuardian() {
		name='Silvermoon Guardian'; type='minion'; cost=4; attack=3; max_health=3
		text='Divine Shield'
		when_coming_in_play(text) {
			this_minion.gains(BuffType.DIVINE_SHIELD)
		}
   }
}

class Skeleton extends CardDefinition {
	Skeleton() {
		name='Skeleton'; type='minion'; cost=1; attack=1; max_health=1
	}
}

class Slime extends CardDefinition {
	Slime() {
		name='Slime'; type='minion'; cost=1; attack=1; max_health=2
		text='Taunt'
		when_coming_in_play("add $text") {
			this_minion.gains(BuffType.TAUNT)
		}
	}
}

class SludgeBelcher extends CardDefinition {
	SludgeBelcher() {
		name='Sludge Belcher'; type='minion'; cost=5; attack=3; max_health=5
		text='Taunt. Deathrattle: Summon a 1/2 Slime with Taunt.'
		when_coming_in_play("add $text") {
			this_minion.gains(BuffType.TAUNT)
			this_minion.when_it_is_destroyed("Deathrattle: Summon a 1/2 Slime with Taunt.") {
				game.summon(this_minion.controller, "Slime")
			}
		}
	}
}

class SouthseaCaptain extends CardDefinition {
	SouthseaCaptain() {
		name='Southsea Captain'; type='minion'; creature_type='pirate'; cost=3; attack=3; max_health=3
		text='Your other Pirates have +1/+1.'
		when_coming_in_play("add $text") {
			def _ssc = this_minion
			_ssc.when_attack_is_evaluated("check $text") {
				if (that_target.is_a_minion() &&
					that_minion.controller == _ssc.controller &&
					that_minion.is_a_pirate() &&
					that_minion != _ssc)
				{
					attack_increase += 1
				}
			}
			_ssc.when_health_is_evaluated("check $text") {
				if (that_target.is_a_minion() &&
					that_minion.controller == _ssc.controller &&
					that_minion.is_a_pirate() &&
					that_minion != _ssc)
				{
					health_increase += 1
				}
			}
		}
	}
}

class SouthseaDeckhand extends CardDefinition {
	SouthseaDeckhand() {
		name='Southsea Deckhand'; type='minion'; creature_type='pirate'; cost=1; attack=2; max_health=1
		text='Has Charge while you have a weapon equipped.'
		when_coming_in_play(text) {
			def ssd = this_minion
			ssd.when_a_buff_is_evaluated(text) {
				if (that_target == ssd &&
					ssd.controller.hero.weapon != null &&
					that_buff_type == BuffType.CHARGE)
				{
						has_buff = true
						stop_action = true
				}
			}
		}
	}
}

class SpectralKnight extends CardDefinition {
	SpectralKnight() {
		name='Spectral Knight'; type='minion'; cost=5; attack=4; max_health=6
		text="Can't be targeted by spells or Hero Powers."
		when_coming_in_play("add $text") {
			this_minion.gains(CANNOT_BE_TARGETED_BY_SPELL_OR_POWER)
		}
	}
}

class SpectralSpider extends CardDefinition {
	SpectralSpider() {
		name='Spectral Spider'; type='minion'; cost=1; attack=1; max_health=1
	}
}

class Spellbreaker extends CardDefinition {
	Spellbreaker() {
		name='Spellbreaker'; type='minion'; cost=4; attack=4; max_health=3
		text='Battlecry: Silence a minion.'
		get_targets=[ { all_minions } ]
		when_played(text) {
			this_minion.silence(select_target(all_minions))
		}
	}
}

class SpitefulSmith extends CardDefinition {
	SpitefulSmith() {
		name='Spiteful Smith'; type='minion'; cost=5; attack=4; max_health=6
		text='Enrage: Your weapon has +2 Attack.'
		when_coming_in_play(text) {
			this_minion.when_enraged(text) {
				if (this_minion.controller.hero.weapon != null) {
					this_minion.controller.hero.weapon.gains('+2 Attack')
				}
			}
		}
	}
}

class StormpikeCommando extends CardDefinition {
	StormpikeCommando() {
		name='Stormpike Commando'; type='minion'; cost=5; attack=4; max_health=2
		text='Battlecry: Deal 2 damage.'
		get_targets=[ { all_characters } ]
		when_played(text) {
			this_minion.deal_damage(2, select_target(all_characters))
		}
	}
}

class StormwindChampion extends CardDefinition {
	StormwindChampion() {
		name='Stormwind Champion'; type='minion'; cost=7; attack=6; max_health=6
		text='Your other minions have +1/+1.'
		when_coming_in_play(text) {
			def stormwind_champion = this_minion
			stormwind_champion.when_attack_is_evaluated(text) {
				if (that_target.is_a_minion() &&
				that_target.controller == stormwind_champion.controller &&
				that_target != stormwind_champion)
				{
					attack_increase += 1
				}
			}
			stormwind_champion.when_health_is_evaluated(text) {
				if (that_target.is_a_minion() &&
				that_target.controller == stormwind_champion.controller &&
				that_target != stormwind_champion)
				{
					health_increase += 1
				}
			}
		}
	}
}

class Stalagg extends CardDefinition {
	Stalagg() {
		name='Stalagg'; type='minion'; cost=5; attack=7; max_health=4
		text='Deathrattle: If Feugen also died this game, summon Thaddius.'
		when_it_is_destroyed("check $text") {
			game.stalagg_died = true
			if (game.feugen_died && this_minion.controller.minions().size() < 7) {
				game.summon(this_minion.controller, "Thaddius")
			}
		}
	}
}

class StampedingKodo extends CardDefinition {
	StampedingKodo() {
		name='Stampeding Kodo'; type='minion'; creature_type='beast'; cost=5; attack=3; max_health=5
		text='Battlecry: Destroy a random enemy minion with 2 or less Attack.'
		when_played("check $text") {
			this_minion.destroy(random_pick(enemy_minions.findAll{it.get_attack() <= 2}))
		}
	}
}

class StoneskinGargoyle extends CardDefinition {
	StoneskinGargoyle() {
		name='Stoneskin Gargoyle'; type='minion'; cost=3; attack=1; max_health=4
		text='At the start of your turn, restore this minion to full Health.'
		when_coming_in_play("add $text") {
			this_minion.when_its_controller_turn_starts("restore this minion to full Health") {
				this_minion.set_health(this_minion.max_health)
			}
		}
	}
}

class StonetuskBoar extends CardDefinition {
	StonetuskBoar() {
		name='Stonetusk Boar'; type='minion'; creature_type='beast'; cost=1; attack=1; max_health=1
		text='Charge'
		when_coming_in_play(text) {
			this_minion.gains(BuffType.CHARGE)
		}
	}
}

class StormwindKnight extends CardDefinition {
	StormwindKnight() {
		name='Stormwind Knight'; type='minion'; cost=4; attack=2; max_health=5
		text='Charge'
		when_coming_in_play (text) {
			this_minion.gains(BuffType.CHARGE)
		}
	}
}

class StranglethornTiger extends CardDefinition {
	StranglethornTiger() {
		name='Stranglethorn Tiger'; type='minion'; creature_type='beast'; cost=5; attack=5; max_health=5
		text='Stealth'
		when_coming_in_play(text) {
			this_minion.gains(BuffType.STEALTH)
		}
	}
}

class SunfuryProtector extends CardDefinition {
	SunfuryProtector() {
		name='Sunfury Protector'; type='minion'; cost=2; attack=2; max_health=3
		text='Battlecry: Give adjacent minions Taunt.'
		when_played(text) {
			this_minion.neighbors()*.have(BuffType.TAUNT)
		}
	}
}

class Sunwalker extends CardDefinition {
	Sunwalker() {
		name='Sunwalker'; type='minion'; cost=6; attack=4; max_health=5
		text='Taunt. Divine Shield'
		when_coming_in_play("add $text") {
			this_minion.gains(BuffType.TAUNT)
			this_minion.gains(BuffType.DIVINE_SHIELD)
		}
	}
}

class SylvanasWindrunner extends CardDefinition {
	SylvanasWindrunner() {
		name='Sylvanas Windrunner'; type='minion'; cost=6; attack=5; max_health=5
		text='Deathrattle: Take control of a random enemy minion.'
		when_coming_in_play(text) {
			def _sylvanas = this_minion
			this_minion.when_it_is_destroyed("check $text") {
				def _enemy_minions = opponent_of(_sylvanas.controller).minions()
				_sylvanas.controller.take_control(random_pick(_enemy_minions))
			}
		}
	}
}

class TaurenWarrior extends CardDefinition {
	TaurenWarrior() {
		name='Tauren Warrior'; type='minion'; cost=3; attack=2; max_health=3
		text='Taunt. Enrage: +3 Attack'
		when_coming_in_play('Taunt') { this_minion.gains(BuffType.TAUNT) }
		when_enraged('Enrage: +3 Attack') { this_minion.gains('+3 Attack') }
		when_enraged_no_more('Remove +3 Attack buff') { this_minion.remove_first_buff('+3 Attack') }
	}
}

class FenCreeper extends CardDefinition {
	FenCreeper() {
		name='Fen Creeper'; type='minion'; cost=5; attack=3; max_health=6
		text="Taunt"
		when_coming_in_play(text) {
			this_minion.gains(BuffType.TAUNT)
		}
	}
}

class Feugen extends CardDefinition {
	Feugen() {
		name='Feugen'; type='minion'; cost=5; attack=4; max_health=7
		text='Deathrattle: If Stalagg also died this game, summon Thaddius.'
		when_it_is_destroyed("check $text") {
			game.feugen_died = true
			if (game.stalagg_died && this_minion.controller.minions().size() < 7) {
				game.summon(this_minion.controller, "Thaddius")
			}
		}
	}
}

class FinkleEinhorn extends CardDefinition {
	FinkleEinhorn () {
		name='Finkle Einhorn'; type='minion'; cost=2; attack=3; max_health=3
		collectible=false
	}
}

class Thaddius extends CardDefinition {
	Thaddius() {
		name='Thaddius'; type='minion'; cost=10; attack=11; max_health=12
		collectible=false
	}
}

class TheBeast extends CardDefinition {
	TheBeast() {
		name='The Beast'; type='minion'; creature_type='beast'; cost=6; attack=9; max_health=7
		text='Deathrattle: Summon a 3/3 Finkle Einhorn for your opponent.'
		when_played("adding $text") {
			def _the_beast = this_minion
			this_minion.when_it_is_destroyed(text) {
				game.summon(opponent_of(_the_beast.controller), "Finkle Einhorn")
			}
		}
	}
}

class TheBlackKnight extends CardDefinition {
	TheBlackKnight() {
		name='The Black Knight'; type='minion'; cost=6; attack=4; max_health=5
		text='Battlecry: Destroy an enemy minion with Taunt.'
		get_targets=[ { enemy_minions.findAll{it.has_buff(BuffType.TAUNT)} } ]
		when_played(text) {
			def _choices = enemy_minions.findAll{it.has_buff(BuffType.TAUNT)}
			this_minion.destroy(select_target(_choices))
		}
	}
}

class TheCoin extends CardDefinition {
	TheCoin() {
		name='The Coin'; type='spell'; cost=0
		text='Gain 1 Mana Crystal this turn only.'
		when_played(text) { you.add_available_mana(1) }
	}
}

class ThrallmarFarseer extends CardDefinition {
	ThrallmarFarseer() {
		name='Thrallmar Farseer'; type='minion'; cost=3; attack=2; max_health=3
		text='Windfury'
		when_coming_in_play(text) { this_minion.gains(BuffType.WINDFURY) }
	}
}

class Squirrel extends CardDefinition {
	Squirrel() {
		name='Squirrel'; type='minion'; creature_type='beast'; cost=1; attack=1; max_health=1
		collectible=false
	}
}

class Devilsaur extends CardDefinition {
	Devilsaur() {
		name='Devilsaur'; type='minion'; creature_type='beast'; cost=5; attack=5; max_health=5
		collectible=false
	}
}

class TinkmasterOverspark extends CardDefinition {
	TinkmasterOverspark() {
		name='Tinkmaster Overspark'; type='minion'; cost=3; attack=3; max_health=3
		text='Battlecry: Transform another random minion into a 5/5 Devilsaur or a 1/1 Squirrel at random.'
		when_played(text) {
			if (all_minions.size() > 0) {
				def c = random_pick(all_minions)
				game.transform(c, random_pick([ 'Devilsaur', 'Squirrel' ]))
			}
		}
	}
}

class TwilightDrake extends CardDefinition {
	TwilightDrake() {
		name='Twilight Drake'; type='minion'; creature_type='dragon'; cost=4; attack=4; max_health=1
		text='Battlecry: Gain +1 Health for each card in your hand.'
		when_played(text) {
			this_minion.gains("+${you.hand.size()} Health")
		}
	}
}

class Undertaker extends CardDefinition {
	Undertaker() {
		name='Undertaker'; type='minion'; cost=1; attack=1; max_health=2
		text='Whenever you summon a minion with Deathrattle, gain +1/+1.'
		when_coming_in_play("add $text") {
			def _u = this_minion
			this_minion.when_its_controller_plays_a_card("check $text") {
				if (that_card.is_a_minion() && that_minion.has_deathrattle()) {
					_u.gains('+1/+1')
				}
			}
		}
	}
}

class UnstableGhoul extends CardDefinition {
	UnstableGhoul() {
		name='Unstable Ghoul'; type='minion'; cost=2; attack=1; max_health=3
		text='Taunt. Deathrattle: Deal 1 damage to all minions.'
		when_coming_in_play("add $text") {
			this_minion.gains(BuffType.TAUNT)
			this_minion.when_it_is_destroyed("Deal 1 damage to all minions") {
				this_minion.deal_damage(1, all_minions)
			}
		}
	}
}

class VentureCoMercenary extends CardDefinition {
	VentureCoMercenary() {
		name='Venture Co. Mercenary'; type='minion'; cost=5; attack=7; max_health=6
		text='Your minions cost (3) more.'
		when_coming_in_play("add $text") {
			def _mercenary = this_minion
			_mercenary.when_a_cost_is_evaluated("check $text") {
				if (that_target.is_a_minion() && that_target.controller == _mercenary.controller) {
					cost_increase += 3
				}
			}
		}
	}
}

class VioletApprentice extends CardDefinition {
	VioletApprentice() {
		name='Violet Apprentice'; type='minion'; cost=0; attack=1; max_health=1
		collectible=false
	}
}

class VioletTeacher extends CardDefinition {
	VioletTeacher() {
		name='Violet Teacher'; type='minion'; cost=4; attack=3; max_health=5
		text='Whenever you cast a spell, summon a 1/1 Violet Apprentice.'
		when_coming_in_play("add $text") {
			def _teacher = this_minion
			_teacher.when_its_controller_plays_a_card("check $text") {
				if (that_card.is_a_spell()) {
					game.summon(_teacher.controller, "Violet Apprentice")
				}
			}
		}
	}
}

class VoodooDoctor extends CardDefinition {
	VoodooDoctor() {
		name='Voodoo Doctor'; type='minion'; cost=1; attack=2; max_health=1
		text='Battlecry: Restore 2 Health.'
		get_targets=[ { all_characters } ]
		when_played(text) {
			this_minion.restore_health(2, select_target(all_characters))
		}
	}
}

class WailingSoul extends CardDefinition {
	WailingSoul() {
		name='Wailing Soul'; type='minion'; cost=4; attack=3; max_health=5
		text='Battlecry: Silence your other minions.'
		when_played(text) {
			your_minions.each {
				this_minion.silence(it)
			}
		}
	}
}

class WarGolem extends CardDefinition {
	WarGolem() {
		name='War Golem'; type='minion'; cost=7; attack=7; max_health=7
	}
}

class Whelp extends CardDefinition {
	Whelp() {
		name='Whelp'; type='minion'; creature_type='dragon'; cost=1; attack=1; max_health=1
		collectible=false
	}
}

class WildPyromancer extends CardDefinition {
	WildPyromancer() {
		name='Wild Pyromancer'; type='minion'; cost=2; attack=3; max_health=2
		text='After you cast a spell, deal 1 damage to ALL minions.'
		when_coming_in_play("add $text") {
			this_minion.when_its_controller_plays_a_card("check $text") {
				if (that_card.is_a_spell()) {
					this_minion.deal_damage(1, all_minions)
				}
			}
		}
	}
}

class WindfuryHarpy extends CardDefinition {
	WindfuryHarpy() {
		name='Windfury Harpy'; type='minion'; cost=6; attack=4; max_health=5
		text='Windfury'
		when_coming_in_play("add $text") {
			this_minion.gains(BuffType.WINDFURY)
		}
   }
}

class Wisp extends CardDefinition {
	Wisp() {
		name='Wisp'; type='minion'; cost=0; attack=1; max_health=1
	}
}

class Wolfrider extends CardDefinition {
	Wolfrider() {
		name='Wolfrider'; type='minion'; cost=3; attack=3; max_health=1
		text='Charge'
		when_coming_in_play(text) { this_minion.gains(BuffType.CHARGE) }
	}
}

class WorgenInfiltrator extends CardDefinition {
	WorgenInfiltrator() {
		name='Worgen Infiltrator'; type='minion'; cost=1; attack=2; max_health=1
		text='Stealth'
		when_coming_in_play(text) {
			this_minion.gains(BuffType.STEALTH)
		}
	}
}

class YoungDragonhawk extends CardDefinition {
	YoungDragonhawk() {
		name='Young Dragonhawk'; type='minion'; creature_type='beast'; cost=1; attack=1; max_health=1
		text='Windfury'
		when_coming_in_play(text) {
			this_minion.gains(BuffType.WINDFURY)
		}
	}
}

class YoungPriestess extends CardDefinition {
	YoungPriestess() {
		name='Young Priestess'; type='minion'; cost=1; attack=2; max_health=1
		text='At the end of your turn, give another random friendly minion +1 Health.'
		when_coming_in_play("add $text") {
			def _yop = this_minion
			_yop.when_its_controller_turn_ends(text) {
				def _other_friendly_minions = _yop.controller.minions() - _yop
				random_card(_other_friendly_minions)?.gains('+1 Health')
			}
		}
	}
}

class YouthfulBrewmaster extends CardDefinition {
	YouthfulBrewmaster() {
		name='Youthful Brewmaster'; type='minion'; cost=2; attack=3; max_health=2
		text='Battlecry: Return a friendly minion from the battlefield to your hand.'
		get_targets=[ { your_minions } ]
		when_played(text) {
			select_card(your_minions)?.return_to_hand()
		}
	}
}

class Ysera extends CardDefinition {
	Ysera() {
		name='Ysera'; type='minion'; creature_type='dragon'; cost=9; attack=4; max_health=12
		text='At the end of your turn, draw a Dream Card.'
		when_coming_in_play("add $text") {
			def _ysera = this_minion
			_ysera.when_its_controller_turn_ends("draw a Dream Card") {
				Card c = game.new_card(
					random_pick([
						'Dream',
						'Emerald Drake',
						'Laughing Sister',
						'Nightmare',
						'Ysera Awakens']))
				_ysera.controller.hand.add(c)
			}
		}
	}
}

class YseraAwakens extends CardDefinition {
	YseraAwakens() {
		name='Ysera Awakens'; type='spell'; cost=2
		text="Deal 5 damage to all characters except Ysera."
		collectible=false
		when_played(text) {
			def _targets = all_characters.findAll{it.name != 'Ysera'}
			this_spell.deal_spell_damage(5, _targets)
		}
	}
}

class ZombieChow extends CardDefinition {
	ZombieChow() {
		name='Zombie Chow'; type='minion'; cost=1; attack=2; max_health=3
		text='Deathrattle: Restore 5 Health to the enemy hero.'
		when_it_is_destroyed(text) {
			this_minion.restore_health(5, opponent_of(this_minion.controller).hero)
		}
	}
}