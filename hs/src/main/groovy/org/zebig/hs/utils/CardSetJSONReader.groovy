package org.zebig.hs.utils

import org.zebig.hs.game.CardLibrary
import org.zebig.hs.game.InvalidDefinitionException
import groovy.json.JsonSlurper

def f = new File('data/hs_cards.json')

def f_out = new File('data/hs_cards.txt')
f_out.delete()

def slurper = new JsonSlurper()
def result = slurper.parseText(f.text)

/*
 id: integer,
 name: string,
 description: string,
 image_url: string,
 hero: string
 - "druid"
 - "hunter"
 - "mage",
 - "paladin"
 - "priest"
 - "rogue"
 - "shaman"
 - "warlock"
 - "warrior",
 category: string
 - "minion"
 - "spell"
 - "secret"
 - "weapon",
 quality: string
 - "free"
 - "common"
 - "rare"
 - "epic"
 - "legendary",
 race: string
 - "none"
 - "beast"
 - "demon",
 set: string
 - "basic"
 - "expert"
 - "naxxramas",
 mana: integer,
 attack: integer,
 health: integer,
 collectible: boolean,
 effect_list: array
 - effect: string
 - "battlecry"
 - "charge"
 - "deathrattle"
 - "divine_shield"
 - "enrage"
 - "secret"
 - "stealth"
 - "taunt"
 - "windfury"
 - extra: string
 */

result.cards
		//.findAll { it.hero == "warrior" }
		.findAll { it.category != "hero" && it.category != "ability" }
		.findAll { it.set != "missions" && it.set != "promotion" }
		//.findAll { it.set != "naxxramas" }
		.sort { it.name }
		.each {
			try {
				def cd = CardLibrary.getCardDefinition(it.name)
				printf('  %-25s %s\n', it.name, it.description)
				assert it.mana == cd.cost
				if (it.category == 'minion') {
					assert (it.attack == cd.attack)||(it.attack == null && cd.attack == 0)
					assert it.health == cd.max_health
				}
				if (it.race != 'none' || cd.creature_type != null) {
					assert it.race == cd.creature_type
				}
				if (it.hero != "neutral") {
					assert cd.reserved_to.toLowerCase() == it.hero
				}
				if (cd.reserved_to == null) {
					assert it.hero == "neutral"
				}
				def s = it.description
						.replaceAll("<b>", "")
						.replaceAll("</b>", "")
						.replaceAll(/\.$/, "")
						.replaceAll(/\s+/,' ')
				assert s.toLowerCase() == cd.text.replaceAll(/\.$/, "").toLowerCase()
			}
			catch(InvalidDefinitionException e) {
				//printf('! %-25s %s %s\n', it.name, it.description, e)
			}
		}
