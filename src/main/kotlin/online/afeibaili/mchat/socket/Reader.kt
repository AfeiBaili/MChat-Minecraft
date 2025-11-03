package online.afeibaili.mchat.socket

import kotlinx.coroutines.*
import online.afeibaili.mchat.MChat.messageManager
import online.afeibaili.mchat.MChat.scope
import online.afeibaili.mchat.socket.cipher.Cipher
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.Socket
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

        val reader = BufferedReader(InputStreamReader(socket.getInputStream()))
        job = scope.launch(readerDispatcher) {
            runCatching {
                reader.use { reader ->
                    while (isActive) {
                        val readLine: String? = reader.readLine()
                        if (readLine != null) {
                            messageManager.parseMessage(cipher.decrypt(readLine))
                        }
                    }
                }
            }.onFailure { catch.invoke() }
        }
    }

    fun close() = {
        job.cancel()
    }
}