package online.afeibaili.mchat.socket.message

import online.afeibaili.mchat.MChatSystem.Companion.INSTANCE


/**
 * 消息管理器
 *
 *@author AfeiBaili
 *@version 2025/11/3 16:03
 */
abstract class MessageManager<Formatting, Server>(var server: Server? = null) {
    abstract var formatting: Formatting

    abstract fun sendToMC(message: String, formatting: Formatting)

    fun sendFormattingMessageToMC(message: String) {
        sendToMC(message, formatting)
    }

    fun sendToGroup(message: String) {
        INSTANCE.socketManager.send(MessageType.Text(message))
    }

    fun sendHeartbeat() {
        INSTANCE.socketManager.send(MessageType.Heartbeat(""))
    }

    fun parseMessage(message: String) {
        val ident = message.take(4)
        val message: String = message.drop(4)
        val msg: MessageType? = match(ident, message)
    }

    private fun match(ident: String, message: String) = when (ident) {
        "txt:" -> MessageType.Text(message).apply {
            sendToMC(this.message, formatting)
        }

        "cmd:" -> MessageType.Command(message)
        "het:" -> MessageType.Heartbeat(message)
        else -> null
    }
}