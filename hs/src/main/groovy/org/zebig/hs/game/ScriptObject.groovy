package org.zebig.hs.game

import org.zebig.hs.mechanics.Trigger
import org.zebig.hs.mechanics.buffs.Buff
import org.zebig.hs.mechanics.buffs.BuffType
import org.zebig.hs.mechanics.events.AnyAttackIsEvaluated
import org.zebig.hs.mechanics.events.AnyBuffIsEvaluated
import org.zebig.hs.mechanics.events.AnyBuffListIsEvaluated
import org.zebig.hs.mechanics.events.AnyCharacterAttacks
import org.zebig.hs.mechanics.events.AnyCharacterIsHealed
import org.zebig.hs.mechanics.events.AnyCostIsEvaluated
import org.zebig.hs.mechanics.events.AnyHealthIsEvaluated
import org.zebig.hs.mechanics.events.AnyHeroTakesDamage
import org.zebig.hs.mechanics.events.AnyMinionDealsDamage
import org.zebig.hs.mechanics.events.AnyMinionDies
import org.zebig.hs.mechanics.events.AnyMinionIsPlayed
import org.zebig.hs.mechanics.events.AnyMinionIsSummoned
import org.zebig.hs.mechanics.events.AnyMinionTakesDamage
import org.zebig.hs.mechanics.events.AnyPowerDamageIsEvaluated
import org.zebig.hs.mechanics.events.AnyPowerHealingIsEvaluated
import org.zebig.hs.mechanics.events.AnySecretIsRevealed
import org.zebig.hs.mechanics.events.AnySpellDamageIsEvaluated
import org.zebig.hs.mechanics.events.AnySpellHealingIsEvaluated
import org.zebig.hs.mechanics.events.AnySpellIsPlayed
import org.zebig.hs.mechanics.events.AnyTurnEnds
import org.zebig.hs.mechanics.events.AnyTurnStarts
import org.zebig.hs.mechanics.events.BeforeItsControllerPlaysACard
import org.zebig.hs.mechanics.events.BeforePlay
import org.zebig.hs.mechanics.events.BeforeUsePower
import org.zebig.hs.mechanics.events.EnragedOff
import org.zebig.hs.mechanics.events.EnragedOn
import org.zebig.hs.mechanics.events.ItAttacks
import org.zebig.hs.mechanics.events.ItComesInPlay
import org.zebig.hs.mechanics.events.ItDealsDamage
import org.zebig.hs.mechanics.events.ItIsAttacked
import org.zebig.hs.mechanics.events.ItIsDestroyed
import org.zebig.hs.mechanics.events.ItIsPlayed
import org.zebig.hs.mechanics.events.ItTakesDamage
import org.zebig.hs.mechanics.events.ItsControllerHeals
import org.zebig.hs.mechanics.events.ItsControllerPlaysACard
import org.zebig.hs.mechanics.events.ItsControllerTurnEnds
import org.zebig.hs.mechanics.events.ItsControllerTurnStarts
import org.zebig.hs.mechanics.events.ItsCostIsEvaluated
import org.zebig.hs.mechanics.events.ItsDurabilityIsReduced
import org.zebig.hs.mechanics.events.SpellTargetSelected
import org.zebig.hs.mechanics.events.ThisPowerIsUsed


abstract class ScriptObject {

    Game game

    ScriptObject(Game game) {
        this.game = game
    }
	
	abstract Trigger add_trigger(Class event_class, Closure c)
	abstract Trigger add_trigger(Class event_class, Closure c, String comment)
	
	int getAttack_increase() {
		return game.current_event.attack_increase
	}

	int getCost_increase() {
		return game.current_event.cost_increase
	}

	int getCost_change() {
		(game.current_event as AnyCostIsEvaluated).cost_change
	}

	void setCost_change(int value) {
		println "      . cost change = $value"
		(game.current_event as AnyCostIsEvaluated).cost_change = value
	}

	void setLowest_cost(int value) {
		println "      . lowest cost = $value"
		(game.current_event as AnyCostIsEvaluated).lowest_cost = value
	}

	void setChosen_target(Target t) {
		println "      . chosen target = $t"
		game.current_event.choice = t
	}
	
	int getHealth_increase() {
		return game.current_event.health_increase
	}

	int getSpell_damage_increase() {
		return (game.current_event as AnySpellDamageIsEvaluated).spell_damage_increase
	}
	
