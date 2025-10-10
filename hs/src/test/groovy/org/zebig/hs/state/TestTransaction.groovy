package org.zebig.hs.state

import org.junit.Test
import org.zebig.hs.decks.GarroshDeck1
import org.zebig.hs.decks.MalfurionDeck1
import org.zebig.hs.game.Card
import org.zebig.hs.game.Game
import org.zebig.hs.game.GarroshHellscream
import org.zebig.hs.game.MalfurionStormrage
import org.zebig.hs.game.Player

import static org.zebig.hs.state.GameChange.Type.*

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

    void _attack(attacker, attacked) {
        g.player_attacks(attacker, attacked)
    }

    void _initGame() {
        g = new Game(
                "Didier", GarroshHellscream.class, GarroshDeck1.class,
                "Aurélien", MalfurionStormrage.class, MalfurionDeck1.class)
    }

    void _startGame() {
        g.start()
        p1 = g.active_player
        p2 = g.passive_player
    }

    def _next_turn() {
        g.next_turn()
        p1 = g.active_player
        p2 = g.passive_player
    }

    @Test
    void testInitialState() {
        _initGame()
        assert g.transaction == null
        g.begin_transaction()
        assert g.transaction != null
        assert g.transaction.change_log.size() == 0
        _startGame()
        assert g.transaction.change_log.size() > 0
    }

    @Test
    void testPlayEffectOnTransaction() {
        _initGame()
        _startGame()
        g.begin_transaction()
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
        _startGame()

        def lch = g.transaction.findChanges(PlayerBecomesActive)
        assert lch.size() == 1
        def ch = lch.first
        assert ch.target_id == p1.name
        assert ch.properties["player_name"] == p1.name

        def lch2 = g.transaction.findChanges(ManaStatusChanged, p1.name)
        assert lch2.size() == 1
        def ch2 = lch2.first
        assert ch2.target_id ==  p1.name
        assert ch2.properties == [player_name:p1.name, max_mana:1, available_mana:1, overload:0]

        def lch3 = g.transaction.findChanges(CardDrawn)
        assert lch3.size() == 8 // fist player draws initially 3 + 1 when turn starts, passive player draws 4 + a coin (not a card draw)
        assert lch3.findAll {it.properties.player_name == p1.name}.size() == 4
        assert lch3.findAll {it.properties.player_name == p2.name}.size() == 4
    }

    @Test
    void testHeroTakesDamage() {
        _initGame()
        _startGame()
        g.begin_transaction()
        def blu = _play("BluegillWarrior")
        assert g.transaction.findChanges(HeroTakesDamage, p2.name).size() == 0
        _attack(blu, p2.hero)
        assert g.transaction.findChanges(HeroTakesDamage, p2.name).size() == 1
    }

    @Test
    void testMinionsTakesDamage() {
        _initGame()
        _startGame()
        def blu = _play("BluegillWarrior")
        _next_turn()
        def bbb = _play("BootyBayBodyguard")
        _next_turn()
        g.begin_transaction()
        assert g.transaction.findChanges(MinionTakesDamage).size() == 0
        _attack(blu, bbb)
        assert g.transaction.findChanges(MinionTakesDamage).size() == 2
        assert g.transaction.findChanges(MinionTakesDamage,"card_id", blu.id as String).size() == 1
        assert g.transaction.findChanges(MinionTakesDamage,"card_id", bbb.id as String).size() == 1
    }
}
