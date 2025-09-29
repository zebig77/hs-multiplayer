package org.zebig.hs.server;

import io.netty.channel.Channel;
import java.util.*;

public class Match {
    private final String matchId;
    private final List<Channel> players = new ArrayList<>();
    private int currentTurnIndex = 0;

    public Match(String matchId) {
        this.matchId = matchId;
    }

    public String getMatchId() {
        return matchId;
    }

    public void addPlayer(Channel c) {
        players.add(c);
    }

    public List<Channel> getPlayers() {
        return Collections.unmodifiableList(players);
    }

    public Channel getCurrentPlayer() {
        if (players.isEmpty()) return null;
        return players.get(currentTurnIndex);
    }

    public void nextTurn() {
        if (!players.isEmpty()) {
            currentTurnIndex = (currentTurnIndex + 1) % players.size();
        }
    }

    public boolean isPlayerTurn(Channel c) {
        return getCurrentPlayer() == c;
    }
}
