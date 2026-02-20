package online.afeibaili.mchat

import net.minecraft.ChatFormatting
import net.minecraft.network.chat.Component
import net.minecraft.server.MinecraftServer
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.common.Mod
import net.neoforged.neoforge.event.ServerChatEvent
import net.neoforged.neoforge.event.entity.player.PlayerEvent
import net.neoforged.neoforge.event.server.ServerStartingEvent
import net.neoforged.neoforge.event.server.ServerStoppingEvent
import online.afeibaili.mchat.MChat.mchatSystem
import online.afeibaili.mchat.config.Config
import online.afeibaili.mchat.listener.MessageListener
import online.afeibaili.mchat.socket.message.MessageManager
import thedarkcolour.kotlinforforge.neoforge.forge.FORGE_BUS

@Mod(MChat.ID)
object MChat {
    const val ID = "mchat"

    init {
        FORGE_BUS.register(this)
        FORGE_BUS.register(Listener())
    }

    val mchatSystem: MChatSystem = MChatSystem()

    @SubscribeEvent
    fun onCommonSetup(event: ServerStartingEvent) {
        val manager: MessageManager<ChatFormatting, MinecraftServer> =
            object : MessageManager<ChatFormatting, MinecraftServer>() {
                override var formatting: ChatFormatting = ChatFormatting.GRAY
                override fun sendToMC(message: String, formatting: ChatFormatting) {
                    event.server.playerList.players.forEach { player ->
                        player.sendSystemMessage(Component.literal(message).withStyle(formatting))
                    }
                }
            }


        mchatSystem.buildManager(manager)
        mchatSystem.connect(Config("c5c5d6ce-", "afeibaili.cn", 33393))
    }

    @SubscribeEvent
    fun onCommonSetup(event: ServerStoppingEvent) {
        MChatSystem.close()
    }
}

class Listener : MessageListener<ServerChatEvent, PlayerEvent.PlayerLoggedInEvent> {
    @SubscribeEvent
    override fun onMessage(event: ServerChatEvent) {
        mchatSystem.getMessageManager().sendToGroup(event.player.name.string + ": " + event.message.string)
    }

    @SubscribeEvent
    override fun onPlayerIn(event: PlayerEvent.PlayerLoggedInEvent) {

    }
}