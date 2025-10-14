package org.zebig.hs.state

import org.junit.Test
import org.zebig.hs.decks.GarroshDeck1
import org.zebig.hs.decks.MalfurionDeck1
import org.zebig.hs.game.Card
import org.zebig.hs.game.Game
import org.zebig.hs.game.GarroshHellscream
import org.zebig.hs.game.IllegalActionException
import org.zebig.hs.game.MalfurionStormrage
import org.zebig.hs.game.Player
import org.zebig.hs.game.Target

import static org.junit.Assert.fail
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

    void _attack(Target attacker, Target attacked) {
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

    Card _play_and_target(String card_name, Target t) {
        p1.next_choices = [t]
        return _play(card_name)
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
        def ch = lch.get(0)
        assert ch.target_id == p1.name
        assert ch.properties["player_name"] == p1.name

        def lch2 = g.transaction.findChanges(ManaChanged, p1.name)
        assert lch2.size() == 1
        def ch2 = lch2.get(0)
        assert ch2.target_id == p1.name
        assert ch2.properties == [player_name: p1.name, max_mana: "1", available_mana: "1", overload: "0"]

        def lch3 = g.transaction.findChanges(CardDrawn)
        assert lch3.size() == 8
        // fist player draws initially 3 + 1 when turn starts, passive player draws 4 + a coin (not a card draw)
        assert lch3.findAll { it.properties.player_name == p1.name }.size() == 4
        assert lch3.findAll { it.properties.player_name == p2.name }.size() == 4
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
        assert g.transaction.findChanges(HeroTakesDamage, "damage_amount", "2").size() == 1
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
        assert g.transaction.findChanges(MinionTakesDamage, "card_id", blu.id as String).size() == 1
        assert g.transaction.findChanges(MinionTakesDamage, "card_id", bbb.id as String).size() == 1
        assert g.transaction.findChanges(MinionDies, "card_id", blu.id as String).size() == 1
    }

    @Test
    void testZoneSizeChange() {
        /*
        hand <- + card drawn, - card played
        board <- + card played, - card dies
        deck <- - card drawn
         */
        _initGame()

        g.begin_transaction()

        assert g.transaction.findChanges(ZoneSizeChange).size() == 0
        _startGame()
        def lch0 = g.transaction.findChanges(ZoneSizeChange, "zone_name", "hand")
        assert lch0.size() == 2
        lch0.each {
            if (it.target_id == p1.name) {
                assert it.properties.new_size == 4
                assert it.is_public
            }
            if (it.target_id == p2.name) {
                assert it.properties.new_size == 5
                assert it.is_public
            }
        }

        g.end_transaction()
        g.begin_transaction()

        def blu = _play("BluegillWarrior")
        def lch = g.transaction.findChanges(ZoneSizeChange, "zone_name", "board")
        assert lch.size() == 1
        def ch = lch.get(0)
        assert ch.target_id == "board of $p1.name"

        _next_turn()

        def bbb = _play("BootyBayBodyguard")
        def lch2 = g.transaction.findChanges(ZoneSizeChange, "zone_name", "board")
        assert lch2.size() == 2 // one for each player
        lch2.each {
            if (it.target_id == p1.name) {
                assert it.properties.new_size == 1
                assert it.is_public
            }
            if (it.target_id == p2.name) {
                assert it.properties.new_size == 1
                assert it.is_public
            }
        }

        _next_turn()

        g.end_transaction()
        g.begin_transaction()

        _attack(blu, bbb) // expect p1 board size = 0 (blu died) and p2 board same size (no change)
        def lch3 = g.transaction.findChanges(ZoneSizeChange, "zone_name", "board")
        assert lch3.size() == 1
        def ch3 = lch3.get(0)
        assert ch3.target_id == "board of $p1.name"
        assert ch3.properties.new_size == "0"
    }

    @Test
    void testDeckZoneChange() {
        _initGame()
        _startGame()
        g.begin_transaction()
        assert g.transaction.findChanges(ZoneSizeChange, "zone_name", "deck").size() == 0
        def previous_deck_size = p1.deck.size()
        p1.draw(1)
        assert p1.deck.size() == previous_deck_size - 1
        assert g.transaction.findChanges(ZoneSizeChange).size() == 2 // +1 hand, -1 deck
        def lch = g.transaction.findChanges(ZoneSizeChange, "zone_name", "deck")
        assert lch.size() == 1
        def ch = lch.get(0)
        assert ch.properties.new_size == p1.deck.size() as String
    }

    @Test
    void testHeroDies() {
        _initGame()
        _startGame()
        def blu = _play("BluegillWarrior")
        p2.hero.health = 1
        g.begin_transaction()
        assert g.transaction.findChanges(HeroDies, p2.name).size() == 0
        _attack(blu, p2.hero)
        def lch = g.transaction.findChanges(HeroDies, p2.name)
        assert lch.size() == 1
        def ch = lch.get(0)
        assert ch.target_id == p2.name
    }

    @Test
    void testCardPlayedMinion() {
        _initGame()
        _startGame()
        g.begin_transaction()
        def blu = _play("BluegillWarrior")
        def lch = g.transaction.findChanges(CardPlayed, blu.id as String)
        assert lch.size() == 1
        def ch = lch.get(0)
        assert ch.target_id == blu.id as String
        assert ch.properties.player_name == p1.name
        assert ch.properties.card_id == blu.id as String
        assert ch.properties.position == '0'
        assert ch.properties.name == blu.name
        assert ch.properties.type == blu.type
        assert ch.properties.cost == blu.cost as String
        assert ch.properties.text == blu.text
        assert ch.properties.attack == blu.attack as String
        assert ch.properties.max_health == blu.max_health as String
    }

    @Test
    void testCardPlayedSpell() {
        _initGame()
        _startGame()
        g.begin_transaction()
        def arc = _play_and_target("Arcane Shot", p2.hero)
        def lch = g.transaction.findChanges(CardPlayed, arc.id as String)
        assert lch.size() == 1
        def ch = lch.get(0)
        assert ch.target_id == arc.id as String
        assert ch.properties.player_name == p1.name
        assert ch.properties.card_id == arc.id as String
        assert ch.properties.name == arc.name
        assert ch.properties.type == arc.type
        assert ch.properties.cost == arc.cost as String
        assert ch.properties.text == arc.text
    }

    @Test
    void testMinionHealedChange() {
        _initGame()
        _startGame()
        def bbb = _play("BootyBayBodyguard")
        _play_and_target("Arcane Shot", bbb)
        g.begin_transaction()
        _play("Circle of Healing") // Restore 4 Health to ALL minions.
        def lch = g.transaction.findChanges(MinionIsHealed, bbb.id as String)
        assert lch.size() == 1
        def ch = lch.get(0)
        assert ch.target_id == bbb.id as String
        assert ch.properties.player_name == p1.name
        assert ch.properties.card_id == bbb.id as String
        assert ch.properties.heal_amount == "2" // max_health - health before heal
        assert ch.properties.health == "4"
        assert ch.properties.max_health == "4"
        assert ch.is_public
    }

    @Test
    void testHeroHealedChange() {
        _initGame()
        _startGame()
        p1.hero.health = 10
        g.begin_transaction()
        _play_and_target("Drain Life", p2.hero)
        def lch = g.transaction.findChanges(HeroIsHealed, p1.name)
        assert lch.size() == 1
        def ch = lch.get(0)
        assert ch.target_id == p1.name
        assert ch.properties.player_name == p1.name
        assert ch.properties.heal_amount == "2" // max_health - health before heal
        assert ch.properties.health == "12"
        assert ch.properties.max_health == "30"
        assert ch.is_public
    }

    @Test
    void testMinionAttacksMinion() {
        _initGame()
        _startGame()
        def bbb = _play("BootyBayBodyguard")
        _next_turn()
        def blu = _play("BluegillWarrior")
        g.begin_transaction()
        _attack(blu, bbb)
        def lch = g.transaction.findChanges(MinionAttacksMinion, blu.id as String)
        assert lch.size() == 1
        def ch = lch.get(0)
        assert ch.target_id == blu.id as String
        assert ch.properties.player_name == blu.controller.name
        assert ch.properties.attacker_id == blu.id as String
        assert ch.properties.attacked_id == bbb.id as String
        assert ch.properties.attack_damage == "2"
        assert ch.is_public
    }

    @Test
    void testMinionAttacksHero() {
        _initGame()
        _startGame()
        def blu = _play("BluegillWarrior")
        g.begin_transaction()
        _attack(blu, p2.hero)
        def lch = g.transaction.findChanges(MinionAttacksHero, blu.id as String)
        assert lch.size() == 1
        def ch = lch.get(0)
        assert ch.target_id == blu.id as String
        assert ch.properties.player_name == blu.controller.name
        assert ch.properties.attacker_id == blu.id as String
        assert ch.properties.attacked_player_name == p2.name
        assert ch.properties.attack_damage == "2"
        assert ch.is_public
    }


    @Test
    void testHeroAttacksMinion() {
        _initGame()
        _startGame()
        def bbb = _play("BootyBayBodyguard")
        _next_turn()
        _play("Doomhammer")
        g.begin_transaction()
        _attack(g.active_player.hero as Target, bbb)
        def lch = g.transaction.findChanges(HeroAttacksMinion, g.active_player.name)
        assert lch.size() == 1
        def ch = lch.get(0)
        assert ch.target_id == g.active_player.name
        assert ch.properties.player_name == g.active_player.name
        assert ch.properties.attacked_id == bbb.id as String
        assert ch.properties.attack_damage == "2"
        assert ch.is_public
    }

    @Test
    void testHeroAttacksHero() {
        _initGame()
        _startGame()
        _play("Doomhammer")
        2.times {// Since Doomhammer has Windfury
            g.begin_transaction()
            _attack(p1.hero, p2.hero)
            def lch = g.transaction.findChanges(HeroAttacksHero, p1.name)
            assert lch.size() == 1
            def ch = lch.get(0)
            assert ch.target_id == p1.name
            assert ch.properties.player_name == p1.name
            assert ch.properties.attacked_player_name == p2.name
            assert ch.properties.attack_damage == "2"
            assert ch.is_public
            g.end_transaction()
        }
        try {
            g.begin_transaction()
            _attack(p1.hero, p2.hero) // should fail, has already attacked twice
            fail("should have detected an illegal action")
        }
        catch (IllegalActionException ignored) {
            // OK
            assert g.transaction.findChanges(HeroAttacksHero).size() == 0
            g.rollback_transaction()
        }
    }

    @Test
    void testWeaponEquipped() {
        _initGame()
        _startGame()
        g.begin_transaction()
        _play("Doomhammer")
        def w = p1.hero.weapon
        assert w != null
        def lch = g.transaction.findChanges(WeaponEquipped, p1.name)
        assert lch.size() == 1
        def ch = lch.get(0)
        assert ch.target_id == p1.name
        assert ch.properties.player_name == p1.name
        assert ch.properties.name == w.name
        assert ch.properties.text == w.text
        assert ch.properties.attack == w.attack as String
        assert ch.properties.durability == w.durability as String
        assert ch.is_public
    }

    @Test
    void testWeaponDestroyed() {
        _initGame()
        _startGame()
        _play("Doomhammer")
        def w = p1.hero.weapon
        assert w != null
        w.durability = 1
        g.begin_transaction()
        _attack(p1.hero, p2.hero) // should break weapon
        def lch = g.transaction.findChanges(WeaponDestroyed, p1.name)
        assert lch.size() == 1
        def ch = lch.get(0)
        assert ch.target_id == p1.name
        assert ch.properties.player_name == p1.name
        assert ch.properties.weapon_name == "Doomhammer"
        assert ch.is_public
    }

    @Test
    void testUsePowerChange() {
        _initGame()
        _startGame()
        while (p1.hero.name != "Garrosh Hellscream") {
            _next_turn()
        }
        p1.max_mana = 2
        p1.available_mana = 2
        g.begin_transaction()
        p1.use_hero_power()
        def lch = g.transaction.findChanges(HeroPowerUsed, p1.name)
        assert lch.size() == 1
        def ch = lch.get(0)
        assert ch.target_id == p1.name
        assert ch.properties.player_name == p1.name
        assert ch.is_public
    }

    @Test
    void testArmorChange() {
        _initGame()
        _startGame()
        while (p1.hero.name != "Garrosh Hellscream") {
            _next_turn()
        }
        p1.max_mana = 2
        p1.available_mana = 2
        g.begin_transaction()
        p1.use_hero_power()
        def lch = g.transaction.findChanges(HeroArmorChanged, p1.name)
        assert lch.size() == 1
        def ch = lch.get(0)
        assert ch.target_id == p1.name
        assert ch.properties.player_name == p1.name
        assert ch.properties.armor == "2"
        assert ch.is_public
    }

    @Test
    void testSecretPlayedChange() {
        _initGame()
        _startGame()
        g.begin_transaction()
        def cns = _play("Counterspell")
        // secrets should NOT be seen as a CardPlayed events (public)
        assert g.transaction.findChanges(CardPlayed, cns.id as String).size() == 0
        def lch = g.transaction.findChanges(SecretPlayed, cns.id as String)
        assert lch.size() == 1
        def ch = lch.get(0)
        assert ch.target_id == cns.id as String
        assert ch.properties.player_name == p1.name
        assert !ch.is_public

        def lch2 = g.transaction.findChanges(ZoneSizeChange, "zone_name", "secrets")
        assert lch2.size() == 1
        def ch2 = lch2.get(0)
        assert ch2.target_id == "secrets of $p1.name"
        assert ch2.properties.player_name == p1.name
        assert ch2.properties.new_size == "1"
    }

    @Test
    void testSecretPlayedRevealed() {
        _initGame()
        _startGame()
        def cns = _play("Counterspell")
        _next_turn()
        g.begin_transaction()
        _play_and_target("Arcane Shot", p2.hero) // should be countered
        assert p2.hero.health == 30
        assert p2.secrets.size() == 0
        def lch = g.transaction.findChanges(SecretRevealed, cns.id as String)
        assert lch.size() == 1
        def ch = lch.get(0)
        assert ch.target_id == cns.id as String
        assert ch.properties.player_name == cns.controller.name
        assert ch.properties.text == cns.text
        assert ch.is_public
    }

    @Test
    void testHeroFrozenChange() {
        _initGame()
        _startGame()
        g.begin_transaction()
        def fro = _play_and_target("Frostbolt", p2.hero)
        assert p2.hero.is_frozen()
        def lch = g.transaction.findChanges(HeroFrozen, p2.name)
        assert lch.size() == 1
        def ch = lch.get(0)
        assert ch.target_id == p2.name
        assert ch.properties.player_name == p2.name
        assert ch.is_public
    }

    @Test
    void testMinionFrozenChange() {
        _initGame()
        _startGame()
        def bbb = _play("BootyBayBodyguard")
        _next_turn()
        g.begin_transaction()
        def fro = _play_and_target("Frostbolt", bbb)
        assert !bbb.is_dead()
        assert bbb.is_frozen()
        def lch = g.transaction.findChanges(MinionFrozen)
        assert lch.size() == 1
        def ch = lch.get(0)
        assert ch.target_id == bbb.id as String
        assert ch.properties.player_name == bbb.controller.name
        assert ch.is_public
    }
}