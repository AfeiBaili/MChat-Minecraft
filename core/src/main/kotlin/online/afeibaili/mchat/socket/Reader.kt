package online.afeibaili.mchat.socket

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import online.afeibaili.mchat.logger.Logger
import online.afeibaili.mchat.socket.cipher.CipherProcessor
import java.io.Closeable
import java.net.Socket

/**
 * 读取器
 *
 *@author AfeiBaili
 *@version 2025/11/3 19:11
 */

class Reader(
    socket: Socket,
    val cipher: CipherProcessor,
    val action: (String) -> Unit,
    val catch: (Throwable) -> Unit,
) : Closeable {
    private val scope = CoroutineScope(Dispatchers.IO)
    private val reader = socket.inputStream.bufferedReader()
    private val logger = Logger.getLogger("MChatReader")
    var isClosed: Boolean = false

    init {
        scope.launch {
            runCatching {
                var line: String
                while (reader.readLine().also { line = it } != null) {
                    val message: String = cipher.decrypt(line)
                    action(message)
                    logger.info(message)
                }
                throw RuntimeException("MChat server disconnect")
            }.onFailure { exception ->
                if (!isClosed) catch(exception)
            }
        }
    }

    override fun close() {
        reader.close()
        scope.cancel()
    }
}