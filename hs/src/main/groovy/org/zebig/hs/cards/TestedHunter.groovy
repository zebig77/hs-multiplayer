package org.zebig.hs.cards

import org.zebig.hs.game.Card
import org.zebig.hs.game.CardDefinition
import org.zebig.hs.game.CardLibrary
import org.zebig.hs.game.Game
import org.zebig.hs.game.Target
import org.zebig.hs.logger.Log
import org.zebig.hs.mechanics.buffs.BuffType

import static org.zebig.hs.mechanics.buffs.BuffType.*

class AnimalCompanion extends CardDefinition {
    AnimalCompanion(Game game) {
        super(game)
        name = 'Animal Companion'; type = 'spell'; cost = 3
        text = 'Summon a random Beast Companion.'
        reserved_to = "Hunter"
        when_played(text) {
            def card_name = random_pick(["Misha", "Leokk", "Huffer"])
            game.summon(you, card_name)
        }
    }
}

class ArcaneShot extends CardDefinition {
    ArcaneShot(Game game) {
        super(game)
        name = 'Arcane Shot'; type = 'spell'; cost = 1
        text = 'Deal 2 damage.'
        reserved_to = "Hunter"
        get_targets = [{ all_targets - your_hero }]
        when_played(text) {
            this_spell.deal_spell_damage(2, select_spell_target(all_targets - your_hero))
        }
    }
}

class BestialWrath extends CardDefinition {
    BestialWrath(Game game) {
        super(game)
        name = 'Bestial Wrath'; type = 'spell'; cost = 1
        text = 'Give a Beast +2 Attack and Immune this turn.'
        reserved_to = "Hunter"
        get_targets = [{ all_minions.findAll { it.creature_type == 'beast' } }]
        when_played(text) {
            def b = select_spell_target(all_minions.findAll { it.creature_type == 'beast' } as List<Target>)
            b.gains("+2 Attack").until_end_of_turn()
            b.gains(IMMUNE).until_end_of_turn()
        }
    }
}

class DeadlyShot extends CardDefinition {
    DeadlyShot(Game game) {
        super(game)
        name = 'Deadly Shot'; type = 'spell'; cost = 3
        text = 'Destroy a random enemy minion.'
        reserved_to = "Hunter"
        when_played(text) {
            this_spell.destroy(random_pick(enemy_minions))
        }
    }
}

class EaglehornBow extends CardDefinition {
    EaglehornBow(Game game) {
        super(game)
        name = 'Eaglehorn Bow'; type = 'weapon'; cost = 3; attack = 3; max_health = 2
        text = 'Whenever a friendly Secret is revealed, gain +1 Durability.'
        reserved_to = "Hunter"
        when_coming_in_play(text) {
            def w = this_weapon
            w.when_a_secret_is_revealed(text) {
                if (that_secret.controller == w.controller) {
                    w.durability += 1
                }
            }
        }
    }
}

class ExplosiveShot extends CardDefinition {
    ExplosiveShot(Game game) {
        super(game)
        name = 'Explosive Shot'; type = 'spell'; cost = 5
        text = 'Deal 5 damage to a minion and 2 damage to adjacent ones.'
        reserved_to = "Hunter"
        get_targets = [{ all_minion_targets }]
        when_played(text) {
            Card m = select_spell_target(all_minion_targets)
            this_spell.deal_spell_damage(5, m)
            this_spell.deal_spell_damage(2, m.neighbors())
        }
    }
}

class ExplosiveTrap extends CardDefinition {
    ExplosiveTrap(Game game) {
        super(game)
        name = 'Explosive Trap'; type = 'spell'; cost = 2
        text = 'Secret: When your hero is attacked, deal 2 damage to all enemies.'
        reserved_to = "Hunter"
        is_a_secret = true
        when_played("create $text") {
            def the_secret = you.create_secret(this_spell)
            this_spell.when_a_character_attacks("check $text") {
                the_secret.activate_if(attacked == this_spell.controller.hero) {
                    def all_enemies = your_minions + your_hero // active player's
                    this_spell.deal_damage(2, all_enemies)
                }
            }
        }
    }
}

class Flare extends CardDefinition {
    Flare(Game game) {
        super(game)
        name = 'Flare'; type = 'spell'; cost = 1
        text = 'All minions lose Stealth. Destroy all enemy Secrets. Draw a card.'
        reserved_to = "Hunter"
        when_played(text) {
            all_minions*.remove_all_buff(STEALTH)
            opponent.secrets.clear()
            you.draw(1)
        }
    }
}

