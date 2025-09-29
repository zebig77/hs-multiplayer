package org.zebig.hs.utils;

import org.junit.Test;

class TestRandomGenerator extends TestHelper {

	@Test
	public void RandomGenerator_average_10000_tries() {
		def r = new RandomGenerator()
		assert r.forced_ints != null
		assert r.random != null
		assert r.get_random_int(10) < 10
		assert r.get_random_int(10) >= 0
		def tot = 0
		def tries = 10000
		tries.times{
			tot += r.get_random_int(100) // returns 0-99
		}
		def moy = tot/tries
		println "apr�s $tries essais, la moyenne est $moy"
		assert moy >= 48
		assert moy <= 51
	}

	@Test
	public void TestRandomGenerator_forced() {
		def r = new RandomGenerator()
		r.forced_ints = [1, 3, 5]
		assert r.get_random_int(100) == 1
		assert r.forced_ints == [ 3, 5 ]
		assert r.get_random_int(100) == 3
		assert r.get_random_int(100) == 5
		assert r.forced_ints.size() == 0
	}

}
