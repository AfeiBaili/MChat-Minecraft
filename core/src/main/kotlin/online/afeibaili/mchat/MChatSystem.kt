package online.afeibaili.mchat

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import online.afeibaili.mchat.config.Config
import online.afeibaili.mchat.logger.Logger
import online.afeibaili.mchat.socket.SocketManager
import online.afeibaili.mchat.socket.message.MessageManager


/**
 * 接口文件，须实现
 *
 *@author AfeiBaili
 *@version 2025/12/17 13:51
 */


class MChatSystem(
    val config: Config,
    val socketManager: SocketManager = SocketManager(config.address, config.port, config.token),
    val logger: Logger = Logger.getLogger("MChatSystem"),
    val scope: CoroutineScope = CoroutineScope(Dispatchers.Default),
) {
    lateinit var messageManager: MessageManager<*, *>

    init {
        INSTANCE = this
    }

    companion object {
        lateinit var INSTANCE: MChatSystem

        init {
            Runtime.getRuntime().addShutdownHook(
                Thread({
                    close()
                }, "Shutdown")
            )
        }

        fun close() {
            INSTANCE.scope.cancel()
            INSTANCE.socketManager.disconnect()
            INSTANCE.logger.info("已断开MChat连接")
        }
    }
}
