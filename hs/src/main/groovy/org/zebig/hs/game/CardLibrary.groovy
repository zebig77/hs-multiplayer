package org.zebig.hs.game

class CardLibrary {

    Game game
    Map<String, CardDefinition> cards = [:]

    CardLibrary(Game game) {
        this.game = game
    }

	static final BEASTS_NAMES = """\
Angry Chicken
Bloodfen Raptor
Boar
Captain's Parrot
Core Hound
Devilsaur
Dire Wolf Alpha
Emperor Cobra
Haunted Creeper
Hungry Crab
Ironbeak Owl
Ironfur Grizzly
Jungle Panther
King Krush
King Mukla
Leokk
Maexxna
Misha
Oasis Snapjaw
Panther
River Crocolisk
Savannah Highmane
Scavenging Hyena
Sheep
Silverback Patriarch
Squirrel
Stampeding Kodo
Starving Buzzard
Stonetusk Boar
Stranglethorn Tiger
The Beast
Timber Wolf
Tundra Rhino
Webspinner
Young Dragonhawk"""
	static List<String> beasts = null
	
	String random_beast_name() {
		if (beasts == null) {
			beasts = []
			CardLibrary.BEASTS_NAMES.eachLine { beasts << it }
		}
		return game.random_pick(beasts)
	}

	CardDefinition getCardDefinition(String card_name) {
		def class_name = card_name.replaceAll("'s", "s").replaceAll(/(\w)(\w*)/) { wholeMatch, initialLetter, restOfWord ->
			initialLetter.toUpperCase() + restOfWord
		}.replaceAll("[^a-zA-Z0-9]", "")
		CardDefinition cd = cards[class_name]
		if (cd == null) {
            Class cd_class
			try {
				cd_class = Class.forName("org.zebig.hs.cards.${class_name}")
			}
			catch (ClassNotFoundException e) {
				println e
				throw new InvalidDefinitionException("carte inconnue: $card_name")
			}
			try {
				cd = cd_class.getConstructor().newInstance() as CardDefinition
			}
			catch (ReflectiveOperationException e) {
				println e
				throw new InvalidDefinitionException("carte invalide: $card_name")
			}
            cd.game = game
			cards[class_name] = cd
		}
		return cd
	}

	Card new_card(String card_name) {
		return new Card(getCardDefinition(card_name))
	}
}
