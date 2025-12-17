package online.afeibaili.mchat

import kotlinx.coroutines.CoroutineScope
import online.afeibaili.mchat.config.Config
import online.afeibaili.mchat.socket.SocketManager
import online.afeibaili.mchat.socket.message.MessageManager
import org.slf4j.Logger


/**
 * 接口文件，须实现
 *
 *@author AfeiBaili
 *@version 2025/12/17 13:51
 */


object MChatSystem {
    lateinit var system: MChatInterface

    init {
        if (::system.isInitialized) error("未初始化系统")
    }
}

interface MChatInterface {
    val scope: CoroutineScope
    var messageManager: MessageManager
    val logger: Logger
    val config: Config
    val socketManager: SocketManager

    companion object {
        init {
            Runtime.getRuntime().addShutdownHook(
                Thread({
                    MChatSystem.system.socketManager.disconnect()
                    MChatSystem.system.logger.info("已断开MChat连接")
                }, "Shutdown")
            )
        }
    }
}
