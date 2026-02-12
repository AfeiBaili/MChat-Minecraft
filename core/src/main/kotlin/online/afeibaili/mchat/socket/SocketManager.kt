package online.afeibaili.mchat.socket

import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import online.afeibaili.mchat.MChatSystem.Companion.INSTANCE
import online.afeibaili.mchat.socket.cipher.CipherProcessor
import online.afeibaili.mchat.socket.message.MessageType
import java.io.PrintWriter
import java.net.InetSocketAddress
import java.net.Socket
import java.nio.charset.StandardCharsets


/**
 * 管理Socket连接
 *
 *@author AfeiBaili
 *@version 2025/11/3 16:00
 */

class SocketManager(val address: String, val port: Int, token: String) {
    var socket = Socket()
    lateinit var writer: PrintWriter
    lateinit var heartbeatJob: Job
    lateinit var reader: Reader
    val cipher = CipherProcessor(token)

    init {
        connect()
    }

    private fun connect() {
        runCatching {
            socket.connect(InetSocketAddress(address, port))
            writer = PrintWriter(socket.getOutputStream(), true, StandardCharsets.UTF_8)
            //创建心跳
            heartbeatJob = Heartbeat({
                send(MessageType.Heartbeat(""))
            }).job
            reader = Reader(socket, cipher) { RuntimeException("读取器异常") }
            INSTANCE.logger.info("连接成功")
            INSTANCE.messageManager.sendFormattingMessageToMC("已连接至服务器。")
        }.onFailure { e ->
            reconnect(RuntimeException("无法连接服务器"))
        }
    }

    fun disconnect() {
        runCatching {
            heartbeatJob.cancel()
            writer.close()
            socket.close()
            reader.close()
        }
    }

    var reconnectJob: Job? = null
    fun reconnect(e: Throwable) {
        runCatching {
            reconnectJob?.cancel()
            reconnectJob = INSTANCE.scope.launch {
                INSTANCE.logger.error("连接至服务器失败：\"${e.message}\"，10秒后进行重连...")
                delay(10000)
                if (::heartbeatJob.isInitialized)
                    heartbeatJob.cancel()
                if (::writer.isInitialized)
                    writer.close()
                socket.close()
                socket = Socket()
                if (::reader.isInitialized)
                    reader.close()
                connect()
            }
        }
    }

    fun send(message: MessageType) {
        val encrypt: String = cipher.encrypt(message.toString())
        runCatching {
            if (socket.isClosed) throw RuntimeException("套接字已断开连接。")
            writer.println(encrypt)
        }.onFailure { e ->
            INSTANCE.messageManager.sendFormattingMessageToMC("发送消息失败，正在重新连接。${e.message}")
            reconnect(e)
        }
    }
}