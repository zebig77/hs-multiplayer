package org.zebig.hs.client;

import org.zebig.hs.protobuf.GameProto;

public class MessageBuilderHelper {

    public static GameProto.ClientMessage buildPlayCard(
            String matchId,
            String cardId,
            int actionSeq,
            String targetId,
            int choiceNum
    ) {
        if (matchId == null || matchId.isEmpty()) {
            throw new IllegalArgumentException("match_id is mandatory");
        }
        if (cardId == null || cardId.isEmpty()) {
            throw new IllegalArgumentException("card_id is mandatory");
        }
        GameProto.PlayCard.Builder playBuilder = GameProto.PlayCard.newBuilder()
                .setMatchId(matchId)
                .setCardId(cardId)
                .setActionSeq(actionSeq);
        if (targetId != null && !targetId.isEmpty()) {
            playBuilder.setTargetId(targetId);
        }
        if (choiceNum > 0) {
            playBuilder.setChoiceNum(choiceNum);
        }
        return GameProto.ClientMessage.newBuilder()
                .setPlayCard(playBuilder.build())
                .build();
    }

    public static GameProto.ClientMessage buildChatMessage(String matchId, String from, String text) {
        if (matchId == null || matchId.isEmpty()) {
            throw new IllegalArgumentException("match_id is mandatory");
        }
        if (from == null || from.isEmpty()) {
            throw new IllegalArgumentException("from is mandatory");
        }
        if (text == null || text.isEmpty()) {
            throw new IllegalArgumentException("text is mandatory");
        }
        GameProto.ChatMessage chat = GameProto.ChatMessage.newBuilder()
                .setMatchId(matchId)
                .setFrom(from)
                .setText(text)
                .build();
        return GameProto.ClientMessage.newBuilder()
                .setChat(chat)
                .build();
    }

    public static GameProto.ClientMessage buildCreateMatch(String mode) {
        if (mode == null || mode.isEmpty()) {
            throw new IllegalArgumentException("mode is mandatory");
        }
        GameProto.CreateMatch createMatch = GameProto.CreateMatch.newBuilder()
                .setMode(mode)
                .build();
        return GameProto.ClientMessage.newBuilder()
                .setCreateMatch(createMatch)
                .build();
    }
}
