package online.afeibaili.mchat

import online.afeibaili.mchat.socket.SocketManager
import online.afeibaili.mchat.socket.message.MessageManager


/**
 * 接口文件，须实现
 *
 *@author AfeiBaili
 *@version 2025/12/17 13:51
 */


class MChatSystem() : SocketManager() {
    private lateinit var message: MessageManager<*, *>

    init {
        INSTANCE = this
    }

    fun buildManager(messageManager: MessageManager<*, *>) {
        message = messageManager
    }

    fun getMessageManager(): MessageManager<*, *> {
        return message
    }

    companion object {
        lateinit var INSTANCE: MChatSystem

        fun close() {
            INSTANCE.close()
        }
    }
}
