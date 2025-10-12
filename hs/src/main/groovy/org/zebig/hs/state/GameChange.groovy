package org.zebig.hs.state

class GameChange {

    enum Type {
        PlayerBecomesActive,
        ManaStatusChanged,
        CardDrawn,
        CardPlayed,
        MinionTakesDamage,
        MinionIsHealed,
        MinionDies,
        HeroTakesDamage,
        HeroIsHealed,
        HeroDies,
        ZoneSizeChange,
        TargetSelected,
    }
    /*
    pubLic  PlayerBecomesActive(player_name)
    public  ManaStatusChanged(player_name, max_mana, available_mana, overload)
    private CardDrawn[card_id](player_name, card_id, name, type, cost, text, attack, max_health)
    public  HeroTakesDamage(player_name, hero_health, damage_amount)
    public  MinionTakesDamage[CardId](card_id, minion_health, damage_amount)
    public  MinionDies[CardId](card_id)
    public  ZoneSizeChange[player_name](zone_name, new_size) // deck, board, hand, secrets
    public  HeroDies(player_name)
    public  MinionPlayed[card_id](player_name, card_id, position, name, type, cost, text, attack, max_health))
    public  SpellPlayed[card_id](player_name, card_id, position, name, type, cost, text)) note: non-secret spells
    public  MinionIsHealed(card_detail, minion_health, heal_amount)
    TODO public     HeroIsHealed[player_name](heal_amount)
    TODO private    CardCostChanged(player_name, cost)
    TODO public     CardTransformed(old_card_detail, new_card_detail)
    TODO public     MinionAttacks(card_detail, target, attack_power)
    TODO public     HeroPowerUsed(player_name, [target])
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
