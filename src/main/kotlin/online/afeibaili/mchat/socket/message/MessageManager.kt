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
    fun sendToMC(message: String, formatting: ChatFormatting) {
        server!!.sendSystemMessage(Component.literal(message).withStyle(formatting))
    }

    fun sendMessageToMC(message: String, formatting: ChatFormatting = ChatFormatting.GRAY) {
        server!!.sendSystemMessage(Component.literal(message).withStyle(formatting))
    }

    fun sendToGroup(message: String) {
        MChat.socketManager.send(TextMessage(message))
    }
}