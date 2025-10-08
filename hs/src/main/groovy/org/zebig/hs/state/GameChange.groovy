package org.zebig.hs.state

class GameChange {
    /*
    TODO pubLic     PlayerBecomesActive(player_name)
    TODO public     ManaStatusChanged(player_name, mana_status)
    TODO private    CardDrawn(card_detail)
    TODO private    CardCostChanged(card_detail)
    TODO public     CardTransformed(old_card_detail, new_card_detail)
    TODO public     MinionPlayed(card_detail, position, [target], [effect_chosen])
    TODO public     MinionAttacks(card_detail, target, attack_power)
    TODO public     MinionTakesDamage(card_detail, minion_health, damage_amount)
    TODO public     MinionIsHealed(card_detail, minion_health, heal_amount)
    TODO public     HeroTakesDamage(player_name, hero_health, damage_amount)
    TODO public     MinionIsHealed(card_detail, minion_health, heal_amount)
    TODO public     MinionDies(card_detail)
    TODO public     HeroPowerUsed(player_name, [target])
    TODO public     SpellPlayed(card_detail, [target], [effect_chosen]) note: non-secret spells
    TODO private    SecretPlayed(card_detail)
    TODO public     SecretRevealed(card_detail)
    TODO public     WeaponReady(card_detail)
    TODO public     WeaponDestroyed(card_detail)
    TODO public     ZoneSizeChange(zone_name, new_size) deck, board, hand, secrets
    TODO public     HealingReceived(target)
    TODO public     HeroDies(player_name)
    TODO public     HeroTakesFatigue(player_name, fatigue_amount, new_hero_health)
    TODO public     TargetFreeze(target_detail)
    TODO public     TargetUnFreeze(target_detail)
     */

    String name
    Map<String,Object> properties
    boolean is_public

    GameChange(String name, Map<String,Object> properties, boolean is_public=true) {
        this.name = name
        this.properties = properties
        this.is_public = is_public
    }

    String toString() {
        "$name:$properties"
    }
}