class FreezingTrap extends CardDefinition {
    FreezingTrap(Game game) {
        super(game)
        name = 'Freezing Trap'; type = 'spell'; cost = 2
        text = "Secret: When an enemy minion attacks, return it to its owner's hand and it costs (2) more."
        reserved_to = "Hunter"
        is_a_secret = true
        when_played("create $text") {
            def the_secret = you.create_secret(this_spell)
            the_secret.when_a_character_attacks("check $text") {
                the_secret.activate_if(
                        attacker.is_a_minion() &&
                                attacker.controller != the_secret.controller)
                        {
                            (attacker as Card).return_to_hand()
                            attacker.gains("costs (2) more")
                        }
            }
        }
    }
}

class GladiatorsLongbow extends CardDefinition {
    GladiatorsLongbow(Game game) {
        super(game)
        name = "Gladiator's Longbow"; type = 'weapon'; cost = 7; attack = 5; max_health = 2
        text = 'Your hero is Immune while attacking.'
        reserved_to = "Hunter"
        when_played(text) {
            def glb = your_hero.weapon
            glb.when_a_buff_is_evaluated(text) {
                if (that_target == glb.controller.hero && that_buff_type == IMMUNE) {
                    if (that_target.is_attacking) {
                        has_buff = true
                        stop_action = true
                    }
                }
            }
        }
    }
}

class Houndmaster extends CardDefinition {
    Houndmaster(Game game) {
        super(game)
        name = 'Houndmaster'; type = 'minion'; cost = 4; attack = 4; max_health = 3
        text = 'Battlecry: Give a friendly Beast +2/+2 and Taunt.'
        reserved_to = "Hunter"
        get_targets = [{ your_minion_targets.findAll { it.is_a_beast() } }]
        when_played(text) {
            def possible_targets = your_minion_targets.findAll { it.is_a_beast() }
            if (!possible_targets.isEmpty()) {
                def to_buff = select_target(possible_targets)
                to_buff.gains("+2/+2")
                to_buff.gains(TAUNT)
            }
        }
    }
}

class Huffer extends CardDefinition {
    Huffer(Game game) {
        super(game)
        name = 'Huffer'; type = 'minion'; creature_type = 'beast'; cost = 3; attack = 4; max_health = 2
        text = 'charge.'
        collectible = false
        reserved_to = "Hunter"
        when_coming_in_play(text) {
            this_minion.gains(CHARGE)
        }
    }
}

class HuntersMark extends CardDefinition {
    HuntersMark(Game game) {
        super(game)
        name = "Hunter's Mark"; type = 'spell'; cost = 0
        text = "Change a minion's health to 1."
        reserved_to = "Hunter"
        get_targets = [{ all_minion_targets }]
        when_played(text) {
            def m = select_spell_target(all_minion_targets)
            m.gains("change health to 1")
        }
    }
}

class Hyena extends CardDefinition {
    Hyena(Game game) {
        super(game)
        name = 'Hyena'; type = 'minion'; creature_type = 'beast'; cost = 2; attack = 2; max_health = 2
        reserved_to = "Hunter"
        collectible = false
    }
}

class KillCommand extends CardDefinition {
    KillCommand(Game game) {
        super(game)
        name = 'Kill Command'; type = 'spell'; cost = 3
        text = 'Deal 3 damage. If you have a Beast, deal 5 damage instead.'
        reserved_to = "Hunter"
        get_targets = [{ all_targets }]
        when_played(text) {
            if (your_minions.find { it.is_a_beast() } != null) {
                this_spell.deal_spell_damage(5, select_spell_target(all_targets))
            } else {
                this_spell.deal_spell_damage(3, select_spell_target(all_targets))
            }
        }
    }
}

class KingKrush extends CardDefinition {
    KingKrush(Game game) {
        super(game)
        name = 'King Krush'; type = 'minion'; creature_type = 'beast'; cost = 9; attack = 8; max_health = 8
        text = 'Charge'
        reserved_to = "Hunter"
        when_coming_in_play(text) { this_minion.gains(CHARGE) }
    }
}

class Leokk extends CardDefinition {
    Leokk(Game game) {
        super(game)
        name = 'Leokk'; type = 'minion'; creature_type = 'beast'; cost = 3; attack = 2; max_health = 4
        text = 'Other friendly minions have +1 Attack.'
        collectible = false
        reserved_to = "Hunter"
        when_coming_in_play(text) {
            def leokk = this_minion
            leokk.when_attack_is_evaluated(text) {
                if (that_target.is_a_minion() &&
                        that_target.controller == leokk.controller &&
                        that_target != leokk) {
                    attack_increase += 1
                    Log.info "   - Leokk attack_increase=$attack_increase"
                }
            }
        }
    }
}

