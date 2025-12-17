package online.afeibaili.mchat

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.ModList
import net.neoforged.fml.common.Mod
import net.neoforged.fml.config.ModConfig
import net.neoforged.neoforge.common.NeoForge
import net.neoforged.neoforge.event.server.ServerStartingEvent
import online.afeibaili.mchat.config.ConfigManager
import online.afeibaili.mchat.listener.MessageListener
import online.afeibaili.mchat.socket.SocketManager
import online.afeibaili.mchat.socket.message.MessageManager
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

/**
 * Main mod class.
 *
 * An example for blocks is in the `blocks` package of this mod.
 */
@Mod(MChat.ID)
object MChat {
    //需要更新组件化
    const val ID = "mchat"

    val scope = CoroutineScope(Dispatchers.IO)
    var messageManager: MessageManager = MessageManager(null)
    val logger: Logger by lazy { LogManager.getLogger(ID) }
    val config by lazy { ConfigManager.loadConfig() }
    val socketManager by lazy { SocketManager(config.address, config.port, config.token) }

    init {
        NeoForge.EVENT_BUS.register(this)
        NeoForge.EVENT_BUS.register(MessageListener())

        ModList.get().getModContainerById(ID).get()
            .registerConfig(ModConfig.Type.COMMON, ConfigManager.spec)

        Runtime.getRuntime().addShutdownHook(
            Thread({
                socketManager.disconnect()
                logger.info("已断开MChat连接")
            }, "Shutdown")
        )
    }

    @SubscribeEvent
    fun onServerSetup(event: ServerStartingEvent) {
        messageManager.server = event.server
        messageManager.sendHeartbeatToGroup()
        logger.info("MChat已启动")
    }
}