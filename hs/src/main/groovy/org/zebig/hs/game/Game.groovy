package org.zebig.hs.game

import org.zebig.hs.autoplay.PlayerAction
import org.zebig.hs.logger.Log
import org.zebig.hs.mechanics.events.*
import org.zebig.hs.state.Transaction
import org.zebig.hs.utils.RandomGenerator

import java.beans.PropertyChangeEvent

import static org.zebig.hs.mechanics.buffs.BuffType.*
import static org.zebig.hs.state.GameChange.Type.*

class Game {

    RandomGenerator random
    CardLibrary card_library
    Transaction transaction

    // Unique identifier for this match instance
    final String gameId = UUID.randomUUID().toString()
    private long seq = 0L

    def state = [:] as ObservableMap
    GameObject scope // game-wide events are attached to this scope
    List<Player> players = []
    Stack<Event> events = new Stack<Event>()

    Game(long random_seed = System.currentTimeMillis()) {
        scope = new GameObject(this)
        card_library = new CardLibrary(this)
        random = new RandomGenerator(random_seed)
        next_id = 100 // 1-99 reserved for heroes and deck cards
        play_id = 1
        is_started = false
        is_ended = false
        turn_timeout = 90
        feugen_died = false
        stalagg_died = false
        state.addPropertyChangeListener {
            process_state_change(it)
        }
    }

    void process_state_change(PropertyChangeEvent event) {
        transaction?.process_state_change(state, event)
    }

    Game(String p1_name, Class p1_hero, Class p1_deck, String p2_name, Class p2_hero, Class p2_deck) {
        this()
        def p1 = new Player(this, p1_name)
        def p2 = new Player(this, p2_name)

        Deck deck1 = p1_deck.getConstructor(Player.class).newInstance(p1) as Deck
        Deck deck2 = p2_deck.getConstructor(Player.class).newInstance(p2) as Deck
        Hero hero1 = p1_hero.getConstructor(Player.class).newInstance(p1) as Hero
        Hero hero2 = p2_hero.getConstructor(Player.class).newInstance(p1) as Hero

        p1.deck = deck1
        p1.hero = hero1
        p2.deck = deck2
        p2.hero = hero2

        players.add(p1)
        players.add(p2)
    }

    long nextSeq() {
        return ++seq
    }

    void begin_transaction() {
        Log.info "\n*** BEGIN TRANSACTION ***"
        if (transaction == null) {
            transaction = new Transaction()
        }
    }

    void end_transaction() {
        Log.info "\n*** END TRANSACTION ***"
        if (transaction != null) {
            transaction = null
        }
    }

    void rollback_transaction() {
        Log.info "\n*** ROLLBACK TRANSACTION ***"
        if (transaction == null) {
            return
        }
        assert !transaction.in_rollback
        transaction.in_rollback = true
        while (!transaction.change_log.isEmpty()) {
            transaction.change_log.pop().undo()
        }
        transaction = null
    }

    boolean getFeugen_died() { state.feugen_died }

    void setFeugen_died(boolean b) { state.feugen_died = b }

    boolean getStalagg_died() { state.stalagg_died }

    void setStalagg_died(boolean b) { state.stalagg_died = b }

    int getNext_id() { state.next_id }

    void setNext_id(int ni) { state.next_id = ni }

    int getPlay_id() { state.play_id }

    void setPlay_id(int pi) { state.play_id = pi }

    boolean getIs_started() { state.is_started }

    void setIs_started(boolean is) { state.is_started = is }

    boolean getIs_ended() { state.is_ended }

    void setIs_ended(boolean ie) { state.is_ended = ie }

    int getTurn_timeout() { state.turn_timeout }

    void setTurn_timeout(int tt) {
        state.turn_timeout = tt
    }

    Player getActive_player() { state.active_player }

    void setActive_player(Player p) {
        state.active_player = p
        transaction?.record(PlayerBecomesActive, p.name, ["player_name":p.name])
    }

