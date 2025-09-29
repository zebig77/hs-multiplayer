package org.zebig.hs.mechanics.buffs

class BuffType {
	
	final String name
	
	static all_buff_types = []
	
	static final BuffType CANNOT_ATTACK = 
		new BuffType( "Can't attack")
	static final BuffType CANNOT_BE_REDUCED_BELOW_1_HEALTH = 
		new BuffType( "Can't be reduced below 1 Health")
	static final BuffType CANNOT_BE_TARGETED_BY_SPELL_OR_POWER = 
		new BuffType( "Can't be targeted by spell or power")
	static final BuffType CHARGE = 
		new BuffType( "Charge" )
	static final BuffType CORRUPTION = 
		new BuffType( "Corruption" )
	static final BuffType DIE_AT_THE_END_OF_TURN = 
		new BuffType( "Die at the end of turn" )
	static final BuffType DESTROY_ALL_MINIONS_AT_START_OF_TURN = 
		new BuffType( "Destroy all minions at the start of your turn" )
	static final BuffType DIVINE_SHIELD = 
		new BuffType( "Divine shield" )
	static final BuffType FROZEN = 
		new BuffType( "Frozen" )
	static final BuffType IMMUNE = 
		new BuffType( "Immune" )
	static final BuffType RETURN_TO_BATTLEFIELD_WHEN_DESTROYED = 
		new BuffType( "Return to battlefield when destroyed" )
	static final BuffType STEALTH =
		new BuffType( "Stealth" )
	static final BuffType TAUNT =
		new BuffType( "Taunt" )
	static final BuffType WINDFURY = 
		new BuffType( "Windfury" )
		
	static final STATELESS_BUFFS = [
			CANNOT_ATTACK, 
			CANNOT_BE_REDUCED_BELOW_1_HEALTH,
			CANNOT_BE_TARGETED_BY_SPELL_OR_POWER,
			CHARGE,
			CORRUPTION,
			DESTROY_ALL_MINIONS_AT_START_OF_TURN,
			DIE_AT_THE_END_OF_TURN,
			DIVINE_SHIELD,
			IMMUNE,
			RETURN_TO_BATTLEFIELD_WHEN_DESTROYED,
			STEALTH,
			TAUNT,
			WINDFURY
		]
	
	static String normalized(String name) {
		return name.trim().toLowerCase()
	}
	
	private BuffType(String name) {
		this.name = normalized(name)
		all_buff_types += this
	}
	
	static BuffType getInstance(name) {
		def btn = normalized(name)
		def bt = all_buff_types.find{ it.name ==  btn }
		if (bt == null) {
			return new BuffType(btn) // for example +5 health
		}
		return bt
	}
	
	boolean equals(Object o) {
		if (o == null || o.class != BuffType.class) {
			return false
		}
		return (o.name == this.name)		
	}
	
	int hashCode() {
		return this.name.hashCode()
	}
	
	String toString() {
		return this.name
	}

}
