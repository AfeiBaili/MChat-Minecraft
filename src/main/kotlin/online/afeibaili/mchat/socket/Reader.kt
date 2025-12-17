package online.afeibaili.mchat.socket

import kotlinx.coroutines.*
import online.afeibaili.mchat.MChatSystem
import online.afeibaili.mchat.socket.cipher.Cipher
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.Socket
import java.nio.charset.StandardCharsets
import java.util.concurrent.Executors


/**
 * 读取器
 *
 *@author AfeiBaili
 *@version 2025/11/3 19:11
 */

class Reader(val socket: Socket, cipher: Cipher, catch: () -> Unit) {
    val job: Job

    init {
        val readerDispatcher: ExecutorCoroutineDispatcher =
            Executors.newSingleThreadExecutor().asCoroutineDispatcher()

        job = MChatSystem.system.scope.launch(readerDispatcher) {
            runCatching {
                val reader = BufferedReader(InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8))
                reader.use { reader ->
                    while (isActive) {
                        val readLine: String = reader.readLine()
                        MChatSystem.system.messageManager.parseMessage(cipher.decrypt(readLine))
                    }
                }
            }.onFailure { catch.invoke() }
        }
    }

    fun close() {
        job.cancel()
    }
}