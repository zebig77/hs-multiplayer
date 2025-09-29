package org.zebig.hs.game

import org.zebig.hs.logger.Log
import org.zebig.hs.mechanics.events.BeforeUsePower
import org.zebig.hs.mechanics.events.ItComesInPlay

class Hero extends Target {
	
	Hero(String hero_name, HeroPowerDefinition power_definition) {
		super(hero_name, "hero", 30, power_definition.game)
		this.controller = null // will be set later
		this.power = new HeroPower(power_definition)
		this.target_type = 'hero'
		this.armor = 0
		this.weapon = null
		this.is_a_demon = false // except Jaraxxus...
	}
	
	HeroPower getPower() { ps.power }
	void setPower(HeroPower hp) { ps.power = hp }
	
	int getArmor() { ps.armor }
	void setArmor(int a) { ps.armor = a }
	
	Weapon getWeapon() { ps.weapon }
	void setWeapon(Weapon w) { ps.weapon = w }
	
	boolean getIs_a_demon() { ps.is_a_demon }
	void setIs_a_demon(boolean iad) { ps.is_a_demon = iad }
	
	
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
	AnduinWrynn(Game g) {
		super( "Anduin Wrynn", new LesserHeal(g))
	}
}

class GarroshHellscream extends Hero {	
	GarroshHellscream(Game g) {
		super( "Garrosh Hellscream", new ArmorUp(g))
	}
}

class Guldan extends Hero {	
	Guldan(Game g) {
		super( "Gul'dan", new LifeTap(g))
	}
}

class JainaProudmoore extends Hero {
	JainaProudmoore(Game g) {
		super( "Jaina Proudmoore", new Fireblast(g))
	}
}

class LordJaraxxus extends Hero {
	LordJaraxxus(Game g) {
		super("Lord Jaraxxus", new Inferno(g))
	}
	
	boolean is_a_demon() {
		return true
	}
}

class MalfurionStormrage extends Hero {
	MalfurionStormrage(Game g) {
		super("Malfurion Stormrage", new Shapeshift(g))
	}
}

class Rexxar extends Hero {
	Rexxar(Game g) {
		super("Rexxar", new SteadyShot(g))
	}
}

class Thrall extends Hero {
	Thrall(Game g) {
		super("Thrall", new TotemicCall(g))
	}
}

class UtherLightbringer extends Hero {
	UtherLightbringer(Game g) {
		super("Uther Lightbringer", new Reinforce(g))
	}
}


class ValeeraSanguinar extends Hero {
	ValeeraSanguinar(Game g) {
		super("Valeera Sanguinar", new DaggerMastery(g))
	}
}
