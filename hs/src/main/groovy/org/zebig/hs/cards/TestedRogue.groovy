package org.zebig.hs.cards

import org.zebig.hs.game.Card
import org.zebig.hs.game.CardDefinition
import org.zebig.hs.mechanics.buffs.BuffType

class AnubArAmbusher extends CardDefinition {
	AnubArAmbusher() {
		name="Anub'ar Ambusher"; type='minion'; cost=4; attack=5; max_health=5
		text='Deathrattle: Return a random friendly minion to your hand.'
		reserved_to="Rogue"
		when_it_is_destroyed(text) {
			(random_pick(this_minion.controller.minions()) as Card)?.return_to_hand()
		}
	}
}

class Assassinate extends CardDefinition {
	Assassinate() {
		name='Assassinate'; type='spell'; cost=5
		text='Destroy an enemy minion.'
		reserved_to="Rogue"
		get_targets=[ { enemy_minion_targets } ]
		when_played(text) { 
			this_spell.destroy( select_spell_target(enemy_minion_targets) ) 
		}
	}
}

class AssassinsBlade extends CardDefinition {
	AssassinsBlade() {
		name="Assassin's Blade"; type='weapon'; cost=5; attack=3; max_health=4
		reserved_to="Rogue"
	}
}

class Backstab extends CardDefinition {
	Backstab() {
		name='Backstab'; type='spell'; cost=0
		text='Deal 2 damage to an undamaged minion.'
		reserved_to="Rogue"
		get_targets=[ { all_minion_targets.findAll{it.get_health() == it.get_max_health()} } ]
		when_played(text) {
			this_spell.deal_spell_damage(2,
					select_spell_target(
						all_minion_targets.findAll{
							m->m.get_health() == m.get_max_health()} ) )
		}
	}
}

class Betrayal extends CardDefinition {
	Betrayal() {
		name='Betrayal'; type='spell'; cost=2
		text='An enemy minion deals its damage to the minions next to it.'
		reserved_to="Rogue"
		get_targets=[ { enemy_minion_targets } ]
		when_played(text) {
			def c = select_spell_target(enemy_minion_targets)
			c.deal_damage( c.get_attack(), (c as Card).neighbors())
		}
	}
}

class BladeFlurry extends CardDefinition {
	BladeFlurry() {
		name='Blade Flurry'; type='spell'; cost=2
		text='Destroy your weapon and deal its damage to all enemies.'
		reserved_to="Rogue"
		before_play("check targets") { 
			check( your_hero.weapon != null, 'you have no weapon') 
		}
		when_played(text) {
			def weapon_attack = your_hero.weapon.get_attack()
			this_spell.destroy(your_hero.weapon)
			this_spell.deal_damage( weapon_attack, opponent_hero + enemy_minions )
		}
	}
}

class ColdBlood extends CardDefinition {
	ColdBlood() {
		name='Cold Blood'; type='spell'; cost=1
		text='Give a minion +2 Attack. Combo: +4 Attack instead.'
		reserved_to="Rogue"
		get_targets=[ { all_minion_targets } ]
        when_played(text) {
			def m = select_spell_target(all_minion_targets)
			if (you.have_combo()) {
				m.gains('+4 Attack')
			} else {
				m.gains('+2 Attack')
			}
		}
    }
}

class Conceal extends CardDefinition {
	Conceal() {
		name='Conceal'; type='spell'; cost=1
		text='Give your minions Stealth until your next turn.'
		reserved_to="Rogue"
		when_played(text) {
			your_minions*.have(BuffType.STEALTH)*.until_your_next_turn()
		}
    }
}

class DeadlyPoison extends CardDefinition {
	DeadlyPoison() {
		name='Deadly Poison'; type='spell'; cost=1
		text='Give your weapon +2 Attack.'
		reserved_to="Rogue"
		before_play("check targets") { 
			check(your_hero.weapon != null, 'you have no weapon') 
		}
		when_played(text) {
			your_hero.weapon.gains("+2 Attack")
		}
    }
}

class DefiasBandit extends CardDefinition {
	DefiasBandit() {
		name='Defias Bandit'; type='minion'; cost=1; attack=2; max_health=1
		reserved_to="Rogue"
		collectible=false
    }
}

class DefiasRingleader extends CardDefinition {
	DefiasRingleader() {
		name='Defias Ringleader'; type='minion'; cost=2; attack=2; max_health=2
		text='Combo: Summon a 2/1 Defias Bandit.'
		reserved_to="Rogue"
		when_played(text) {
			if (you.have_combo()) { 
				game.summon(you, "Defias Bandit")
			}
		}	
    }
}

class Eviscerate extends CardDefinition {
	Eviscerate() {
		name='Eviscerate'; type='spell'; cost=2
		text='Deal 2 damage. Combo: Deal 4 damage instead.'
		reserved_to="Rogue"
		get_targets=[ { all_targets } ]
		when_played(text) {
			def t = select_spell_target(all_targets)
			if (you.have_combo()) {
				this_spell.deal_spell_damage(4,t)
			}
			else {
				this_spell.deal_spell_damage(2,t)
			}
		}
	}
}

class FanOfKnives extends CardDefinition {
	FanOfKnives() {
		name='Fan of Knives'; type='spell'; cost=3
		text='Deal 1 damage to all enemy minions. Draw a card.'
		reserved_to="Rogue"
		when_played(text) {
			this_spell.deal_spell_damage(1, enemy_minions)
			you.draw(1)
		}
	}
}

