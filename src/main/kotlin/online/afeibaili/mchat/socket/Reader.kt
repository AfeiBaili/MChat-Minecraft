package online.afeibaili.mchat.socket

import kotlinx.coroutines.*
import net.minecraft.ChatFormatting
import online.afeibaili.mchat.MChat.messageManager
import online.afeibaili.mchat.MChat.scope
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

class Reader(val socket: Socket, catch: () -> Unit) {
    val job: Job
    val messageStyle = ChatFormatting.GRAY

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
                            messageManager.sendToMC(readLine, messageStyle)
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