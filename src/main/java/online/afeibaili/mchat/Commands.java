package online.afeibaili.mchat;

import net.minecraft.util.Formatting;

public class Commands {
    public static void parsingMessage(String message) {
        if (message.charAt(message.length() - 1) != '!') return;

        String[] right = message.split("：");
        String[] parma = right[right.length - 1].split(" ");
        switch (parma[parma.length - 1]) {
            case "online!" -> Message.sendToGroup(Listener.getServerPlayers());
            case "color!" -> {
                String color = parma[0];
                Formatting formatting = Formatting.byName(color);
                if (formatting == null) Message.sendToGroup("没有这个颜色！");
                else {
                    SocketHandle.client.style = formatting;
                    Message.sendToGroup("设置成功！" + formatting.getName());
                }
            }
        }
    }
}
