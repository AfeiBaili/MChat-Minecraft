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
                        (len = message.getString().lastIndexOf("[")) != -1) {
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
}
