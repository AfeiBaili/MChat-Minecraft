package online.afeibaili.mchat

import net.minecraft.ChatFormatting
import net.minecraft.network.chat.Component
import net.minecraft.server.MinecraftServer
import net.minecraftforge.common.ForgeConfigSpec
import net.minecraftforge.event.ServerChatEvent
import net.minecraftforge.event.entity.player.PlayerEvent
import net.minecraftforge.event.server.ServerStartingEvent
import net.minecraftforge.event.server.ServerStoppingEvent
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.config.ModConfig
import online.afeibaili.mchat.MChat.Companion.mchatSystem
import online.afeibaili.mchat.config.Config
import online.afeibaili.mchat.listener.MessageListener
import online.afeibaili.mchat.socket.message.MessageManager
import thedarkcolour.kotlinforforge.forge.FORGE_BUS
import thedarkcolour.kotlinforforge.forge.registerConfig

@Mod(MChat.ID)
class MChat {
    companion object {
        const val ID = "mchat"
        val mchatSystem: MChatSystem = MChatSystem()
    }

    init {
        FORGE_BUS.register(this)
        FORGE_BUS.register(Listener())
        registerConfig(ModConfig.Type.COMMON, MChatModConfig.spec)
    }

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
        mchatSystem.connect(ConfigLoader.config)
    }

    @SubscribeEvent
    fun onCommonStop(event: ServerStoppingEvent) {
        MChatSystem.close()
    }
}

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

object ConfigLoader {
    val config = Config(
        MChatModConfig.token.get(),
        MChatModConfig.address.get(),
        MChatModConfig.port.get(),
        MChatModConfig.name.get()
    )
}

object MChatModConfig {
    val builder = ForgeConfigSpec.Builder()

    val address: ForgeConfigSpec.ConfigValue<String> =
        builder.comment("mchat server url address")
            .define("address", "mc.afeibaili.cn")

    val port: ForgeConfigSpec.ConfigValue<Int> =
        builder.comment("mchat server port")
            .define("port", 33393)

    val token: ForgeConfigSpec.ConfigValue<String> =
        builder.comment("mchat server token")
            .define("token", "c5c5d6ce-")

    val name: ForgeConfigSpec.ConfigValue<String> =
        builder.comment("mchat server name")
            .define("name", "未命名")

    val spec: ForgeConfigSpec = builder.build()
}