	int getPower_damage_increase() {
		return (game.current_event as AnyPowerDamageIsEvaluated).power_damage_increase
	}
	
	int getSpell_healing_increase() {
		return (game.current_event as AnySpellHealingIsEvaluated).spell_healing_increase
	}
	
	int getPower_healing_increase() {
		return (game.current_event as AnyPowerHealingIsEvaluated).power_healing_increase
	}
	
	int getSpell_damage_amount() {
		return (game.current_event as AnySpellDamageIsEvaluated).spell_damage_amount
	}
	
	int getPower_damage_amount() {
		return (game.current_event as AnyPowerDamageIsEvaluated).power_damage_amount
	}
	
	int getSpell_healing_amount() {
		return (game.current_event as AnySpellHealingIsEvaluated).spell_healing_amount
	}
	
	int getPower_healing_amount() {
		return (game.current_event as AnyPowerHealingIsEvaluated).power_healing_amount
	}
	
	void setAttack_increase(int value) {
		println "      . attack_increase = $value"
		game.current_event.attack_increase = value
	}
	
	void setAttack_change(int value) {
		println "      . attack_change = $value"
		game.current_event.attack_change = value
	}
	
	void setCost_increase(int value) {
		println "      . cost_increase = $value"
		game.current_event.cost_increase = value
	}
	
	BuffType getThat_buff_type() {
		return (game.current_event as AnyBuffIsEvaluated).buff_type
	}

	Card select_card(List<Card> choices) { you.select_card(choices) }
	Target select_target(List<Target> choices) { you.select_target(choices) }
	Target select_spell_target(List<Target> choices) { you.select_spell_target(choices) }

	void setHas_buff(boolean value) {
		(game.current_event as AnyBuffIsEvaluated).has_buff = value
	}

	void setHealth_increase(int value) {
		println "      . health increase = $value"
		game.current_event.health_increase = value
	}

	void setSpell_damage_increase(int value) {
		println "      . spell damage increase = $value"
		(game.current_event as AnySpellDamageIsEvaluated).spell_damage_increase = value
	}

	void setSpell_healing_increase(int value) {
		println "      . spell healing increase = $value"
		(game.current_event as AnySpellHealingIsEvaluated).spell_healing_increase = value
	}

	void setPower_damage_increase(int value) {
		println "      . power damage increase = $value"
		(game.current_event as AnyPowerDamageIsEvaluated).power_damage_increase = value
	}

	void setPower_healing_increase(int value) {
		println "      . power healing increase = $value"
		(game.current_event as AnyPowerHealingIsEvaluated).power_healing_increase = value
	}

	List<Target> getAll_characters() {
		return [your_hero, opponent_hero ]+ your_minions + enemy_minions as List<Target>
	}

	List<Target> getActive_secrets() {
		return opponent.secrets.storage
	}

	List<Card> getAll_minion_targets() {
		return all_minions.findAll{ it.can_be_targeted() }
	}

	List<Card> getAll_minions() {
		return your_minions + enemy_minions
	}

	List<Target> getAll_targets() {
		return all_characters.findAll{ it.can_be_targeted() }
	}
	
	Target getAttacked() {
		return (game.current_event as AnyCharacterAttacks).attacked
	}

	Target getAttacker() {
		return (game.current_event as AnyCharacterAttacks).attacker
	}
	
	void setChanged_attacked(Target t) {
		(game.current_event as AnyCharacterAttacks).changed_attacked = t
	}

	Target getDamaged_target() {
		return game.current_event.target
	}

	int getDamage_amount() {
		return game.current_event.amount
	}

	List<Card> getEnemy_minion_targets() {
		return game.passive_player.minions().findAll{ it.can_be_targeted() }
	}

	List<Card> getEnemy_minions() {
		return game.passive_player.minions()
	}

	int getHeal_amount() {
		return game.current_event.heal_amount
	}

	Target getHealed() {
		return game.current_event.healed
	}

	Target getHealer() {
		return game.current_event.healer
	}

	List<Buff> getThat_buff_list() {
		return game.current_event.additional_buffs
	}

	Card getThat_card() {
		return game.current_event.target
	}

	Target getThat_character() {
		return game.current_event.target
	}

	Card getThat_minion() {
		assert game.current_event.target.is_a_minion()
		return game.current_event.target
	}

