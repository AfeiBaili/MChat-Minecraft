package online.afeibaili.mchat;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerWorldEvents;
import net.fabricmc.fabric.api.message.v1.ServerMessageEvents;
import net.minecraft.server.MinecraftServer;

public class Listener {
    static MinecraftServer server;

    public static void load() {
        ServerMessageEvents.ALLOW_GAME_MESSAGE.register((server, message, overlay) -> {
            if (!SocketHandle.client.message.equals(message.getString())) {
                int len;
                String player = message.getString().split(" ")[0];
                if (message.getString().contains("has made the advancement") &&
                        (len = message.getString().lastIndexOf("[")) != -1 && Options.PRINT_ADVANCEMENT.value) {
                    Message.sendToGroup(player + "取得了进度" + message.getString().substring(len));
                    return true;
                }
                if (message.getString().contains("left the game") && Options.PRINT_LEFT_GAME.value) {
                    Message.sendToGroup(player + "退出了游戏");
                    return true;
                }
                if (message.getString().contains("joined the game") && Options.PRINT_JOINED_GAME.value) {
                    Message.sendToGroup(player + "加入了游戏");
                    return true;
                }
                if (message.getString().contains("fell from a high place") && Options.PRINT_DEATH.value) {
                    Message.sendToGroup(player + "从高处摔了下来");
                    return true;
                }
                if (Options.PRINT_OTHER.value) {
                    Message.sendToGroup(message.getString());

                }
            }
            return true;
        });
        ServerMessageEvents.ALLOW_CHAT_MESSAGE.register((message, player, overlay) -> {
            if (Options.PRINT_CHAT.value) {
                Message.sendToGroup(player.getName().getString() + ": " + message.getContent().getString());
            }
            return true;
        });
        ServerWorldEvents.LOAD.register((server, world) -> {
            Listener.server = server;
            SocketHandle.load();
        });
        ServerWorldEvents.UNLOAD.register((server, world) -> {
            SocketHandle.unload();
        });
    }

    public static String getServerPlayers() {
        StringBuilder stringBuilder = new StringBuilder();
        int leg = server.getPlayerNames().length;
        if (leg == 0) return "当前服务器没有玩家！";
        stringBuilder.append("当前玩家").append(leg).append("人：");
        for (String name : server.getPlayerNames()) {
            stringBuilder.append(name).append("、");
        }
        stringBuilder.delete(stringBuilder.length() - 1, stringBuilder.length());
        return stringBuilder.toString();
    }
}