    Player getPassive_player() { state.passive_player }

    void setPassive_player(Player p) { state.passive_player = p }

    void check_end_of_game() {
        if (!players[0].hero.is_dead() && !players[1].hero.is_dead()) {
            return // not ended
        }
        is_ended = true
        Log.info ""
        def win_message
        if (players[0].hero.is_dead() && players[1].hero.is_dead()) {
            win_message = "!!! both heroes are dead: it is a draw !!!"
        } else if (players[1].hero.is_dead()) {
            win_message = "!!! ${players[0]} wins !!!"
        } else {
            win_message = "!!! ${players[1]} wins !!!"
        }
        Log.info '!' * win_message.size()
        Log.info win_message
        Log.info '!' * win_message.size()
        end_game()
    }

    static void end_game() {
        Log.info "---- end of game"
    }

    void end_turn() {
        Log.info "\n - $active_player ends its turn"

        // kill minions scheduled to die at end of turn
        new ItsControllerTurnEnds(active_player).check()
        new AnyTurnEnds(this).check()
        active_player.minions().findAll { it.has_buff(DIE_AT_THE_END_OF_TURN) }.each {
            Log.info "   - executing '${DIE_AT_THE_END_OF_TURN}' for $it"
            (it as Card).dies()
        }
        // remove freeze buff for minions who have not attacked
        List<Target> to_check = active_player.minions()
        to_check.add active_player.hero
        to_check.findAll { it.is_frozen() }.each { Target t ->
            if (t.attack_counter == 0) {
                t.remove_all_buff(FROZEN)
            }
        }
        // swap active and opponent
        def x = active_player
        active_player = passive_player
        passive_player = x
    }

    int get_random_int(int bound) {
        return random.get_random_int(bound)
    }

    Player opponent_of(Player p) {
        return p == active_player ? passive_player : active_player
    }

    Event getCurrent_event() {
        if (events.isEmpty()) {
            throw new InvalidDefinitionException("no current event")
        }
        return events.peek()
    }

    void start_turn() {
        Log.info "\n---- ${active_player}'s turn begins"

        turn_timeout = 90 // default timeout, can be changed by Nozdormu
        new AnyTurnStarts(this).check()

        active_player.init_turn()
        new ItsControllerTurnStarts(active_player).check()
        active_player.draw(1)

        // check for corruption: "Choose an enemy minion. At the start of your turn, destroy it"
        passive_player.minions().each {
            if (it.has_buff(CORRUPTION)) {
                Log.info "      . corruption kills $it"
                it.dies()
            }
        }
        // check for "destroy all minions at the start of your turn"
        active_player.minions().each {
            if (it.has_buff(DESTROY_ALL_MINIONS_AT_START_OF_TURN)) {
                Log.info "   - destroying all minions"
                active_player.minions()*.dies()
                passive_player.minions()*.dies()
            }
        }
    }

    void remove_dead_from_battlefield() {
        active_player.minions().each {
            if (it.get_health() <= 0) {
                it.dies()
            }
        }
        passive_player.minions().each {
            if (it.get_health() <= 0) {
                it.dies()
            }
        }
    }

    // transform a card into another one
    void transform(Card c, String new_card_name) {
        def new_c = new_card(new_card_name)
        Log.info "      . transforming $c into a '$new_card_name'"
        c.card_definition = new_c.card_definition
        c.init()
        new ItComesInPlay(c).check()
    }