	Player getThat_player() {
		return game.current_event.player
	}

	Card getThat_secret() {
		return game.current_event.target
	}

	Card getThat_spell() {
		return game.current_event.target
	}

	Target getThat_target() {
		return game.current_event.target
	}

	Weapon getThat_weapon() {
		return game.current_event.target
	}
	
	Target getThe_attacked() {
		return (game.current_event as ItsDurabilityIsReduced).attacked
	}

	Card getThis_card() {
		return game.current_event.origin as Card
	}

	Card getThis_minion() {
		return game.current_event.origin as Card
	}
	
	Player opponent_of(Player p) {
		return game.opponent_of(p)
	}
	
	Card random_card(List<Card> choices) {
		return game.random_pick(choices) as Card
	}

	Object random_pick(List choices) {
		return game.random_pick(choices)
	}

	Object random_pick(int count, List choices) {
		return game.random_pick(count, choices)
	}

	Card getThis_spell() {
		return game.current_event.origin as Card
	}

	Target getThis_target() {
		return game.current_event.origin as Target
	}

	Weapon getThis_weapon() {
		return game.current_event.origin as Weapon
	}

	Player getOpponent() {
		return game.passive_player
	}

	Hero getOpponent_hero() {
		return game.passive_player.hero
	}

	Player getYou() {
		return game.active_player
	}

	Hero getYour_hero() {
		return game.active_player.hero
	}

	List<Card> getYour_minion_targets() {
		return game.active_player.minions().findAll{ it.can_be_targeted() }
	}

	List<Card> getYour_minions() {
		return game.active_player.minions()
	}

	void setStop_action(boolean value) {
		println "      . setting stop_action = $value for event '${game.current_event}'"
		game.current_event.stop_action = value
	}
	
	Trigger before_play(String comment='', Closure c) {
		return add_trigger( BeforePlay.class, c, "before play: $comment" )
	}
	
	Trigger before_use_power(String comment='', Closure c) {
		return add_trigger( BeforeUsePower.class, c, "before using power: $comment" )
	}
	
	Trigger when_coming_in_play(String comment='', Closure c) {
		return add_trigger( ItComesInPlay.class, c, "when coming in play: $comment" )
	}
	
	Trigger when_a_buff_list_is_evaluated(String comment='', Closure c) {
		return add_trigger( AnyBuffListIsEvaluated.class, c, "when a buff list is evaluated: $comment" )
	}
	
	Trigger when_a_buff_is_evaluated(String comment='', Closure c) {
		return add_trigger( AnyBuffIsEvaluated.class, c, "when a buff is evaluated: $comment" )
	}
	
	Trigger when_a_character_is_healed(String comment='', Closure c) {
		return add_trigger( AnyCharacterIsHealed.class, c, "when a character is healed: $comment" )
	}
	
	Trigger when_a_minion_dies(String comment='', Closure c) {
		return add_trigger( AnyMinionDies.class, c, "when a minion dies: $comment" )
	}
	
	Trigger when_a_minion_takes_damage(String comment='', Closure c) {
		return add_trigger( AnyMinionTakesDamage.class, c, "when a minion takes damage: $comment" )
	}
	
	Trigger when_a_secret_is_revealed(String comment='', Closure c) {
		return add_trigger( AnySecretIsRevealed.class, c, "when a secret is revealed: $comment" )
	}
	
	Trigger when_attack_is_evaluated(String comment='', Closure c) {
		return add_trigger( AnyAttackIsEvaluated.class, c, "when <attack> is evaluated: $comment")
	}
	
	Trigger when_its_cost_is_evaluated(String comment='', Closure c) {
		return add_trigger( ItsCostIsEvaluated.class, c, "when its cost is evaluated: $comment" )
	}

	Trigger when_a_cost_is_evaluated(String comment='', Closure c) {
		return add_trigger( AnyCostIsEvaluated.class, c, "when a cost is evaluated: $comment" )
	}
	
	Trigger when_enraged_no_more(String comment='', Closure c) {
		return add_trigger( EnragedOff.class, c, "when enraged no more: $comment" )
	}
	
	Trigger when_enraged(String comment='', Closure c) {
		return add_trigger( EnragedOn.class, c, "when enraged: $comment" )
	}
	
	Trigger when_health_is_evaluated(String comment='', Closure c) {
		return add_trigger( AnyHealthIsEvaluated.class, c, "when <Health> is evaluated: $comment" )
	}
	
