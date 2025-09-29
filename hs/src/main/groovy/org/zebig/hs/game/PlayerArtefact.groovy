package org.zebig.hs.game

class PlayerArtefact extends GameObject {
	
	String name
	
    PlayerArtefact(Game game, String name) {
        super(game)
        this.name = name
    }

    String toString() { name }

}
