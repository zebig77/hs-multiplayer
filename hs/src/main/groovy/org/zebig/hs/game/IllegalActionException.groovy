package org.zebig.hs.game

import org.zebig.hs.logger.Log

class IllegalActionException extends Exception {
	
	IllegalActionException(String msg) {
		super(msg)
        Log.error "Illegal action : $msg"
	}

}
