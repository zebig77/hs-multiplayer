package org.zebig.hs.game


class CardDefinition extends DefinitionObject {

	int cost
	String type // minion, spell, weapon
	String creature_type // beast, pirate, demon, ...
	String reserved_to = '' // class ex Priest, = '' if neutral
	int attack
	int max_health
	boolean collectible = true
	boolean is_a_secret = false
	List<String> druid_choices

    CardDefinition(Game game) {
        super(game)
    }

    CardDefinition() {
        super(null) // game is set after construction
    }

    void setType(String type) {
		assert type == "minion" || type == "weapon" || type == "spell"
		this.type = type
	}
	
	void setReserved_to(String hero_class) {
		assert hero_class in [ 'Druid', 'Hunter', 'Mage', 'Paladin', 'Priest', 'Rogue', 'Shaman', 'Warlock', 'Warrior' ]
		reserved_to = hero_class
	}

    String toString() {
		return "name='$name' type='$type' creature_type='$creature_type' cost=$cost attack=$attack max_health=$max_health text='$text' triggers=$triggers"
	}
		
}
