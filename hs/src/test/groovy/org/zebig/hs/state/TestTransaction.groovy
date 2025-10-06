package org.zebig.hs.state

import org.junit.Test
import org.zebig.hs.utils.TestHelper

class TestTransaction extends TestHelper {

    @Test
    void testInitialState() {
        assert g.transaction == null
        g.begin_transaction()
        assert g.transaction != null
        assert g.transaction.change_log.size() == 0
    }

    @Test
    void testPlayEffectOnTransaction() {

        g.begin_transaction()
        assert p1.available_mana == 1
        assert p1.board.size() == 0
        _play("Angry Chicken")
        assert p1.board.size() == 1
        assert g.transaction.change_log.size() > 0
        assert p1.available_mana == 0

        g.rollback_transaction()
        assert p1.board.size() == 0
        assert p1.available_mana == 1
    }
}
