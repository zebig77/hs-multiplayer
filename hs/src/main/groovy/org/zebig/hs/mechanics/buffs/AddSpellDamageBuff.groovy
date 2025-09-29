package org.zebig.hs.mechanics.buffs

import org.zebig.hs.game.Target

class AddSpellDamageBuff extends Buff {

	static final BUFF_PATTERN = /spell damage \+(\d*)/

	AddSpellDamageBuff(String buff_string, Target t) {
		super(buff_string, t)
		def matcher = buff_string =~ BUFF_PATTERN
		def spell_damage_change = matcher[0][1].toInteger()
		this.spell_damage_increase = spell_damage_change
	}

}