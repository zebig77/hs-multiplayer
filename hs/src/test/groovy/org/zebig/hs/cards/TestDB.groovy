package org.zebig.hs.cards

import groovy.json.JsonSlurper

import org.junit.Test
import org.zebig.hs.utils.TestHelper

class TestDB extends TestHelper {

	@Test
	void test() {
        def url = this.class.getResource("/data/hs_cards.json")
		def slurper = new JsonSlurper()
		def result = slurper.parseText(url.text)

		result.cards
        .findAll { it.category != "hero" && it.category != "ability" }
        .findAll { it.set != "missions" && it.set != "promotion" }
        .sort { it.name }
        .each {
            def cd = g.card_library.getCardDefinition(it.name)
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
            def db_s = it.description
                    .replaceAll("<b>", "")
                    .replaceAll("</b>", "")
                    .replaceAll(/\.$/, "")
                    .replaceAll(/\s+/,' ')
            def my_s = cd.text
            if (my_s == null) {
                my_s = ''
            }
            assert db_s.toLowerCase() == my_s.replaceAll(/\.$/, "").toLowerCase()
            if (cd.druid_choices != null) {
                assert cd.get_targets != null
            }
            it.effect_list.each { effect ->
                effect.each { k, v ->
                    if (k == "effect" && v == "battlecry") {
                        assert cd.text.contains("Battlecry: ")
                    }
                }
            }
        }
	}

	@Test
	void testBeasts() {
		g.card_library.BEASTS_NAMES.eachLine { g.card_library.getCardDefinition(it) }
	}
}
