package org.zebig.hs.server;

import io.netty.channel.Channel;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class MatchManager {
    private static final Map<String, Match> matches = new ConcurrentHashMap<>();

    public static Match createMatch(Channel c) {
        String matchId = UUID.randomUUID().toString();
        Match match = new Match(matchId);
        match.addPlayer(c);
        matches.put(matchId, match);
        return match;
    }

    public static Match getMatch(String matchId) {
        return matches.get(matchId);
    }

    public static void joinMatch(String matchId, Channel c) {
        Match match = matches.computeIfAbsent(matchId, id -> new Match(matchId));
        match.addPlayer(c);
    }

    public static void removePlayer(Channel c) {
        matches.values().forEach(match -> match.getPlayers().remove(c));
    }
}
