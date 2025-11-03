package online.afeibaili.mchat.socket.message

import net.minecraft.ChatFormatting
import net.minecraft.network.chat.Component
import net.minecraft.server.MinecraftServer
import online.afeibaili.mchat.MChat

/**
 * 消息管理器
 *
 *@author AfeiBaili
 *@version 2025/11/3 16:03
 */

class MessageManager(var server: MinecraftServer?) {
    val messageStyle = ChatFormatting.GRAY

    fun sendToMC(message: String, formatting: ChatFormatting) {
        server!!.playerList.players.forEach { player ->
            player.sendSystemMessage(Component.literal(message).withStyle(formatting))
        }
    }

    fun sendMessageToMC(message: String, formatting: ChatFormatting = ChatFormatting.GRAY) {
        sendToMC(message, formatting)
    }

    fun sendToGroup(message: String) {
        MChat.socketManager.send(TextMessage(message))
    }


    fun parseMessage(message: String) {
        val ident: String = message.take(4)
        val message: String = message.drop(4)
        val msg: Message? = match(ident, message)
    }

    fun match(ident: String, message: String) = when (ident) {
        "txt:" -> TextMessage(message).apply {
            sendToMC(this.message, messageStyle)
        }

        "cmd:" -> CommandMessage(message)
        "het:" -> HeartbeatMessage(message)
        else -> null
    }
}