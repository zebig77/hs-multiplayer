package org.zebig.hs.mechanics.events

import org.zebig.hs.game.Game
import org.zebig.hs.game.GameObject
import org.zebig.hs.game.ScriptObject

abstract class GlobalEvent extends Event {
	
	GlobalEvent(String name, Game game) {
		super(name, game)
	}
		
	List<GameObject> get_scope() {
        List<GameObject> scope = [
                    game.scope,
                    game.scope.your_hero,
                    game.scope.opponent_hero,
                    game.scope.you.artefact,
                    game.scope.opponent.artefact
            ] as List<GameObject>
        game.scope.your_minions.each {scope.add it }
        game.scope.enemy_minions.each {scope.add it}
        game.scope.active_secrets.each { scope.add it}
        if (game.scope.your_hero.weapon != null) {
            scope.add game.scope.your_hero.weapon
        }
        if (game.scope.opponent_hero.weapon != null) {
            scope.add game.scope.opponent_hero.weapon
        }
		return scope
	}

	GlobalEvent check() {
        for(GameObject o in get_scope()) {
            assert o.game != null
			o.check_event(this)
		}
		return this
	}

}
