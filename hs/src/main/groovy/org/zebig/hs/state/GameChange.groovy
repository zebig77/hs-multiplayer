package org.zebig.hs.state

class GameChange {

    enum Type {
        PlayerBecomesActive,
        ManaStatusChanged,
        CardDrawn,
        MinionTakesDamage,
        MinionDies,
        HeroTakesDamage,
        HeroDies,
        ZoneSizeChange,
    }
    /*
    pubLic     PlayerBecomesActive(player_name)
    public     ManaStatusChanged(player_name, max_mana, available_mana, overload)
    private    CardDrawn[CardId](player_name, card_id, name, type, cost, text, attack, max_health)
    public     HeroTakesDamage(player_name, hero_health, damage_amount)
    public     MinionTakesDamage[CardId](card_id, minion_health, damage_amount)
    public     MinionDies[CardId](card_id)
    TODO public     ZoneSizeChange[player_name](zone_name, new_size) // deck, board, hand, secrets
    TODO public     HeroDies(player_name)
    TODO private    CardCostChanged(player_name, cost)
    TODO public     CardTransformed(old_card_detail, new_card_detail)
    TODO public     MinionPlayed(card_detail, position, [target], [effect_chosen])
    TODO public     MinionAttacks(card_detail, target, attack_power)
    TODO public     MinionIsHealed(card_detail, minion_health, heal_amount)
    TODO public     MinionIsHealed(card_detail, minion_health, heal_amount)
    TODO public     HeroPowerUsed(player_name, [target])
    TODO public     SpellPlayed(card_detail, [target], [effect_chosen]) note: non-secret spells
    TODO private    SecretPlayed(card_detail)
    TODO public     SecretRevealed(card_detail)
    TODO public     WeaponReady(card_detail)
    TODO public     WeaponDestroyed(card_detail)
    TODO public     HealingReceived(target)
    TODO public     HeroTakesFatigue(player_name, fatigue_amount, new_hero_health)
    TODO public     TargetFreeze(target_detail)
    TODO public     TargetUnFreeze(target_detail)
     */

    Type type
    String target_id // what has changed
    Map<String,Object> properties
    boolean is_public

     GameChange(Type type, String target_id, Map<String,Object> properties, boolean is_public=true) {
        this.type = type
        this.target_id = target_id
        this.properties = properties
        this.is_public = is_public
    }

    String getName() {
        type.toString()+"[$target_id]"
    }

    String toString() {
        if (this.is_public) {
            "public: $name:$properties"
        } else {
            "private: $name:$properties"
        }
    }
}
