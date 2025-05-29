package online.afeibaili.mchat;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static online.afeibaili.mchat.MChat.LOGGER;

public class Message {
    static ExecutorService executor = Executors.newSingleThreadExecutor();

    public static void sendToMC(String message, ChatFormatting formatting) {
        Listener.server.getPlayerList().getPlayers().forEach(player -> {
            player.sendSystemMessage(Component.literal(message).withStyle(formatting));
        });
    }

    public static void sendToGroup(String message) {
        executor.execute(() -> {
            try {
                SocketHandle.send(message);
            } catch (Exception e) {
                LOGGER.info("发送消息失败: {}", e.getMessage());
            }
        });
    }

    public static void shutdown() {
        executor.shutdown();
    }
}
