package online.afeibaili.mchat

import net.minecraft.ChatFormatting
import net.minecraft.network.chat.Component
import net.minecraft.server.MinecraftServer
import net.minecraftforge.common.ForgeConfigSpec
import net.minecraftforge.event.server.ServerStartingEvent
import net.minecraftforge.event.server.ServerStoppingEvent
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.config.ModConfig
import online.afeibaili.mchat.config.Config
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

                override fun sendImageToMC(message: String, formatting: ChatFormatting) {
                    //todo
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