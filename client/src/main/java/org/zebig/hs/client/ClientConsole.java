package org.zebig.hs.client;

import org.zebig.hs.protobuf.GameProto;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32LengthFieldPrepender;

import java.util.Map;
import java.util.Scanner;

public class ClientConsole {
    private static Channel channel;

    public static void main(String[] args) throws Exception {
        String host = "127.0.0.1";
        int port = 8888;

        EventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap b = new Bootstrap();
            b.group(group)
             .channel(NioSocketChannel.class)
             .handler(new ChannelInitializer<SocketChannel>() {
                 @Override
                 protected void initChannel(SocketChannel ch) {
                     ch.pipeline()
                             .addLast(new ProtobufVarint32FrameDecoder())
                             .addLast(new ProtobufDecoder(GameProto.ServerMessage.getDefaultInstance()))
                             .addLast(new ProtobufVarint32LengthFieldPrepender())
                             .addLast(new ProtobufEncoder())
                             .addLast(new SimpleChannelInboundHandler<GameProto.ServerMessage>() {
                                 @Override
                                 protected void channelRead0(ChannelHandlerContext ctx, GameProto.ServerMessage msg) {
                                     if (msg.hasMatchCreated()) {
                                         System.out.println("Match created with ID: " + msg.getMatchCreated().getMatchId());
                                     } else if (msg.hasChat()) {
                                         System.out.println("[" + msg.getChat().getMatchId() + "] " + msg.getChat().getFrom() + ": " + msg.getChat().getText());
                                     } else if (msg.hasAck()) {
                                         System.out.println("SERVER: " + msg.getAck().getMsg());
                                     } else {
                                         System.out.println("SERVER: " + msg);
                                     }
                                 }
                             });
                 }
             });

            channel = b.connect(host, port).sync().channel();
            System.out.println("Connected to server " + host + ":" + port);
            System.out.println("Commands:");
            System.out.println(" create mode=<mode>");
            System.out.println(" chat <match_id> from=<name> text=<message>");
            System.out.println(" play <match_id> <card_id> action_seq=<n> [target_id=<id>] [choice_num=<n>]");
            System.out.println(" join <match_id>   (client-side only placeholder)");

            Scanner scanner = new Scanner(System.in);
            while (true) {
                System.out.print("> ");
                String line = scanner.nextLine().trim();
                if (line.equalsIgnoreCase("quit") || line.equalsIgnoreCase("exit")) {
                    break;
                }
                handleCommand(line);
            }

            channel.close().sync();
        } finally {
            group.shutdownGracefully();
        }
    }

    private static void handleCommand(String line) {
        String[] parts = line.split("\s+");
        if (parts.length == 0) return;
        String command = parts[0].toLowerCase();
        switch (command) {
            case "play" -> CommandHandlers.handlePlay(parts, channel);
            case "chat" -> CommandHandlers.handleChat(parts, channel);
            case "create" -> CommandHandlers.handleCreate(parts, channel);
            case "join" -> CommandHandlers.handleJoin(parts, channel);
            default -> System.out.println("Unknown command: " + command);
        }
    }
}
