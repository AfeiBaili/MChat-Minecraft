package online.afeibaili.mchat.socket

import kotlinx.coroutines.*
import online.afeibaili.mchat.MChatSystem.Companion.INSTANCE
import online.afeibaili.mchat.config.Config
import online.afeibaili.mchat.logger.Logger
import online.afeibaili.mchat.socket.cipher.CipherProcessor
import online.afeibaili.mchat.socket.message.MessageType
import java.io.Closeable
import java.net.Socket


/**
 * 管理Socket连接
 *
 *@author AfeiBaili
 *@version 2025/11/3 16:00
 */

open class SocketManager : Closeable {
    lateinit var socket: Socket
    lateinit var writer: Writer
    lateinit var reader: Reader
    lateinit var cipher: CipherProcessor
    lateinit var heartbeat: Heartbeat
    val reconnectScope = CoroutineScope(Dispatchers.IO)
    var reconnectJob: Job? = null
    var isConnectable = true

    private val logger = Logger.getLogger("SocketManager")

    fun connect(config: Config) = if (isConnectable) {
        isConnectable = false
        cipher = CipherProcessor(config.token)
        runCatching {
            socket = Socket(config.address, config.port)
            writer = Writer(socket, cipher)
            reader = Reader(socket, cipher, { message ->
                INSTANCE.getMessageManager().sendFormattingMessageToMC(message)
            }) { reconnect(config, "远程断开连接") }
            heartbeat = Heartbeat({ INSTANCE.getMessageManager().sendHeartbeat() })
            logger.info("已连接MChat服务器")
        }.onFailure { e ->
            reconnect(config, "无法连接")
        }
    } else {
        reconnect(config, "不是首次连接")
    }

    fun reconnect(config: Config, errorMessage: String) {
        reconnectJob?.cancel()
        reconnectJob = reconnectScope.launch {
            logger.error("连接至服务器失败：\"${errorMessage}\"，10秒后进行重连...")
            delay(10000)
            runCatching {
                close()
                yield()
                isConnectable = true
                connect(config)
            }
        }
    }

    override fun close() {
        if (isConnectable) return
        socket.close()
        reader.close()
        writer.close()
        heartbeat.close()
        logger.info("已关闭MChatSystem")
    }

    fun send(message: MessageType) {
        writer.write(message)
    }
}