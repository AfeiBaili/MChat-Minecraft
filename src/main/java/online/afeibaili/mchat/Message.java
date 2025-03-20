package online.afeibaili.mchat;

import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class Message {
    public static void sendToMC(String message, Formatting formatting) {
        MutableText text = Text.literal(message).formatted(formatting);
        Listener.server.getPlayerManager().broadcast(text, false);
    }

    public static void sendToGroup(String message) {
        SocketHandle.send(message);
    }
}
