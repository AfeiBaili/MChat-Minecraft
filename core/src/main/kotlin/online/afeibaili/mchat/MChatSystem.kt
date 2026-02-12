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
    config: Config,
) {
    init {
        INSTANCE = this
    }

    val scope: CoroutineScope = CoroutineScope(Dispatchers.Default)
    val logger: Logger = Logger.getLogger("MChatSystem")
    val socketManager: SocketManager = SocketManager(config.address, config.port, config.token)
    lateinit var messageManager: MessageManager<*, *>

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
