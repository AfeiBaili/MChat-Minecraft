package cn.afeibaili.mchat;

import cn.afeibaili.mchat.socket.message.MessageManager;
import cn.afeibaili.mchat.socket.message.SocketManager;

/**
 * MChat系统
 *
 * @author AfeiBaili
 * @version 2026/3/3 01:45
 */

public class MChatSystem extends SocketManager {
    MessageManager<?, ?> messageManager;

    public static MChatSystem INSTANCE;

    public static void stop() {
        INSTANCE.close();
    }

    {
        INSTANCE = this;
    }

    void buildManager(MessageManager<?, ?> messageManager) {
        this.messageManager = messageManager;
    }

    public MessageManager<?, ?> getMessageManager() {
        return messageManager;
    }
}
