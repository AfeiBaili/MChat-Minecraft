package online.afeibaili.mchat

import net.minecraftforge.event.ServerChatEvent
import net.minecraftforge.event.entity.player.PlayerEvent
import net.minecraftforge.eventbus.api.SubscribeEvent
import online.afeibaili.mchat.MChat.Companion.mchatSystem
import online.afeibaili.mchat.listener.MessageListener

class Listener : MessageListener<ServerChatEvent, PlayerEvent.PlayerLoggedInEvent, PlayerEvent.PlayerLoggedOutEvent> {
    @SubscribeEvent
    override fun onMessage(event: ServerChatEvent) {
        mchatSystem.getMessageManager()
            .sendToGroup("[${ConfigLoader.config.name}] " + event.player.name.string + ": " + event.message.string)
    }

    @SubscribeEvent
    override fun onPlayerIn(event: PlayerEvent.PlayerLoggedInEvent) {
        mchatSystem.getMessageManager()
            .sendLoginInOut("${event.entity.name.string}加入了${ConfigLoader.config.name}服务器")
    }

    @SubscribeEvent
    override fun onPlayerOut(event: PlayerEvent.PlayerLoggedOutEvent) {
        mchatSystem.getMessageManager()
            .sendLoginInOut("${event.entity.name.string}退出了${ConfigLoader.config.name}服务器")
    }
}