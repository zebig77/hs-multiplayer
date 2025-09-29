package org.zebig.hs.utils

class RandomGenerator {

	Random random
	List forced_ints = []
	
	RandomGenerator(long seed=System.currentTimeMillis()) {
		random = new Random(seed)
	}

	int get_random_int(int bound) {
		return (forced_ints.size() == 0) ? random.nextInt(bound) : forced_ints.remove(0)  
	}
}
