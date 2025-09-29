package org.zebig.hs.mechanics.events

import org.zebig.hs.game.GameObject
import org.zebig.hs.game.Target

class AnyCostIsEvaluated extends GlobalEvent {
	
	int cost_increase
	int cost_change = -1	// -1 means no cost change
	int lowest_cost = -1	// -1 means no minimum cost
	Target target
	
	AnyCostIsEvaluated(Target target) {
		super("a cost is evaluated", target.game)
		this.target = target
	}
	
	@Override
	List<GameObject> get_scope() {
		if (target.controller == null) {
			//return Object.get_scope()
			return super.get_scope()
		}
		// special case: triggers for cards in hand apply
		//return Object.get_scope() + target.controller.hand.cards.storage
		return super.get_scope() + target.controller.hand.cards.storage
	}
	
}
