package org.zebig.hs.client;

import org.zebig.hs.protobuf.GameProto;
import io.netty.channel.Channel;

import java.util.Map;

public class CommandHandlers {
    public static void handlePlay(String[] parts, Channel channel) {
        if (parts.length < 4) {
            System.out.println("Usage: play <match_id> <card_id> action_seq=<n> [target_id=<id>] [choice_num=<n>]");
            return;
        }
        String matchId = parts[1];
        String cardId = parts[2];
        Map<String, String> namedArgs = CommandParser.parseNamedArgs(parts, 3);
        int actionSeq;
        try {
            actionSeq = Integer.parseInt(namedArgs.get("actionseq"));
        } catch (Exception e) {
            System.out.println("ERROR: action_seq is mandatory and must be a number");
            return;
        }
        String targetId = namedArgs.getOrDefault("targetid", "");
        int choiceNum = 0;
        if (namedArgs.containsKey("choicenum")) {
            try {
                choiceNum = Integer.parseInt(namedArgs.get("choicenum"));
            } catch (NumberFormatException e) {
                System.out.println("ERROR: choice_num must be a number");
                return;
            }
        }
        try {
            GameProto.ClientMessage msg = MessageBuilderHelper.buildPlayCard(matchId, cardId, actionSeq, targetId, choiceNum);
            channel.writeAndFlush(msg);
        } catch (IllegalArgumentException e) {
            System.out.println("ERROR: " + e.getMessage());
        }
    }

    public static void handleChat(String[] parts, Channel channel) {
        if (parts.length < 4) {
            System.out.println("Usage: chat <match_id> from=<name> text=<message>");
            return;
        }
        String matchId = parts[1];
        Map<String, String> named = CommandParser.parseNamedArgs(parts, 2);
        String from = named.get("from");
        String text = named.get("text");
        try {
            GameProto.ClientMessage msg = MessageBuilderHelper.buildChatMessage(matchId, from, text);
            channel.writeAndFlush(msg);
        } catch (IllegalArgumentException e) {
            System.out.println("ERROR: " + e.getMessage());
        }
    }

    public static void handleCreate(String[] parts, Channel channel) {
        Map<String, String> named = CommandParser.parseNamedArgs(parts, 1);
        String mode = named.get("mode");
        try {
            GameProto.ClientMessage msg = MessageBuilderHelper.buildCreateMatch(mode);
            channel.writeAndFlush(msg);
        } catch (IllegalArgumentException e) {
            System.out.println("ERROR: " + e.getMessage());
        }
    }

    public static void handleJoin(String[] parts, Channel channel) {
        if (parts.length < 2) {
            System.out.println("Usage: join <match_id>");
            return;
        }
        String matchId = parts[1];
        System.out.println("Join requested for match " + matchId + " (server-side join not implemented in proto; share match_id and use create/join server API when added).");
    }
}
