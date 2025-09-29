package org.zebig.hs.game


class HeroPowerDefinition extends DefinitionObject {
	
	int cost
	
	HeroPowerDefinition(Game game, String name, String text) {
        super(game)
		this.name = name
		this.text = text
		this.cost = 2
	}	
}

class LesserHeal extends HeroPowerDefinition {
	LesserHeal(Game g) {
		super( g, 'Lesser Heal', 'Restore 2 Health')
		get_targets = [ { all_targets } ]
		when_this_power_is_used(text) {
			your_hero.restore_health(2, select_target(all_targets))
		}
	}
}

class LifeTap extends HeroPowerDefinition {
	LifeTap(Game g) {
		super( g, 'Life Tap', 'Draw a card and take 2 damage')
		when_this_power_is_used(text) {
			you.draw(1)
			your_hero.receive_damage(2)
		}
	}
}

class MindSpike  extends HeroPowerDefinition {
	MindSpike(Game g) {
		super( g, 'Mind Spike', 'Deal 2 damage')
		get_targets = [ { all_targets } ]
		when_this_power_is_used(text) {
			your_hero.deal_power_damage(2, select_target(all_targets))
		}
	}
} 

class MindShatter  extends HeroPowerDefinition {
	MindShatter(Game g) {
		super( g, 'Mind Shatter', 'Deal 3 damage')
		get_targets = [ { all_targets } ]
		when_this_power_is_used(text) {
			your_hero.deal_power_damage(3, select_target(all_targets))
		}
	}
} 

class DaggerMastery extends HeroPowerDefinition {
	DaggerMastery(Game g) {
		super( g, 'Dagger Mastery', 'Equip a 1/2 Dagger')
		when_this_power_is_used(text) {
			your_hero.equip_weapon("Wicked Knife")
		}
	}
}

class Fireblast extends HeroPowerDefinition {
	Fireblast(Game g) {
		super( g, 'Fireblast', 'Deal 1 damage')
		get_targets = [ { all_targets } ]
		when_this_power_is_used(text) {
			your_hero.deal_power_damage(1, select_target(all_targets - your_hero))
		}
	}
}

class ArmorUp extends HeroPowerDefinition {
	ArmorUp(Game g) {
		super( g, 'Armor Up!', 'Gain 2 armor')
		when_this_power_is_used(text) {
			your_hero.add_armor(2)
		}
	}
}

class Reinforce extends HeroPowerDefinition {
	Reinforce(Game g) {
		super( g, 'Reinforce', 'Summon a 1/1 Silver Hand Recruit')
		when_this_power_is_used(text) {
			game.summon(you, "Silver Hand Recruit")
		}
	}
}

class Shapeshift extends HeroPowerDefinition {
	Shapeshift(Game g) {
		super( g, 'Shapeshift', '+1 Attack this turn, +1 Armor' )
		when_this_power_is_used(text) {
			your_hero.add_armor(1)
			your_hero.gains('+1 Attack').until_end_of_turn()
		}
	}
}

class TotemicCall extends HeroPowerDefinition {
	static power_totems = [ "Healing Totem", "Wrath of Air Totem", "Searing Totem", "Stoneclaw Totem" ]
	TotemicCall(Game g) {
		super( g, "Totemic Call", "Summon a random totem")
		before_use_power(text) {
			// check that there is a random totem to summon
			List<Card> in_play_totems = your_minions.findAll {it.creature_type == "totem"}
			if (in_play_totems*.name.containsAll(power_totems)) {
				throw new IllegalActionException("all totems have already been summoned")
			} 
		}
		when_this_power_is_used(text) {
            List<String> in_play_totems = your_minions.findAll {it.creature_type == "totem"}.collect {name}
			List<String> possible_totems = power_totems - in_play_totems
            game.summon(you, random_pick(possible_totems) as String)
		}
	}
}

class SteadyShot extends HeroPowerDefinition {
	SteadyShot(Game g) {
		super( g, 'Steady Shot', 'Deal 2 damage to the enemy hero' )
		when_this_power_is_used(text) {
			your_hero.deal_power_damage(2, opponent_hero)
		}
	}
}

class Inferno extends HeroPowerDefinition {
	Inferno(Game g) {
		super( g, 'Inferno', 'Summon a 6/6 Infernal')
		when_this_power_is_used(text) {
			game.summon(you, "Infernal")
		}
	}
}
