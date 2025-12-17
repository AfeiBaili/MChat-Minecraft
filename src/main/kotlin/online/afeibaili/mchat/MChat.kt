package online.afeibaili.mchat

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import net.minecraft.network.chat.Component
import net.minecraft.server.level.ServerPlayer
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.ModList
import net.neoforged.fml.common.Mod
import net.neoforged.fml.config.ModConfig
import net.neoforged.neoforge.common.ModConfigSpec
import net.neoforged.neoforge.common.NeoForge
import net.neoforged.neoforge.event.ServerChatEvent
import net.neoforged.neoforge.event.server.ServerStartingEvent
import online.afeibaili.mchat.config.Config
import online.afeibaili.mchat.listener.MessageListener
import online.afeibaili.mchat.socket.SocketManager
import online.afeibaili.mchat.socket.message.MessageManager
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.*

/**
 * Main mod class.
 *
 * An example for blocks is in the `blocks` package of this mod.
 */
@Mod(MChat.ID)
object MChat {
    const val ID = "mchat"

    init {
        val builder: ModConfigSpec.Builder = ModConfigSpec.Builder()
        val address = builder
            .comment("服务器地址")
            .define("address", "null")
        val port = builder
            .comment("服务器端口")
            .define("port", 33393)
        val token = builder
            .comment("通讯密码：服务端必须和客户端一致")
            .define("token", UUID.randomUUID().toString().take(10))
        val spec: ModConfigSpec = builder.build()

        ModList.get().getModContainerById(ID).get().registerConfig(ModConfig.Type.COMMON, spec)

        MChatSystem.system = object : MChatInterface {
            override val scope: CoroutineScope = CoroutineScope(Dispatchers.IO)
            override var messageManager: MessageManager = MessageManager(null)
            override val logger: Logger = LoggerFactory.getLogger("MChat")
            override val config: Config by lazy { Config(token.get(), address.get(), port.get()) }
            override val socketManager: SocketManager by lazy {
                SocketManager(
                    config.address,
                    config.port,
                    config.token
                )
            }
        }

        NeoForge.EVENT_BUS.register(this)
        NeoForge.EVENT_BUS.register(object : MessageListener<ServerChatEvent> {
            @SubscribeEvent
            override fun onMessage(event: ServerChatEvent) {
                val message: Component = event.message
                val player: ServerPlayer = event.player
                MChatSystem.system.messageManager.sendToGroup(
                    "${player.name.string}: ${message.string}"
                )
            }
        })
    }

    @SubscribeEvent
    fun onServerSetup(event: ServerStartingEvent) {
        MChatSystem.system.messageManager.server = event.server
        MChatSystem.system.messageManager.sendHeartbeatToGroup()
        MChatSystem.system.logger.info("MChat已启动")
    }
}