class Misdirection extends CardDefinition {
    Misdirection(Game game) {
        super(game)
        name = 'Misdirection'; type = 'spell'; cost = 2
        text = 'Secret: When a character attacks your hero, instead he attacks another random character.'
        reserved_to = "Hunter"
        is_a_secret = true
        when_played("create $text") {
            def the_secret = you.create_secret(this_spell)
            the_secret.when_a_character_attacks("check $text") {
                def possible_targets = all_minions + attacker.controller.hero - attacker
                the_secret.activate_if(
                        attacked == the_secret.controller.hero &&
                                possible_targets.size() > 0)
                        {
                            Collections.shuffle(possible_targets)
                            changed_attacked = possible_targets[0]
                        }
            }
        }
    }
}

class Misha extends CardDefinition {
    Misha(Game game) {
        super(game)
        name = 'Misha'; type = 'minion'; creature_type = 'beast'; cost = 3; attack = 4; max_health = 4
        text = 'Taunt.'
        collectible = false
        reserved_to = "Hunter"
        when_coming_in_play(text) {
            this_minion.gains(TAUNT)
        }
    }
}

class MultiShot extends CardDefinition {
    MultiShot(Game game) {
        super(game)
        name = 'Multi-Shot'; type = 'spell'; cost = 4
        text = 'Deal 3 damage to two random enemy minions.'
        reserved_to = "Hunter"
        before_play("check targets") {
            check(opponent.board.size() >= 2, "not enough targets")
        }
        when_played(text) {
            def possible_choices = enemy_minions
            Collections.shuffle(possible_choices)
            this_spell.deal_spell_damage(3, possible_choices[0])
            this_spell.deal_spell_damage(3, possible_choices[1])
        }
    }
}

class SavannahHighmane extends CardDefinition {
    SavannahHighmane(Game game) {
        super(game)
        name = 'Savannah Highmane'; type = 'minion'; creature_type = 'beast'; cost = 6; attack = 6; max_health = 5
        text = 'Deathrattle: Summon two 2/2 Hyenas.'
        reserved_to = "Hunter"
        when_coming_in_play(text) {
            def savannah_highmane = this_minion
            this_minion.when_it_is_destroyed(text) {
                2.times {
                    game.summon(savannah_highmane.controller, "Hyena")
                }
            }
        }
    }
}

class ScavengingHyena extends CardDefinition {
    ScavengingHyena(Game game) {
        super(game)
        name = 'Scavenging Hyena'; type = 'minion'; creature_type = 'beast'; cost = 2; attack = 2; max_health = 2
        text = 'Whenever a friendly Beast dies, gain +2/+1.'
        reserved_to = "Hunter"
        when_coming_in_play(text) {
            def scavenging_hyena = this_minion
            this_minion.when_a_minion_dies(text) {
                if (that_minion.is_a_beast() && that_minion.controller == scavenging_hyena.controller) {
                    scavenging_hyena.gains('+2/+1')
                }
            }
        }
    }
}

class Snake extends CardDefinition {
    Snake(Game game) {
        super(game)
        name = 'Snake'; type = 'minion'; creature_type = 'beast'; cost = 0; attack = 1; max_health = 1
        collectible = false
        reserved_to = "Hunter"
    }
}

class SnakeTrap extends CardDefinition {
    SnakeTrap(Game game) {
        super(game)
        name = 'Snake Trap'; type = 'spell'; cost = 2
        text = 'Secret: When one of your minions is attacked, summon three 1/1 Snakes.'
        reserved_to = "Hunter"
        is_a_secret = true
        when_played("create $text") {
            def the_secret = you.create_secret(this_spell)
            the_secret.when_a_character_attacks("check $text") {
                the_secret.activate_if(
                        attacked.is_a_minion() &&
                                attacked.controller == the_secret.controller)
                        {
                            3.times {
                                game.summon(the_secret.controller, "Snake")
                            }
                        }
            }
        }
    }
}

