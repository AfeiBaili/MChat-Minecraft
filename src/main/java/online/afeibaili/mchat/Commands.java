package online.afeibaili.mchat;


import net.minecraft.ChatFormatting;

public class Commands {
    public static void parsingMessage(String message) {
        if (message.isEmpty()) return;
        if (message.charAt(message.length() - 1) != '!') return;

        String[] right = message.split("：");
        String[] parma = right[right.length - 1].split(" ");
        switch (parma[parma.length - 1]) {
            case "online!" -> Message.sendToGroup(Listener.getServerPlayers());
            case "color!" -> {
                String color = parma[0];
                ChatFormatting formatting = ChatFormatting.getByName(color);
                if (formatting == null) Message.sendToGroup("没有这个颜色！");
                else {
                    SocketHandle.client.style = formatting;
                    Message.sendToGroup("设置成功！" + formatting.getName());
                }
            }
            case "print!" -> {
                if (parma.length != 2) return;
                switch (parma[0]) {
                    case "死亡" -> Options.PRINT_DEATH.value = !Options.PRINT_DEATH.value;
                    case "加入" -> Options.PRINT_JOINED_GAME.value = !Options.PRINT_JOINED_GAME.value;
                    case "退出" -> Options.PRINT_LEFT_GAME.value = !Options.PRINT_LEFT_GAME.value;
                    case "进度" -> Options.PRINT_ADVANCEMENT.value = !Options.PRINT_ADVANCEMENT.value;
                    case "其他" -> Options.PRINT_OTHER.value = !Options.PRINT_OTHER.value;
                    case "聊天" -> Options.PRINT_CHAT.value = !Options.PRINT_CHAT.value;
                    default -> Message.sendToGroup("参数不正确！请填入[死亡|加入|退出|进度|其他|聊天]");
                }
                Message.sendToGroup("当前输出设置：" +
                        Options.PRINT_ADVANCEMENT + "、" +
                        Options.PRINT_JOINED_GAME + "、" +
                        Options.PRINT_LEFT_GAME + "、" +
                        Options.PRINT_DEATH + "、" +
                        Options.PRINT_CHAT + "、" +
                        Options.PRINT_OTHER
                );
            }
        }
    }
}
