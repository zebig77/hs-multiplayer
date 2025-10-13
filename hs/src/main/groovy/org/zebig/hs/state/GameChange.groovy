package org.zebig.hs.state

class GameChange {

    enum Type {
        CardDrawn,
        CardPlayed,
        HeroAttacksHero,
        HeroAttacksMinion,
        HeroDies,
        HeroPowerUsed,
        HeroTakesDamage,
        HeroIsHealed,
        ManaChanged,
        MinionAttacksMinion,
        MinionAttacksHero,
        MinionDies,
        MinionIsHealed,
        MinionTakesDamage,
        PlayerBecomesActive,
        TargetSelected,
        WeaponDestroyed,
        WeaponEquipped,
        ZoneSizeChange,
    }
    /*
    pubLic  PlayerBecomesActive(player_name)
    public  ManaStatusChanged(player_name, max_mana, available_mana, overload)
    private CardDrawn[card_id](player_name, card_id, name, type, cost, text, attack, max_health)
    public  HeroTakesDamage(player_name, hero_health, damage_amount, armor)
    public  MinionTakesDamage[CardId](card_id, minion_health, damage_amount)
    public  MinionDies[CardId](card_id)
    public  ZoneSizeChange[player_name](zone_name, new_size) // deck, board, hand, secrets
    public  HeroDies(player_name)
    public  MinionPlayed[card_id](player_name, card_id, position, name, type, cost, text, attack, max_health))
    public  SpellPlayed[card_id](player_name, card_id, position, name, type, cost, text)) note: non-secret spells
    public  MinionIsHealed[card_id](player_name, card_id, health, heal_amount, max_health)
    public  HeroIsHealed[player_name](heal_amount, health, max_health)
    public  MinionAttacksMinion[attacker_id](player_name, attacker_id, attacked_id, attack_damage)
    public  MinionAttacksHero[attacker_id](player_name, attacker_id, attacked_player_name, attack_damage)
    public  HeroAttacksMinion[player_name](player_name, attacked_id, attack_damage)
    public  HeroAttacksHero[player_name](player_name, attacked_player_name, attack_damage)
    public  WeaponEquipped[card_id](player_name, card_id, name, text, attack, durability)
    public  WeaponDestroyed[player_name](player_name, card_id)
    TODO public     HeroPowerUsed(player_name)
    TODO public     HeroArmorChanged(player_name, armor)
    TODO private    CardCostChanged[card_id](player_name, cost)
    TODO public     CardTransformed(old_card_detail, new_card_detail)
    TODO private    SecretPlayed(card_detail)
    TODO public     SecretRevealed(card_detail)
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
