package cn.afeibaili.mchat.socket.message;

import cn.afeibaili.mchat.MChatSystem;

/**
 * 消息管理器
 *
 * @author AfeiBaili
 * @version 2026/3/3 00:33
 */

public abstract class MessageManager<Formatting, Server> {
    abstract public Formatting getFormat();

    boolean isShowInOut = true;

    abstract public void sendToMc(String message, Formatting format);

    public void sendFormattingMessageToMC(String message) {
        sendToMc(message, getFormat());
    }

    public void sendToGroup(String message) {
        MChatSystem.INSTANCE.send(new MessageType.Text(message));
    }

    public void sendLoginInOut(String message) {
        if (isShowInOut) {
            MChatSystem.INSTANCE.send(new MessageType.Text(message));
        }
    }

    public void sendHeartbeat() {
        MChatSystem.INSTANCE.send(new MessageType.Heartbeat(""));
    }


    public void parseMessage(String message) {
        String ident = message.substring(0, 4);
        String msg = message.substring(4);
        MessageType messageType = match(ident, msg);
    }

    MessageType match(String ident, String message) {
        switch (ident) {
            case "txt:": {
                MessageType.Text text = new MessageType.Text(message);
                sendToMc(text.message, getFormat());
                return text;
            }

            case "cmd:": {
                MessageType.Command command = new MessageType.Command(message);
                switch (command.message) {
                    case "help":
                    case "菜单": {
                        sendToGroup("help、菜单、close-login-message、关闭登录消息、open-login-message、开启登录消息");
                        break;
                    }
                    case "close-login-message":
                    case "关闭登录消息": {
                        isShowInOut = false;
                        sendToGroup("已关闭登录消息");
                        break;
                    }

                    case "open-login-message":
                    case "开启登录消息": {
                        isShowInOut = true;
                        sendToGroup("已开启登录消息");
                        break;
                    }
                }
            }

            case "het:": {
                return new MessageType.Heartbeat(message);
            }
            default: {
                return null;
            }
        }
    }
}
