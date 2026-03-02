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
                INSTANCE.getMessageManager().parseMessage(message)
            }) { reconnect(config, "Disconnect remotely") }
            heartbeat = Heartbeat({ INSTANCE.getMessageManager().sendHeartbeat() })
            logger.info("MChat server is connected")
        }.onFailure { e ->
            e.printStackTrace()
            reconnect(config, "Unable to connect")
        }
    } else {
        logger.info("MChat server is connected")
    }

    fun reconnect(config: Config, errorMessage: String) {
        reconnectJob?.cancel()
        reconnectJob = reconnectScope.launch {
            logger.error("Failed to connect to the server:\"${errorMessage}\", Reconnect after 10 seconds...")
            delay(10000)
            runCatching {
                clean()
                yield()
                isConnectable = true
                connect(config)
            }
        }
    }

    fun clean() {
        if (isConnectable) return
        if (::socket.isInitialized) socket.close()
        if (::reader.isInitialized) reader.close()
        if (::writer.isInitialized) writer.close()
        if (::heartbeat.isInitialized) reader.close()
        logger.info("MChatSystem Closed")
        isConnectable = true
    }

    override fun close() {
        clean()
        isConnectable = false
    }

    fun send(message: MessageType) {
        if (::socket.isInitialized && socket.isConnected) {
            writer.write(message)
        }
    }
}