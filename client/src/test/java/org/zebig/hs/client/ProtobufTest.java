package org.zebig.hs.client;

import org.zebig.hs.protobuf.GameProto;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ProtobufTest {
    @Test
    public void testPlayCardOptionals() throws Exception {
        GameProto.PlayCard pc = GameProto.PlayCard.newBuilder()
                .setMatchId("m1")
                .setCardId("Fireball")
                .setActionSeq(1)
                .setChoiceNum(2) // targetId omitted on purpose
                .build();
        assertEquals("Fireball", pc.getCardId());
        assertEquals("", pc.getTargetId());
        assertEquals(2, pc.getChoiceNum());
    }
}
