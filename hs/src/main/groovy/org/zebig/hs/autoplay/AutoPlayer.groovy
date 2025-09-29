package org.zebig.hs.autoplay

import org.zebig.hs.game.Card
import org.zebig.hs.game.Game
import org.zebig.hs.game.GarroshHellscream
import org.zebig.hs.game.MalfurionStormrage
import org.zebig.hs.game.Player
import org.zebig.hs.decks.GarroshDeck1
import org.zebig.hs.decks.MalfurionDeck1

class AutoPlayer {

	static long seed = 1968

	Game g
	File autoplay_log

	AutoPlayer() {
		create_game()
		autoplay_log = new File("log/autoplayer/autoplayer_${seed}.log")
		autoplay_log.delete()

		log "Active player = ${g.active_player}"
		log "--> ${g.active_player.stats()}"
		log "Opponent = ${g.passive_player}"
		log "--> ${g.passive_player.stats()}\n"
	}

	def create_game() {
		g = new Game(seed)
		def p1 = new Player( g, 'Didier', new GarroshHellscream(), new GarroshDeck1() )
		def p2 = new Player( g, 'Titou', new MalfurionStormrage(), new MalfurionDeck1() )
		g.players = [p1, p2]
		g.start()
	}

	def log(String s) {
		autoplay_log << s + '\n'
	}

	def do_action(PlayerAction pa) {
		log "${g.active_player} $pa"
		if (pa.action == 'END_TURN') {
			g.next_turn()
		}
		else if (pa.action == 'PLAY_CARD') {
			g.active_player.next_choices = []
			if (pa.choice != null) {
				g.active_player.next_choices.add( pa.choice )
			}
			if (pa.target != null) {
				g.active_player.next_choices << pa.target				
			}
			Card c = pa.played
			if (c.is_a_minion()) {
				g.active_player.play(c, pa.position)
			}
			else {
				g.active_player.play(c)
			}
		}
		else if (pa.action == 'ATTACK') {
			Game.player_attacks(pa.played, pa.target)
		}
		else if (pa.action == 'USE_HERO_POWER') {
			if (pa.target != null) {
				g.active_player.next_choices = [pa.target]
			}
			g.active_player.use_hero_power()
		}
		log "--> ${g.active_player.stats()}"
		log "--> ${g.passive_player.stats()}\n"
	}

	def play() {
		int tries = 150
		while (!g.is_ended && (tries-- > 0) ) {
			List<PlayerAction> lpa = g.possible_actions()
			if (lpa == []) {
				log "NEXT_TURN\n"
				g.next_turn()
			}
			else {
				do_action( g.random_pick(lpa) )
			}
		}
		if (g.is_ended) {
			g.players.each { Player p ->
				if (p.hero.health <= 0) {
					log "${p.name} looses"
				}
				else {
					log "${p.name} wins"
				}
			}
		}
	}

	static main(args) {
		for(i in 0..999) {
			new AutoPlayer().play()
			seed++
		}
	}
}
