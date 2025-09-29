package org.zebig.hs.autoplay

import org.zebig.hs.game.Card
import org.zebig.hs.game.GameObject
import org.zebig.hs.game.Hero
import org.zebig.hs.game.Target

class PlayerAction {

	static List<String> valid_actions = [
		'PLAY_CARD',
		'ATTACK',
		'USE_HERO_POWER',
		'END_TURN',
		'CONCEDE_GAME'
	]

	String action	// see valid_actions
	GameObject played
	String choice	// Druid-only for cards "Choose One: ..."
	Target target
	int position // position in battlefield

	static List<PlayerAction> combinePlayCardActions(Card c, String _choice, List<Target> _l_targets) {

		List<PlayerAction> pa = []
		
		if (_l_targets == null || _l_targets == []) {
			_l_targets = [ null ]
		}

		// possible positions on battlefield (minions)
		if (c.is_a_minion()) {
			def _l_positions = 0..c.game.active_player.minions.size()
			GroovyCollections.combinations([_l_targets, _l_positions]).each {
				def _target = it[0]
				int _position = 0
				if (it[1] != null) {
					_position = it[1]
				}
				pa += new PlayerAction(action:'PLAY_CARD', played:c, choice:_choice, target:_target, position:_position)
			}
			return pa
		}

		// not a minion
		if (c.get_targets != null && _l_targets == [null]) {
			return [] // no valid target
		}
		
		_l_targets.each { Target _target ->
			pa += new PlayerAction(action:'PLAY_CARD', played:c, choice:_choice, target:_target)
		}
		return pa
	}

	static List<PlayerAction> possiblePlayCardActions(Card c) {
		if (c.can_be_played() == false) {
			return []
		}

		if (c.druid_choices == null) {
			if (c.get_targets != null) {
				return combinePlayCardActions(c,null,c.get_targets[0].call())
			}
			return combinePlayCardActions(c,null,null)
		}
			
		// special case: 2 card versions for choices #0 et #1
		return combinePlayCardActions(c,c.druid_choices[0],c.get_targets[0]?.call()) 
			 + combinePlayCardActions(c,c.druid_choices[1],c.get_targets[1]?.call())			
	}

	static List<PlayerAction> possibleUsePowerActions(Hero h) {
		if (h.can_use_power() == false) {
			return []
		}

		// power with no targets
		if (h.power.get_targets == null) {
			return [
				new PlayerAction(action:'USE_HERO_POWER', played:h.power)
			]
		}

		// 1 action for each possible target
		List<PlayerAction> pa = []
		List<Target> lt = h.power.get_targets[0].call()
		lt.each { Target _ta ->
			pa += new PlayerAction(action:'USE_HERO_POWER', played:h.power,target:_ta)
		}
		return pa
	}

	static List<PlayerAction> possibleAttackActions(Target t) {
		if (t.can_attack() == false) {
			return []
		}

		List<Target> possible_targets = t.game.passive_player.minions() + t.game.passive_player.hero
		
		List<Target> targets_with_taunt = possible_targets.findAll{it.has_taunt()}
		if (targets_with_taunt != []) {
			possible_targets = targets_with_taunt
		}
		
		List<PlayerAction> pa = []
		possible_targets.each { 
			if (it.can_be_attacked()) {
				pa += new PlayerAction(action:'ATTACK', played:t,target:it)
			}
		}
		return pa
	}

	void setAction(String a) {
		assert valid_actions.contains(a)
		this.action = a
	}

	String toString() {
		def sb = new StringBuilder(action)
		if (action == 'PLAY_CARD') {
			Card c = played
			sb << " id=${c.id}"
			if (choice != null) {
				sb << " choice='$choice'"
			}
			if (target != null) {
				sb << " target=$target"
			}
			if (c.is_a_minion()) {
				sb << " position=$position"
			}
			sb << " // name='${c.name}' cost=${c.cost} text='${c.text}'"
		}
		else if (action == 'ATTACK') {
			sb << " attacker=$played attacked=$target"
		}
		else if (action == 'USE_HERO_POWER') {
			sb << " power='$played'"
			if (target != null) {
				sb << " target=$target"
			}
		}
		return sb.toString()
	}

}
