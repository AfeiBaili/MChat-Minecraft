package online.afeibaili.mchat

import net.minecraft.ChatFormatting
import net.minecraft.network.chat.Component
import net.minecraft.server.MinecraftServer
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.common.Mod
import net.neoforged.neoforge.event.ServerChatEvent
import net.neoforged.neoforge.event.server.ServerStartingEvent
import net.neoforged.neoforge.event.server.ServerStoppingEvent
import online.afeibaili.mchat.MChat.mchatSystem
import online.afeibaili.mchat.config.Config
import online.afeibaili.mchat.listener.MessageListener
import online.afeibaili.mchat.logger.Logger
import online.afeibaili.mchat.socket.message.MessageManager
import thedarkcolour.kotlinforforge.neoforge.forge.FORGE_BUS

@Mod(MChat.ID)
object MChat {
    const val ID = "mchat"

    init {
        FORGE_BUS.register(this)
        FORGE_BUS.register(NMessageListener())
    }

    val logger = Logger.getLogger("MChat")

    lateinit var mchatSystem: MChatSystem

    @SubscribeEvent
    fun onCommonSetup(event: ServerStartingEvent) {
        mchatSystem = MChatSystem(Config("c5c5d6ce-", "u", 33393))
        mchatSystem.messageManager = object : MessageManager<ChatFormatting, MinecraftServer>() {
            override var formatting: ChatFormatting = ChatFormatting.GRAY
            override fun sendToMC(message: String, formatting: ChatFormatting) {
                event.server.playerList.players.forEach { player ->
                    player.sendSystemMessage(Component.literal(message).withStyle(formatting))
                }
            }
        }
    }

    @SubscribeEvent
    fun onCommonSetup(event: ServerStoppingEvent) {
        if (::mchatSystem.isInitialized) {
            logger.info("关闭MChatSystem")
            MChatSystem.close()
        }
    }
}

class NMessageListener : MessageListener<ServerChatEvent> {
    @SubscribeEvent
    override fun onMessage(event: ServerChatEvent) {
        mchatSystem.messageManager.sendToGroup(event.player.name.string + ": " + event.message.string)
    }
}