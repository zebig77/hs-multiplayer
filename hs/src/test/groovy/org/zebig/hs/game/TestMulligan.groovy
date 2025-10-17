package org.zebig.hs.game

import org.junit.Test
import org.zebig.hs.decks.GarroshDeck1
import org.zebig.hs.decks.MalfurionDeck1

import static org.junit.Assert.fail
import static org.zebig.hs.game.Game.Phase.*

class TestMulligan {

    Game g
    Player p1, p2

    void _initGame() {
        g = new Game(
                "Didier", GarroshHellscream.class, GarroshDeck1.class,
                "Aurélien", MalfurionStormrage.class, MalfurionDeck1.class)
        assert g.phase == WAITING_FOR_MULLIGAN
    }

    void _startGame() {
        g.start()
        p1 = g.active_player
        p2 = g.passive_player
    }

    void _doMulligan(List<String> p1_mulligan, List<String> p2_mulligan) {
        p1.doMulligan(p1_mulligan)
        p2.doMulligan(p2_mulligan)
        g.start_turn()
    }

    @Test
    void test0Card() {
        _initGame()
        _startGame()
        _doMulligan([], [])
        assert p1.mulligan_done
        assert p2.mulligan_done
        assert g.phase == IN_PLAY
    }

    @Test
    void test1CardP1() {
        _initGame()
        _startGame()
        _doMulligan([p1.hand.get(0).id as String],[])
        assert g.phase == IN_PLAY
    }


    @Test
    void testWrongCard() {
        _initGame()
        _startGame()
        try {
            _doMulligan(["1963484674"], [])
            fail "should have detected wrong card"
        }
        catch (AssertionError ignored) {
            // ok
        }
    }

    @Test
    void detectUnfinishedMulligan() {
        _initGame()
        _startGame()
        p1.doMulligan([])
        // should fail because p2 mulligan not yet done
        try {
            g.start_turn()
            fail "turn cannot start because p2 mulligan is not done"
        }
        catch (AssertionError ignored) {
            // OK
        }
    }
}
