package org.zebig.hs.game;

import org.zebig.hs.mechanics.events.ItComesInPlay
import org.zebig.hs.mechanics.events.ItIsDestroyed

import org.junit.Test

import org.zebig.hs.utils.TestHelper

class TestCard extends TestHelper {

	@Test
	public void Card_new() {
		Card c2 = _play('Timber Wolf')
		assert c2.get_attack() == 1
		assert c2.is_a_minion()
		assert c2.is_a_beast()
		Card c3 = _play("Abomination")
		assert c3.triggers.size() == 2
		assert c3.triggers[0].event_class == ItComesInPlay.class
		assert c3.triggers[1].event_class == ItIsDestroyed.class
	}
	
	@Test
	public void addText() {
		Card c = _play('Timber Wolf')
		assert c.text == 'Your other Beasts have +1 Attack.'
		c.addText("Deathrattle: Summon a 2/2 Treant") 
		assert c.text == 'Your other Beasts have +1 Attack. Deathrattle: Summon a 2/2 Treant'
		c.addText("Deathrattle: Summon a 2/2 Treant") 
		assert c.text == 'Your other Beasts have +1 Attack. Deathrattle: Summon a 2/2 Treant'
		c.text = ''
		c.addText("Deathrattle: Summon a 2/2 Treant")
		assert c.text == 'Deathrattle: Summon a 2/2 Treant'
		c.text = null
		c.addText("Deathrattle: Summon a 2/2 Treant")
		assert c.text == 'Deathrattle: Summon a 2/2 Treant'
	}

}
