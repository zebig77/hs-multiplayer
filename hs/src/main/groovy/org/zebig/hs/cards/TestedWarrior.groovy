package org.zebig.hs.cards

import org.zebig.hs.game.CardDefinition
import org.zebig.hs.game.Target
import org.zebig.hs.mechanics.buffs.Buff
import org.zebig.hs.mechanics.buffs.BuffType

class ArathiWeaponsmith extends CardDefinition {
	ArathiWeaponsmith() {
		name='Arathi Weaponsmith'; type='minion'; cost=4; attack=3; max_health=3
		text='Battlecry: Equip a 2/2 weapon.'
		reserved_to="Warrior"
		when_played(text) { your_hero.equip_weapon(2,2) }
	}
}

class ArcaniteReaper extends CardDefinition {
	ArcaniteReaper() {
		name='Arcanite Reaper'; type='weapon'; cost=5; attack=5; max_health=2
		reserved_to="Warrior"
	}
}

class Armorsmith extends CardDefinition {
	Armorsmith() {
		name='Armorsmith'; type='minion'; cost=2; attack=1; max_health=4
		text='Whenever a friendly minion takes damage, gain 1 Armor.'
		reserved_to="Warrior"
		when_coming_in_play(text) {
			this_minion.when_a_minion_takes_damage("check $text") {
				if (that_minion.controller == this_minion.controller) { // this=Armorsmith
					this_minion.controller.hero.armor += 1
				}
			}
		}
	}
}

class BattleRage extends CardDefinition {
	BattleRage() {
		name='Battle Rage'; type='spell'; cost=2
		text='Draw a card for each damaged friendly character.'
		reserved_to="Warrior"
		when_played(text) {
			List<Target> friendly_characters = your_hero + your_minions
			you.draw(
				friendly_characters.findAll{it.get_health() < it.get_max_health()}.size() )
		}
	}
}

class Brawl extends CardDefinition {
	Brawl() {
		name='Brawl'; type='spell'; cost=5
		text='Destroy all minions except one. (chosen randomly)'
		reserved_to="Warrior"
		when_played(text) {
			if (all_minions.size() > 1) {
				this_spell.destroy(all_minions - random_pick( 1, all_minions ) )
			}
		}
	}
}

class Charge extends CardDefinition {
	Charge() {
		name='Charge'; type='spell'; cost=3
		text='Give a friendly minion +2 Attack and Charge.'
		reserved_to="Warrior"
		get_targets=[ { your_minion_targets } ]
		when_played(text) {
			def m = select_spell_target( your_minion_targets )
			m.gains('+2 Attack')
			m.gains(BuffType.CHARGE)
		}
	}
}

class Cleave extends CardDefinition {
	Cleave() {
		name='Cleave'; type='spell'; cost=2
		text='Deal 2 damage to two random enemy minions.'
		reserved_to="Warrior"
		when_played(text) {
			this_spell.deal_spell_damage(2, random_pick(2,enemy_minions) as List)
		}
	}
}

class CommandingShout extends CardDefinition {
	CommandingShout() {
		name='Commanding Shout'; type='spell'; cost=2
		text="Your minions can't be reduced below 1 Health this turn. Draw a card."
		reserved_to="Warrior"
		when_played(text) {
			def spell_controller = you
			your_hero.when_a_buff_list_is_evaluated("check $text") {
				if (that_target.is_a_minion() && that_minion.controller == spell_controller) {
					if (that_buff_list.find{it.buff_type == BuffType.CANNOT_BE_REDUCED_BELOW_1_HEALTH} == null) {
						def buff = Buff.create_buff(BuffType.CANNOT_BE_REDUCED_BELOW_1_HEALTH, that_minion)
						that_buff_list.add(buff)
					}
				}
			}.until_end_of_turn()
			you.draw(1)
		}
	}
}

class CruelTaskmaster extends CardDefinition {
	CruelTaskmaster() {
		name='Cruel Taskmaster'; type='minion'; cost=2; attack=2; max_health=2
		text='Battlecry: Deal 1 damage to a minion and give it +2 Attack.'
		reserved_to="Warrior"
		get_targets=[ { all_minions } ]
		when_played(text) {
			def m = select_target(all_minions)
			if (m != null) {
				this_minion.deal_damage(1,m)
				m.gains('+2 Attack')
			}
		}
	}
}

class Execute extends CardDefinition {
	Execute() {
		name='Execute'; type='spell'; cost=1
		text='Destroy a damaged enemy minion.'
		reserved_to="Warrior"
		get_targets=[ { enemy_minion_targets.findAll{m->m.get_health() < m.get_max_health()} } ]
		when_played(text) {
			this_spell.destroy(
				// TODO select_spell_target qui exploite get_targets ?
					select_spell_target(
                            enemy_minion_targets.findAll{
                                m->m.get_health() < m.get_max_health()} as List<Target>) )
		}
	}
}

class FieryWarAxe extends CardDefinition {
	FieryWarAxe() {
		name='Fiery War Axe'; type='weapon'; cost=2; attack=3; max_health=2
		reserved_to="Warrior"
	}
}

class FrothingBerserker extends CardDefinition {
	FrothingBerserker() {
		name='Frothing Berserker'; type='minion'; cost=3; attack=2; max_health=4
		text='Whenever a minion takes damage, gain +1 Attack.'
		reserved_to="Warrior"
		when_a_minion_takes_damage('gain +1 Attack') { 
			this_minion.gains('+1 Attack')
		}
	}
}

