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
                if (message.getString().contains("has made the advancement") && (len = message.getString().lastIndexOf("[")) != -1) {
                    Message.sendToGroup(player + "取得了进度" + message.getString().substring(len));
                    return true;
                }
                if (message.getString().contains("left the game")) {
                    Message.sendToGroup(player + "退出了游戏");
                    return true;
                }
                if (message.getString().contains("joined the game")) {
                    Message.sendToGroup(player + "加入了游戏");
                    return true;
                }
                if (message.getString().contains("fell from a high place")) {
                    Message.sendToGroup(player + "从高处摔了下来");
                    return true;
                }
                Message.sendToGroup(message.getString());
            }
            return true;
        });
        ServerMessageEvents.ALLOW_CHAT_MESSAGE.register((message, player, overlay) -> {
            Message.sendToGroup(player.getName().getString() + ": " + message.getContent().getString());
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
        stringBuilder.delete(leg - 1, leg);
        return stringBuilder.toString();
    }
}
