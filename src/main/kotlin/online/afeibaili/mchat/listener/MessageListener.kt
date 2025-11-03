package online.afeibaili.mchat.listener

import net.minecraft.network.chat.Component
import net.minecraft.server.level.ServerPlayer
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.neoforge.event.ServerChatEvent
import online.afeibaili.mchat.MChat.messageManager


/**
 * 消息监听器
 *
 *@author AfeiBaili
 *@version 2025/11/3 13:51
 */

class MessageListener {

    @SubscribeEvent
    fun onMessage(event: ServerChatEvent) {
        val message: Component = event.message
        val player: ServerPlayer = event.player
        messageManager.sendToGroup(
            "${messageManager.server!!.name()}\n${player.name.string}: ${message.string}"
        )
    }
}