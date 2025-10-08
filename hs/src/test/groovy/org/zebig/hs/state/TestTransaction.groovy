package org.zebig.hs.state

import org.junit.Test
import org.zebig.hs.decks.GarroshDeck1
import org.zebig.hs.decks.MalfurionDeck1
import org.zebig.hs.game.Card
import org.zebig.hs.game.Game
import org.zebig.hs.game.GarroshHellscream
import org.zebig.hs.game.MalfurionStormrage
import org.zebig.hs.game.Player

class TestTransaction {

    Game g
    Player p1
    Player p2

    Card _play(String card_name, Player p = p1) {
        Card c = g.new_card(card_name)
        c.controller = p
        p.available_mana = c.get_cost()
        return p.play(c)
    }

    void _initGame() {
        g = new Game(
                "Didier", GarroshHellscream.class, GarroshDeck1.class,
                "Aurélien", MalfurionStormrage.class, MalfurionDeck1.class)
    }

    @Test
    void testInitialState() {
        _initGame()
        assert g.transaction == null
        g.begin_transaction()
        assert g.transaction != null
        assert g.transaction.change_log.size() == 0
        g.start()
        p1 = g.active_player
        p2 = g.passive_player
        assert g.transaction.change_log.size() > 0
    }

    @Test
    void testPlayEffectOnTransaction() {
        _initGame()
        g.start()
        g.begin_transaction()
        p1 = g.active_player
        p2 = g.passive_player
        assert p1.available_mana == 1
        assert p1.board.size() == 0
        _play("Angry Chicken")
        assert p1.board.size() == 1
        assert g.transaction.change_log.size() > 0
        assert p1.available_mana == 0

        g.rollback_transaction()
        assert p1.board.size() == 0
        assert p1.available_mana == 1
    }

    @Test
    void testChangeStartGame() {
        _initGame()
        g.begin_transaction()
        g.start()
        def ch = g.transaction.game_changes["PlayerBecomesActive"]
        assert ch != null
        assert ch.properties["player_name"] == g.active_player.name
        def ch2 = g.transaction.game_changes["ManaStatusChanged"]
        assert ch2 != null
        assert ch2.properties == [player_name:g.active_player.name, max_mana:1, available_mana:1, overload:0]
        def lch3 = g.transaction.game_changes.findAll {name, properties ->
            name.startsWith("CardDrawn")
        }
        assert lch3.size() == 8
        assert lch3.findAll {name, change ->
            change.properties["player_name"] == g.active_player.name
        }.size() == 4
        assert lch3.findAll {name, change ->
            change.properties["player_name"] == g.passive_player.name
        }.size() == 4
    }
}