    void player_attacks(Target attacker, Target attacked) {
        if (attacker.controller == attacked.controller) {
            throw new IllegalActionException("$attacker and $attacked have the same controller")
        }
        Log.info "\n- ${active_player} orders $attacker to attack $attacked"

        attacker.check_can_attack()
        attacked.check_can_be_attacked()
        /*
         * if a defender has taunt, it must be attacked
         */
        def l = passive_player.minions().findAll { it.has_buff(TAUNT) }
        if (!l.isEmpty() && !l.contains(attacked)) {
            throw new IllegalActionException("you must attack a minion with taunt")
        }
        // if attack has stealth, remove it
        if (attacker.has_buff(STEALTH)) {
            attacker.remove_all_buff(STEALTH)
        }

        // attacker and defender deals their damage at the same time
        Log.info "   - fight begins between $attacker and $attacked"
        Log.info "      . $attacker is ${attacker.get_attack()}/${attacker.get_health()}"
        Log.info "      . $attacked is ${attacked.get_attack()}/${attacked.get_health()}"

        attacker.attack_counter++
        attacker.is_attacking = true

        // check what happens when someone attacks
        new ItAttacks(attacker).check()
        AnyCharacterAttacks e = new AnyCharacterAttacks(attacker, attacked)
        e.check()
        if (attacker.is_a_minion() && !attacker.is_in_play()) {
            Log.info "      . $attacker is no longer on the battlefield -> attack cancelled "
            return
        }
        if (e.changed_attacked != null) {
            Log.info "     . $attacked is replaced by $e.changed_attacked"
            attacked = e.changed_attacked
        }

        // check what happens when someone is attacked
        new ItIsAttacked(attacked).check()

        // damage is dealt and taken at the same time for the attacker and the attacked
        // triggered events are checked later

        // check if attacked takes damage and compute how much
        def attacker_damage = attacker.get_attack()
        def attacked_health_loss = 0
        if (attacker_damage > 0) {
            if (!attacked.has_buff(IMMUNE)) {
                if (attacked.has_buff(DIVINE_SHIELD)) {
                    Log.info "      . $attacked loses its divine shield and takes no damage"
                    attacked.remove_all_buff(DIVINE_SHIELD)
                } else { // no divine shield
                    attacked_health_loss = attacked.receive_combat_damage(attacker_damage)
                }
            } else { // immune
                Log.info "      . $attacked receives no damage because it is immune"
            }
        }

        // check if attacker takes damage and compute how much
        def attacked_damage = attacked.get_attack()
        def attacker_health_loss = 0
        if (attacked_damage > 0) {
            if (!attacker.has_buff(IMMUNE)) {
                if (attacker.has_buff(DIVINE_SHIELD)) {
                    Log.info "      . $attacker loses its divine shield and takes no damage"
                    attacker.remove_all_buff(DIVINE_SHIELD)
                } else { // no divine shield
                    attacker_health_loss = attacker.receive_combat_damage(attacked_damage)
                }
            } else { // immune
                Log.info "      . $attacker receives no damage because it is immune"
            }
        }

        // check enrage status
        attacker.check_enrage_status()
        attacked.check_enrage_status()

        // attacker 'deal damage' events
        if (attacked_health_loss > 0) {
            new ItDealsDamage(attacker, attacked).check()
            if (attacker.is_a_minion()) {
                new AnyMinionDealsDamage(attacker, attacked).check()
            }
        }

        // attacked 'deal damage' events
        if (attacker_health_loss > 0) {
            new ItDealsDamage(attacked, attacker).check()
            if (attacked.is_a_minion()) {
                new AnyMinionDealsDamage(attacked, attacker).check()
            }
        }

        // attacked 'take damage' events
        if (attacked_health_loss > 0) {
            Log.info "      . checking 'take damage' events for $attacked"
            if (attacked.is_a_minion()) {
                new AnyMinionTakesDamage(attacked as Card).check()
            }
            if (attacked.is_a_hero()) {
                new AnyHeroTakesDamage(attacked as Hero, attacked_health_loss).check()
            }
            new ItTakesDamage(attacked).check()
        }

        // attacker 'take damage' events
        if (attacker_health_loss > 0) {
            Log.info "      . checking 'take damage' events for $attacker"
            if (attacker.is_a_minion()) {
                new AnyMinionTakesDamage(attacker as Card).check()
            }
            if (attacker.is_a_hero()) {
                new AnyHeroTakesDamage(attacker as Hero, attacker_health_loss).check()
            }
            new ItTakesDamage(attacker).check()
        }

        attacker.is_attacking = false

        // check if attacker dies
        if (attacker.get_health() <= 0 && !attacker.is_destroyed) {
            attacker.dies()
        }

        // check if attacked dies
        if (attacked.get_health() <= 0 && !attacked.is_destroyed) {
            attacked.dies()
        }

        // reduce weapon durability if player has attacked with hero / weapon
        if (attacker.is_a_hero()) {
            Hero hero = attacker as Hero
            if (hero.weapon != null) {
                ItsDurabilityIsReduced e2 = new ItsDurabilityIsReduced(hero.weapon, attacked)
                e2.check()
                if (!e2.stop_action) {
                    hero.weapon.add_durability(-1)
                }
            }
        }

        check_end_of_game()
    }