class Snipe extends CardDefinition {
    Snipe(Game game) {
        super(game)
        name = 'Snipe'; type = 'spell'; cost = 2
        text = 'Secret: When your opponent plays a minion, deal 4 damage to it.'
        reserved_to = "Hunter"
        is_a_secret = true
        when_played("create $text") {
            def the_secret = you.create_secret(this_spell)
            the_secret.when_a_minion_is_played("check $text") {
                the_secret.activate_if(that_minion.controller != the_secret.controller) {
                    the_secret.deal_damage(4, that_minion)
                }
            }
        }
    }
}

class StarvingBuzzard extends CardDefinition {
    StarvingBuzzard(Game game) {
        super(game)
        name = 'Starving Buzzard'; type = 'minion'; creature_type = 'beast'; cost = 5; attack = 3; max_health = 2
        text = 'Whenever you summon a Beast, draw a card.'
        reserved_to = "Hunter"
        when_coming_in_play(text) {
            def buzzard = this_minion
            this_minion.when_a_minion_is_summoned("check $text") {
                if (that_minion.controller == buzzard.controller && that_minion.is_a_beast()) {
                    you.draw(1)
                }
            }
        }
    }
}

class TimberWolf extends CardDefinition {
    TimberWolf(Game game) {
        super(game)
        name = 'Timber Wolf'; type = 'minion'; creature_type = 'beast'; cost = 1; attack = 1; max_health = 1
        text = 'Your other Beasts have +1 Attack.'
        reserved_to = "Hunter"
        when_coming_in_play(text) {
            def timber_wolf = this_minion
            timber_wolf.when_attack_is_evaluated(text) {
                if (that_target.is_a_minion() &&
                        that_minion.controller == timber_wolf.controller &&
                        that_minion.is_a_beast() &&
                        that_minion != timber_wolf) {
                    attack_increase += 1
                    Log.info "   - Timberwolf attack_increase=$attack_increase"
                }
            }
        }
    }
}

class Tracking extends CardDefinition {
    Tracking(Game game) {
        super(game)
        name = 'Tracking'; type = 'spell'; cost = 1
        text = 'Look at the top three cards of your deck. Draw one and discard the others.'
        reserved_to = "Hunter"
        get_targets = [{ [you.deck.cards[0], you.deck.cards[1], you.deck.cards[2]] }]
        before_play("check deck size") {
            check(you.deck.cards.size() >= 3, "not enough cards in your deck")
        }
        when_played(text) {
            def choices = [you.deck.cards[0], you.deck.cards[1], you.deck.cards[2]]
            def to_keep = select_card(choices)
            3.times {
                if (you.deck.cards[0] == to_keep) {
                    you.draw(1)
                } else {
                    you.deck.cards.remove(0)    // discard
                }
            }
        }
    }
}

class TundraRhino extends CardDefinition {
    TundraRhino(Game game) {
        super(game)
        name = 'Tundra Rhino'; type = 'minion'; creature_type = 'beast'; cost = 5; attack = 2; max_health = 5
        text = 'Your Beasts have Charge.'
        reserved_to = "Hunter"
        when_coming_in_play("add $text") {
            def _rhino = this_minion
            _rhino.when_a_buff_is_evaluated("check $text") {
                if (that_target.is_a_minion() &&
                        that_minion.is_a_beast() &&
                        that_minion.controller == _rhino.controller &&
                        that_buff_type == CHARGE) {
                    has_buff = true
                    stop_action = true
                }
            }
        }
    }
}

class Hound extends CardDefinition {
    Hound(Game game) {
        super(game)
        name = 'Hound'; type = 'minion'; creature_type = 'beast'; cost = 1; attack = 1; max_health = 1
        text = 'Charge'
        collectible = false
        reserved_to = "Hunter"
        when_coming_in_play("add $text") {
            this_minion.gains(CHARGE)
        }
    }
}

class UnleashTheHounds extends CardDefinition {
    UnleashTheHounds(Game game) {
        super(game)
        name = 'Unleash the Hounds'; type = 'spell'; cost = 3
        text = 'For each enemy minion, summon a 1/1 Hound with Charge.'
        reserved_to = "Hunter"
        when_played(text) {
            opponent.board.size().times {
                game.summon(you, "Hound")
            }
        }
    }
}

class Webspinner extends CardDefinition {
    Webspinner(Game game) {
        super(game)
        name = 'Webspinner'; type = 'minion'; creature_type = 'beast'; cost = 1; attack = 1; max_health = 1
        text = 'Deathrattle: Add a random Beast card to your hand.'
        reserved_to = "Hunter"
        when_it_is_destroyed(text) {
            this_minion.controller.hand.add(game.new_card(game.card_library.random_beast_name()))
        }
    }
}

