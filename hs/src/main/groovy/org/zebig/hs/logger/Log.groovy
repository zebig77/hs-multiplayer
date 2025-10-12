package org.zebig.hs.logger

class Log {
	
	static info(String msg) {
		println msg
	}

    static error(String msg) {
        System.err.println msg
    }

}
