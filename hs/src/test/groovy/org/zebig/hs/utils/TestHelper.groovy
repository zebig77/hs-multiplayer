package org.zebig.hs.utils

import org.zebig.hs.game.*

import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.rules.TestRule
import org.junit.rules.TestWatcher
import org.junit.runner.Description

import org.zebig.hs.state.*
import org.zebig.hs.decks.*

class TestHelper {
	
	Game g
	Player p1
	Player p2
	StateNode before_tree, after_tree

	@Before
	void newGame() {
		_create_game_Garrosh_vs_Malfurion() // default game
		before_tree = State.buildGameTree(g).clone()
        //g.begin_transaction()
	}
	
	@After
	void after() {
/*
		g.rollback_transaction()
		if (before_tree != null) { // clear before_tree to avoid the following test
			after_tree = State.buildGameTree(g).clone()
			StringBuilder log_sb = new StringBuilder()
			def same = before_tree.equalsTree(after_tree, log_sb)
			if (!same) {
				println "---> "+log_sb.toString()
				assert same
			}
		}
*/
	}
		
	@Rule
	public TestRule watcher = new TestWatcher() {
		protected void starting(Description description) {
			println """
=================================================
Starting test: ${description.getMethodName()}
=================================================
"""
		}
		protected void finished(Description description) {
			println """
=================================================
Finished test: ${description.getMethodName()}
=================================================
"""
//			Game.current.next_id = 1
//			Game.current.play_id = 1
		}

	}

	def _attack(attacker, attacked) {
		g.player_attacks(attacker, attacked)
	}

	def _create_game( String p1_name, Class p1_hero, Class p1_deck, String p2_name, Class p2_hero, Class p2_deck ) {
		g = new Game( p1_name, p1_hero, p1_deck, p2_name, p2_hero, p2_deck)
		g.start()
		p1 = g.active_player
		p2 = g.passive_player
	}
	
	def _create_game_Garrosh_vs_Malfurion() {
		_create_game( 
			"Didier", GarroshHellscream.class, GarroshDeck1.class,
			"Titou",  MalfurionStormrage.class, MalfurionDeck1.class)	
		}
	
	def _create_game_Uther_vs_Uther() {
		_create_game( 
			"Didier", UtherLightbringer.class, UtherDeck1.class,
			"Titou",  UtherLightbringer.class, UtherDeck1.class)	
		}
	
	def _create_game_Garrosh_vs_Jaina() {
		_create_game( 
			"Didier", GarroshHellscream.class, GarroshDeck1.class,
			"Titou", JainaProudmoore.class, JainaDeck1.class)	
		}

    void check(boolean check_result, String error_message) {
        if (check_result == false) {
            throw new IllegalActionException("check failed: $error_message")
        }
    }

	Player _get_player(String player_name) {
		def p = [p1, p2].find{ it.name = player_name }
		assert p != null
		return p
	}

	Player _get_player_with_hero(String hero_name) {
		def p = [p1, p2].find{ it.hero.name = hero_name }
		assert p != null
		return p
	}

	Card _play(String card_name, Player p = p1) {
		Card c = g.new_card(card_name)
		c.controller = p
		p.available_mana = c.get_cost()
		return p.play(c)
	}
	
	Card _play(String card_name, int place) {
		Card c = g.new_card(card_name)
		c.controller = p1
		p1.available_mana = c.get_cost()
		return p1.play(c, place)
	}
	
	Card _play(String card_name, Target target) {
		return _play_and_target( card_name, target )
	}

	/**
	 * creates card, add to hand, 
	 * set available mana as needed,
	 * set the target for battlecry effect and play the card
	 * @param card_name
	 * @return created card
	 */
	Card _play_and_target(String card_name, Target t) {
		p1.next_choices = [t]
		return _play( card_name )
	}

	def _set_active_player(Player p) {
		if (p != p1) {
			p2 = p1
			p1 = p
		}
	}
	
	def _next_random_int(int value) {
		g.random.forced_ints = [ value ]
	}
	
	def _next_turn() {
		g.next_turn()
		p1 = g.active_player
		p2 = g.passive_player
	}
	
	def _should_fail(String reason="", Closure c) {
		try {
			c.call()
			fail("Should fail: $reason")
		}
		catch( Exception e ) {
			assert e.toString().contains(reason)
			println "   - $e (ok)"
		}
	}
	
	def _use_hero_power(def choice) {
		p1.next_choices.add(choice)
		_use_hero_power()
	}
	
	def _use_hero_power() {
		p1.available_mana = p1.hero.power.cost
		p1.use_hero_power()
	}
	
}
