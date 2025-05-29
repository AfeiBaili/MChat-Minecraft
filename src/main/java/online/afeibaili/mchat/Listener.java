package online.afeibaili.mchat;

import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.event.level.LevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class Listener {
    static MinecraftServer server;

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

    @SubscribeEvent
    public void onWorldLoadEvent(LevelEvent.Load event) {
        SocketHandle.load();
    }

    @SubscribeEvent
    public void onWorldUnloadEvent(LevelEvent.Unload event) {
        SocketHandle.unload();
        Message.shutdown();
        if (SocketHandle.heartbeatTimer != null) {
            SocketHandle.heartbeatTimer.cancel();
        }
    }

    @SubscribeEvent
    public void onMessageEvent(ServerChatEvent event) {
        Component message = event.getMessage();
        ServerPlayer player = event.getPlayer();
        if (Options.PRINT_CHAT.value) {
            Message.sendToGroup(player.getName().getString() + ": " + message.getString());
        }
    }
}
