package online.afeibaili.mchat.socket.message

import online.afeibaili.mchat.MChatSystem.Companion.INSTANCE


/**
 * 消息管理器
 *
 *@author AfeiBaili
 *@version 2025/11/3 16:03
 */
abstract class MessageManager<Formatting, Server>() {
    abstract var formatting: Formatting
    abstract fun sendToMC(message: String, formatting: Formatting)
    var isShowInOut: Boolean = true

    fun sendFormattingMessageToMC(message: String) {
        sendToMC(message, formatting)
    }

    fun sendToGroup(message: String) {
        INSTANCE.send(MessageType.Text(message))
    }

    fun sendLoginInOut(message: String) {
        if (isShowInOut) {
            INSTANCE.send(MessageType.Text(message))
        }
    }

    fun sendHeartbeat() {
        INSTANCE.send(MessageType.Heartbeat(""))
    }

    fun parseMessage(message: String) {
        val ident = message.take(4)
        val message: String = message.drop(4)
        match(ident, message)
    }

    private fun match(ident: String, message: String) = when (ident) {
        "txt:" -> MessageType.Text(message).apply {
            sendToMC(this.message, formatting)
        }

        "cmd:" -> MessageType.Command(message).apply {
            when (this.message) {
                "help", "菜单" -> sendToGroup(
                    """
                    菜单
                    help
                    close-login-message
                    关闭登录消息
                    open-login-message
                    开启登录消息
                """.trimIndent()
                )

                "close-login-message", "关闭登录消息" -> {
                    isShowInOut = false
                    sendToGroup("已关闭登录消息")
                }

                "open-login-message", "开启登录消息" -> {
                    isShowInOut = true
                    sendToGroup("已开启登录消息")
                }
            }
        }

        "het:" -> MessageType.Heartbeat(message)
        else -> null
    }
}