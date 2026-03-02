package online.afeibaili.mchat

import net.minecraft.ChatFormatting
import net.minecraft.network.chat.Component
import net.minecraft.server.MinecraftServer
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.ModList
import net.neoforged.fml.common.Mod
import net.neoforged.fml.config.ModConfig
import net.neoforged.neoforge.common.ModConfigSpec
import net.neoforged.neoforge.event.ServerChatEvent
import net.neoforged.neoforge.event.entity.player.PlayerEvent
import net.neoforged.neoforge.event.server.ServerStartingEvent
import net.neoforged.neoforge.event.server.ServerStoppingEvent
import online.afeibaili.mchat.MChat.ID
import online.afeibaili.mchat.MChat.mchatSystem
import online.afeibaili.mchat.config.Config
import online.afeibaili.mchat.listener.MessageListener
import online.afeibaili.mchat.socket.message.MessageManager
import thedarkcolour.kotlinforforge.neoforge.forge.FORGE_BUS

@Mod(ID)
object MChat {
    const val ID = "mchat"

    init {
        FORGE_BUS.register(this)
        FORGE_BUS.register(Listener())
        ModList.get().getModContainerById(ID)?.get()
            ?.registerConfig(ModConfig.Type.SERVER, MChatModConfig.spec)
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
        mchatSystem.connect(ConfigLoader.config)
    }

    @SubscribeEvent
    fun onCommonSetup(event: ServerStoppingEvent) {
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
    val builder = ModConfigSpec.Builder()

    val address: ModConfigSpec.ConfigValue<String> =
        builder.comment("mchat server url address")
            .define("address", "localhost")

    val port: ModConfigSpec.ConfigValue<Int> =
        builder.comment("mchat server port")
            .define("port", 33393)

    val token: ModConfigSpec.ConfigValue<String> =
        builder.comment("mchat server token")
            .define("token", "c5c5d6ce-")

    val name: ModConfigSpec.ConfigValue<String> =
        builder.comment("mchat server name")
            .define("name", "未命名")

    val spec: ModConfigSpec = builder.build()
}