    Card summon(Player p, Card c, int place) {
        if (place >= 7) {
            // no room for it
            Log.info "      . $c is destroyed because there is no place in battlefield"
            c.dies()
        }
        Log.info "      . $c is put in play for $p at x=$place"

        // set the play order
        c.play_order = play_id++

        // set the controller and place in battlefield
        p.controls(c, place)

        // signal the minion as summoned
        c.just_summoned = true
        c.is_destroyed = false
        new AnyMinionIsSummoned(c).check()

        // when_coming_in_play trigger
        if (!c.being_copied) {
            // whether played from hand or summoned by effect but not copied
            new ItComesInPlay(c).check()
        } else {
            c.being_copied = false // done
        }

        return c
    }

    def possible_actions() {
        List<PlayerAction> pa = []
        // playable cards
        active_player.hand.cards.each { Card c ->
            pa += PlayerAction.possiblePlayCardActions(c)
        }
        // minion attacks
        active_player.minions().each { Card c ->
            pa += PlayerAction.possibleAttackActions(c)
        }
        // hero attack
        pa += PlayerAction.possibleAttackActions(active_player.hero)
        // use power
        pa += PlayerAction.possibleUsePowerActions(active_player.hero)
        return pa
    }

    Card summon(Player p, Card c) {
        if (c == null) {
            return null
        }
        return summon(p, c, p.board.size())
    }

    Card summon_copy(Player p, Card c) {
        def copy = c.get_copy()
        return summon(p, copy)
    }

    Card summon(Player p, String card_name) {
        if (card_name == null) {
            return null
        }
        return summon(p, new_card(card_name))
    }

    Card summon(Player p, String card_name, int place) {
        if (card_name == null) {
            return null
        }
        return summon(p, new_card(card_name))
    }

    def next_turn() {
        end_turn()
        start_turn()
    }

    Card new_card(String card_name) {
        return card_library.new_card(card_name)
    }

    void start() {
        players.each { Player p ->
            p.deck.cards.shuffle()
            p.max_mana = 0
            p.overload = 0
            p.available_mana = 0
        }
        active_player = players[random.get_random_int(2)]
        passive_player = players.find { it != active_player }
        Log.info """\
---- Starting game, 
- $active_player plays first with ${active_player.hero}
- $passive_player plays second with ${passive_player.hero}"""
        active_player.draw(3)
        passive_player.draw(4)
        // TODO: each player can discard any number of cards and have them replaced
        passive_player.hand.add(new_card("The Coin"))
        is_started = true
        start_turn()
    }

    // single pick
    Object random_pick(Collection choices) {
        if (choices.size() == 0) {
            return null
        }
        if (choices.size() == 1) {
            return choices[0]
        }
        return choices[random.get_random_int(choices.size())]
    }

    // multiple picks
    List random_pick(int amount, List choices) {
        def result = []
        amount.times {
            result += random_pick(choices - result)
        }
        return result
    }

    String toString() { "Game[$gameId" }

}