class Gorehowl extends CardDefinition {
	Gorehowl() {
		name='Gorehowl'; type='weapon'; cost=7; attack=7; max_health=1
		text='Attacking a minion costs 1 Attack instead of 1 Durability.'
		reserved_to="Warrior"
		when_played(text) {
			def weapon = your_hero.weapon
			weapon.when_its_durability_is_reduced("check $text") {
				if (the_attacked.is_a_minion()) {
					weapon.set_attack(weapon.attack-1)
					stop_action=true
				}
			}
		}
	}
}

class GrommashHellscream extends CardDefinition {
	GrommashHellscream() {
		name='Grommash Hellscream'; type='minion'; cost=8; attack=4; max_health=9
		text='Charge. Enrage: +6 Attack'
		reserved_to="Warrior"
		when_coming_in_play(text) {
			this_minion.gains(BuffType.CHARGE)
		}
		when_enraged('+6 Attack') {
			this_minion.gains("+6 Attack")
		}
		when_enraged_no_more('Remove +6 Attack buff') {
			this_minion.remove_first_buff("+6 Attack")
		}
	}
}

class HeroicStrike extends CardDefinition {
	HeroicStrike() {
		name='Heroic Strike'; type='spell'; cost=2
		text='Give your hero +4 Attack this turn.'
		reserved_to="Warrior"
		when_played(text) { your_hero.gains('+4 Attack').until_end_of_turn() }
	}
}

class InnerRage extends CardDefinition {
	InnerRage() {
		name='Inner Rage'; type='spell'; cost=0
		text='Deal 1 damage to a minion and give it +2 Attack.'
		reserved_to="Warrior"
		get_targets=[ { all_minion_targets } ]
		when_played(text) {
			def m = select_spell_target(all_minion_targets)
			this_spell.deal_damage(1, m)
			m.gains('+2 Attack')
		}
	}
}

class KorKronElite extends CardDefinition { // tested with Blessing of Wisdom
	KorKronElite() {
		name="Kor'kron Elite"; type='minion'; cost=4; attack=4; max_health=3
		text='Charge'
		reserved_to="Warrior"
		when_coming_in_play(text) { 
			this_minion.gains(BuffType.CHARGE)
		}
	}
}

class MortalStrike extends CardDefinition {
	MortalStrike() {
		name='Mortal Strike'; type='spell'; cost=4
		text='Deal 4 damage. If your hero has 12 or less Health, deal 6 damage instead.'
		reserved_to="Warrior"
		get_targets=[ { all_targets } ]
		when_played(text) {
			if (your_hero.get_health() <= 12) {
				this_spell.deal_spell_damage(6, select_spell_target(all_targets))
			}
			else {
				this_spell.deal_spell_damage(4, select_spell_target(all_targets))
			}
		}
	}
}

class Rampage extends CardDefinition {
	Rampage() {
		name='Rampage'; type='spell'; cost=2
		text='Give a damaged minion +3/+3.'
		reserved_to="Warrior"
		get_targets=[ { your_minions.findAll{it.get_health() < it.get_max_health()} } ]
		when_played(text) {
			select_spell_target(
                    your_minions.findAll{it.get_health() < it.get_max_health()} as List<Target>
            ).gains("+3/+3")
		}
	}
}

class ShieldBlock extends CardDefinition {
	ShieldBlock() {
		name='Shield Block'; type='spell'; cost=3
		text='Gain 5 Armor. Draw a card.'
		reserved_to="Warrior"
		when_played(text) {
			your_hero.add_armor(5)
			you.draw(1)
		}
	}
}

class ShieldSlam extends CardDefinition {
	ShieldSlam() {
		name='Shield Slam'; type='spell'; cost=1
		text='Deal 1 damage to a minion for each Armor you have.'
		reserved_to="Warrior"
		get_targets=[ { all_minion_targets } ]
		before_play("check armor") {
			check( your_hero.armor > 0, 'no armor' )
		}
		when_played(text) {
			def m = select_spell_target(all_minion_targets)
			this_spell.deal_spell_damage(your_hero.armor, m )
		}
	}
}

class Slam extends CardDefinition {
	Slam() {
		name='Slam'; type='spell'; cost=2
		text='Deal 2 damage to a minion. If it survives, draw a card.'
		reserved_to="Warrior"
		get_targets=[ { all_minion_targets } ]
		when_played(text) {
			def m = select_spell_target(all_minion_targets)
			this_spell.deal_spell_damage(2, m)
			if (! m.is_dead()) {
				you.draw(1)
			}
		}
	}
}

class Upgrade extends CardDefinition {
	Upgrade() {
		name='Upgrade!'; type='spell'; cost=1
		text='If you have a weapon, give it +1/+1. Otherwise equip a 1/3 weapon.'
		reserved_to="Warrior"
		when_played(text) {
			if (your_hero.weapon != null) {
				your_hero.weapon.gains("+1/+1")
			} else {
				your_hero.equip_weapon(1, 3)
			}
		}
	}
}

class WarsongCommander extends CardDefinition {
	WarsongCommander() {
		name='Warsong Commander'; type='minion'; cost=3; attack=2; max_health=3
		text='Whenever you play a minion with 3 or less Attack, give it Charge.'
		reserved_to="Warrior"
		when_coming_in_play("add $text") {
			def _commander = this_minion
			_commander.when_its_controller_plays_a_card("check $text") {
				if (that_card.is_a_minion() && that_minion.get_attack() <= 3) {
					that_minion.gains(BuffType.CHARGE)
				}
			}
		}
	}
}

class Whirlwind extends CardDefinition {
	Whirlwind() {
		name='Whirlwind'; type='spell'; cost=1
		text='Deal 1 damage to ALL minions.'
		reserved_to="Warrior"
		when_played(text) { 
			this_spell.deal_damage(1, all_minions) 
		}
	}
}
