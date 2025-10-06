package org.zebig.hs.game

import org.zebig.hs.logger.Log
import org.zebig.hs.mechanics.events.BeforeUsePower
import org.zebig.hs.mechanics.events.ItComesInPlay

class Hero extends Target {
	
	Hero(Player controller, String hero_name, HeroPowerDefinition power_definition) {
		super(hero_name, "hero", 30, controller.game)
		this.controller = controller
		this.power = new HeroPower(power_definition)
		this.target_type = 'hero'
		this.armor = 0
		this.weapon = null
		this.is_a_demon = false // except Jaraxxus...
	}
	
	HeroPower getPower() { state.power }
	void setPower(HeroPower hp) { state.power = hp }
	
	int getArmor() { state.armor }
	void setArmor(int a) { state.armor = a }
	
	Weapon getWeapon() { state.weapon }
	void setWeapon(Weapon w) { state.weapon = w }
	
	boolean getIs_a_demon() { state.is_a_demon }
	void setIs_a_demon(boolean iad) { state.is_a_demon = iad }
	
	
	def add_armor(int amount) {
		armor += amount
		if (armor < 0) {
			armor = 0
		}
		Log.info "   - $this armor = $armor"
	}
	
	boolean can_use_power(StringBuilder reason=new StringBuilder()) {
		if (power.use_counter > 0) {
			reason << "already used"
			return false
		}
		if (power.cost > game.active_player.available_mana) {
			reason << "not enough mana"
			return false
		}
		try {
			new BeforeUsePower(power).check()
		}
		catch(IllegalActionException e) {
			reason << e.getMessage()
			return false
		}
		return true
	}
	
	def deal_power_damage(int amount, Target target) {
		int damage = controller.get_power_damage(amount)
		deal_damage(damage, target)
	}

	def equip_weapon(String card_name) {
		def w = new Weapon(game.card_library.getCardDefinition(card_name))
		equip_weapon(w)
	}
	
	def equip_weapon(Weapon w) {
		this.weapon = w
		w.controller = this.controller
        Log.info "      . $this equips $w (${w.get_attack()}/${w.get_durability()})"
		new ItComesInPlay(w).check()
	}
	
	// builds dynamically a weapon
	def equip_weapon(int attack, int durability) {
		def wd = new CardDefinition(game)
		wd.name = "Weapon"
		wd.attack = attack
		wd.max_health = durability
		wd.type = "weapon"
		equip_weapon( new Weapon(wd) )
	}
	
	int get_attack() {
		def weapon_attack = weapon == null ? 0 : weapon.get_attack()
		return evaluate_attack(weapon_attack)
	}
	
}

class AnduinWrynn extends Hero {
	AnduinWrynn(Player p) {
		super(p, "Anduin Wrynn", new LesserHeal(p.game))
	}
}

class GarroshHellscream extends Hero {
	GarroshHellscream(Player p) {
		super(p, "Garrosh Hellscream", new ArmorUp(p.game))
	}
}

class Guldan extends Hero {
	Guldan(Player p) {
		super(p, "Gul'dan", new LifeTap(p.game))
	}
}

class JainaProudmoore extends Hero {
	JainaProudmoore(Player p) {
		super(p, "Jaina Proudmoore", new Fireblast(p.game))
	}
}

class LordJaraxxusHero extends Hero {
	LordJaraxxusHero(Player p) {
		super(p, "Lord Jaraxxus", new Inferno(p.game))
	}

	boolean is_a_demon() {
		return true
	}
}

class MalfurionStormrage extends Hero {
	MalfurionStormrage(Player p) {
		super(p, "Malfurion Stormrage", new Shapeshift(p.game))
	}
}

class Rexxar extends Hero {
	Rexxar(Player p) {
		super(p, "Rexxar", new SteadyShot(p.game))
	}
}

class Thrall extends Hero {
	Thrall(Player p) {
		super(p, "Thrall", new TotemicCall(p.game))
	}
}

class UtherLightbringer extends Hero {
	UtherLightbringer(Player p) {
		super(p, "Uther Lightbringer", new Reinforce(p.game))
	}
}


class ValeeraSanguinar extends Hero {
	ValeeraSanguinar(Player p) {
		super(p, "Valeera Sanguinar", new DaggerMastery(p.game))
	}
}