	Trigger when_it_attacks(String comment='', Closure c) {
		return add_trigger( ItAttacks.class, c, "when it attacks: $comment" )
	}
	
	Trigger when_it_is_attacked(String comment='', Closure c) {
		return add_trigger( ItIsAttacked.class, c, "when it is attacked: $comment" )
	}
	
	Trigger when_a_hero_takes_damage(String comment='', Closure c) {
		return add_trigger( AnyHeroTakesDamage.class, c, "when a hero takes damage: $comment" )
	}
	
	Trigger when_a_character_attacks(String comment='', Closure c) {
		return add_trigger( AnyCharacterAttacks.class, c, comment )
	}
	
	Trigger when_a_minion_deals_damage(String comment='', Closure c) {
		return add_trigger( AnyMinionDealsDamage.class, c, comment )
	}
	
	Trigger when_it_deals_damage(String comment='', Closure c) {
		return add_trigger( ItDealsDamage.class, c, comment )
	}
	
	Trigger when_it_is_destroyed(String comment='', Closure c) {
		return add_trigger( ItIsDestroyed.class, c, comment )
	}
	
	Trigger when_played(String comment = null, Closure c) {
		return add_trigger( ItIsPlayed.class, c, "when played: $comment" )
	}
	
	Trigger when_its_controller_heals(String comment = null, Closure c) {
		return add_trigger( ItsControllerHeals.class, c, comment )
	}
	
	Trigger before_its_controller_plays_a_card(String comment='', Closure c) {
		return add_trigger( BeforeItsControllerPlaysACard.class, c, comment )
	}
	
	Trigger when_its_controller_plays_a_card(String comment='', Closure c) {
		return add_trigger( ItsControllerPlaysACard.class, c, comment )
	}
	
	Trigger when_its_controller_turn_ends(String comment='', Closure c) {
		return add_trigger( ItsControllerTurnEnds.class, c, comment )
	}
	
	Trigger when_its_controller_turn_starts(String comment='', Closure c) {
		return add_trigger( ItsControllerTurnStarts.class, c, comment )
	}
	
	Trigger when_a_turn_ends(String comment='', Closure c) {
		return add_trigger( AnyTurnEnds.class, c, comment )
	}
	
	Trigger when_a_turn_starts(String comment='', Closure c) {
		return add_trigger( AnyTurnStarts.class, c, comment )
	}
	
	Trigger when_its_durability_is_reduced(String comment='', Closure c) {
		return add_trigger( ItsDurabilityIsReduced.class, c, comment )
	}
	
	Trigger when_this_power_is_used(String comment='', Closure c) {
		return add_trigger( ThisPowerIsUsed.class, c, comment )
	}
	
	Trigger when_it_takes_damage(String comment='', Closure c) {
		return add_trigger( ItTakesDamage.class, c, comment )
	}
	
	Trigger when_a_spell_target_is_selected(String comment='', Closure c) {
		return add_trigger( SpellTargetSelected.class, c, comment )
	}
	
	Trigger when_a_minion_is_played(String comment='', Closure c) {
		return add_trigger( AnyMinionIsPlayed.class, c, comment )
	}

	Trigger when_a_minion_is_summoned(String comment='', Closure c) {
		return add_trigger( AnyMinionIsSummoned.class, c, comment )
	}

	Trigger when_a_spell_is_played(String comment='', Closure c) {
		return add_trigger( AnySpellIsPlayed.class, c, comment )
	}

	Trigger when_spell_damage_is_evaluated(String comment='', Closure c) {
		return add_trigger( AnySpellDamageIsEvaluated.class, c, comment )
	}

	Trigger when_power_damage_is_evaluated(String comment='', Closure c) {
		return add_trigger( AnyPowerDamageIsEvaluated.class, c, comment )
	}

	Trigger when_spell_healing_is_evaluated(String comment='', Closure c) {
		return add_trigger( AnySpellHealingIsEvaluated.class, c, comment )
	}

	Trigger when_power_healing_is_evaluated(String comment='', Closure c) {
		return add_trigger( AnyPowerHealingIsEvaluated.class, c, comment )
	}

    static void check(boolean check_result, String error_message) {
        if (!check_result) {
            throw new IllegalActionException("check failed: $error_message")
        }
    }
}
