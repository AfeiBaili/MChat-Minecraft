package online.afeibaili.mchat;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerWorldEvents;
import net.fabricmc.fabric.api.message.v1.ServerMessageEvents;
import net.minecraft.server.MinecraftServer;

public class Listener {
    static MinecraftServer server;

    public static void load() {
        ServerMessageEvents.ALLOW_GAME_MESSAGE.register((server, message, overlay) -> {
            Message.sendToGroup(message.getString());
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
