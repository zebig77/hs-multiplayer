package org.zebig.hs.server;

import org.zebig.hs.protobuf.GameProto;
import io.netty.channel.*;

public class ServerHandler extends SimpleChannelInboundHandler<GameProto.ClientMessage> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, GameProto.ClientMessage msg) throws Exception {
        if (msg.hasPlayCard()) {
            GameProto.PlayCard play = msg.getPlayCard();
            if (play.getCardId() == null || play.getCardId().isEmpty()) {
                ctx.writeAndFlush(GameProto.ServerMessage.newBuilder()
                        .setAck(GameProto.Ack.newBuilder()
                            .setMsg("ERROR: card_id is mandatory")
                            .setServerTime(System.currentTimeMillis()).build())
                        .build());
                return;
            }
            Match match = MatchManager.getMatch(play.getMatchId());
            if (match == null) {
                ctx.writeAndFlush(GameProto.ServerMessage.newBuilder()
                        .setAck(GameProto.Ack.newBuilder()
                                .setMsg("MATCH_NOT_FOUND")
                                .setServerTime(System.currentTimeMillis()).build())
                        .build());
                return;
            }
            if (!match.isPlayerTurn(ctx.channel())) {
                ctx.writeAndFlush(GameProto.ServerMessage.newBuilder()
                        .setAck(GameProto.Ack.newBuilder()
                                .setMsg("NOT_YOUR_TURN")
                                .setServerTime(System.currentTimeMillis()).build())
                        .build());
                return;
            }
            String targetId = play.getTargetId();
            int choiceNum = play.getChoiceNum();
            System.out.println("[" + match.getMatchId() + "] Play: card=" + play.getCardId() + " target=" + targetId + " choice=" + choiceNum);
            for (Channel c : match.getPlayers()) {
                if (c != ctx.channel()) {
                    c.writeAndFlush(GameProto.ServerMessage.newBuilder()
                            .setAck(GameProto.Ack.newBuilder()
                                    .setMsg("Opponent played: " + play.getCardId() + " target=" + targetId + " choice=" + choiceNum)
                                    .setServerTime(System.currentTimeMillis()).build())
                            .build());
                }
            }
            match.nextTurn();
            return;
        }

        GameProto.Ack ack = GameProto.Ack.newBuilder().setMsg("UNKNOWN_PAYLOAD").setServerTime(System.currentTimeMillis()).build();
        ctx.writeAndFlush(GameProto.ServerMessage.newBuilder().setAck(ack).build());
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) {
        MatchManager.removePlayer(ctx.channel());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}