class Headcrack extends CardDefinition {
	Headcrack() {
		name='Headcrack'; type='spell'; cost=3
		text='Deal 2 damage to the enemy hero. Combo: Return this to your hand next turn.'
		reserved_to="Rogue"
		when_played(text) {
			this_spell.deal_spell_damage(2, opponent_hero)
			if (you.have_combo()) {
				def headcrack = this_spell
				your_hero.when_its_controller_turn_ends('Return this to your hand') {
					headcrack.return_to_hand()
				}.run_once()
			}
		}
	}
}

class Kidnapper extends CardDefinition {
	Kidnapper() {
		name='Kidnapper'; type='minion'; cost=6; attack=5; max_health=3
		text="Combo: Return a minion to its owner's hand."
		reserved_to="Rogue"
		get_targets=[ { all_minions } ]
		when_played(text) {
			if (you.have_combo() && all_minions.size() > 0) {
				select_card(all_minions).return_to_hand()
			}
		}
	}
}

class MasterOfDisguise extends CardDefinition {
	MasterOfDisguise() {
		name='Master of Disguise'; type='minion'; cost=4; attack=4; max_health=4
		text='Battlecry: Give a friendly minion Stealth.'
		reserved_to="Rogue"
		get_targets=[ { your_minions } ]
		when_played(text) {
			select_target(your_minions)?.gains(BuffType.STEALTH)
		}
	}
}

class PatientAssassin extends CardDefinition {
	PatientAssassin() {
		name='Patient Assassin'; type='minion'; cost=2; attack=1; max_health=1
		text='Stealth. Destroy any minion damaged by this minion.'
		reserved_to="Rogue"
		when_coming_in_play(text) {
			this_minion.gains(BuffType.STEALTH)
			this_minion.when_it_deals_damage('Destroy any minion damaged by this minion') {
				if (damaged_target.is_a_minion()) {
					this_minion.destroy(damaged_target)
				}
			}
		}
	}
}

class PerditionsBlade extends CardDefinition {
	PerditionsBlade() {
		name="Perdition's Blade"; type='weapon'; cost=3; attack=2; max_health=2
		text='Battlecry: Deal 1 damage. Combo: Deal 2 instead.'
		reserved_to="Rogue"
		get_targets=[ { all_characters } ]
		when_played(text) {
			if (you.have_combo()) {
				your_hero.weapon.deal_damage(2, select_target(all_characters))
			}
			else {
				your_hero.weapon.deal_damage(1, select_target(all_characters))
			}
		}
	}
}

class Preparation extends CardDefinition {
	Preparation() {
		name='Preparation'; type='spell'; cost=0
		text='The next spell you cast this turn costs (3) less.'
		reserved_to="Rogue"
		when_played(text) {
			def use_counter = 0
			your_hero.when_a_cost_is_evaluated(text) {
				if (that_target.is_a_spell() && that_target.controller == you) {
					if (use_counter == 0) { // only the first time
						cost_increase -= 3
					}
				}
			}.until_end_of_turn()
			your_hero.when_a_spell_is_played('Preparation used') {
				if (that_spell.controller == you && use_counter == 0) {
					use_counter = 1
				}
			}.until_end_of_turn()
		}
	}
}

class Sap extends CardDefinition {
	Sap() {
		name='Sap'; type='spell'; cost=2
		text="Return an enemy minion to its owner's hand."
		reserved_to="Rogue"
		get_targets=[ { enemy_minion_targets } ]
		when_played(text) {
			Card c = select_spell_target(enemy_minion_targets)
			c.return_to_hand()
		}
	}
}

class Shadowstep extends CardDefinition {
	Shadowstep() {
		name='Shadowstep'; type='spell'; cost=0
		text='Return a friendly minion to your hand. It costs (2) less.'
		reserved_to="Rogue"
		get_targets=[ { your_minion_targets } ]
		when_played(text) {
			Card m = select_spell_target(your_minion_targets)
			m.return_to_hand()
			m.gains('costs (2) less')
		}
	}
}

class Shiv extends CardDefinition {
	Shiv() {
		name='Shiv'; type='spell'; cost=2
		text='Deal 1 damage. Draw a card.'
		reserved_to="Rogue"
		get_targets=[ { all_targets } ]
		when_played(text) {
			this_spell.deal_spell_damage(1, select_spell_target(all_targets))
			you.draw(1)
		}
	}
}

class SI7Agent extends CardDefinition {
	SI7Agent() {
		name='SI-7 Agent'; type='minion'; cost=3; attack=3; max_health=3
		text='Combo: Deal 2 damage.'
		reserved_to="Rogue"
		get_targets=[ { all_characters } ]
		when_played(text) {
			if (you.have_combo()) {
				this_minion.deal_damage(2, select_target(all_characters))
			}
		}
	}
}

class SinisterStrike extends CardDefinition {
	SinisterStrike() {
		name='Sinister Strike'; type='spell'; cost=1
		text='Deal 3 damage to the enemy hero.'
		reserved_to="Rogue"
		when_played(text) {
			this_spell.deal_spell_damage(3, opponent_hero)
		}
	}
}

class Sprint extends CardDefinition {
	Sprint() {
		name='Sprint'; type='spell'; cost=7
		text='Draw 4 cards.'
		reserved_to="Rogue"
		when_played(text) {
			you.draw(4)
		}
	}
}

class Vanish extends CardDefinition {
	Vanish() {
		name='Vanish'; type='spell'; cost=6
		text="Return all minions to their owner's hand."
		reserved_to="Rogue"
		when_played(text) {
			all_minions.sort{it.play_order}.each {
				(it as Card).return_to_hand()
			}
		}
	}
}

class WickedKnife extends CardDefinition {
	WickedKnife() {
		name="Wicked Knife"; type='weapon'; cost=1; attack=1; max_health=2
		reserved_to="Rogue"
	}
}