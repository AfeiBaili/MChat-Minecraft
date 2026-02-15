package online.afeibaili.mchat.socket

import kotlinx.coroutines.*
import java.io.Closeable

/**
 * 心跳类
 *
 *@author AfeiBaili
 *@version 2025/11/3 18:46
 */

class Heartbeat(action: () -> Unit, catch: (exception: Throwable) -> Unit = {}) :
    Closeable {
    private val scope = CoroutineScope(Dispatchers.IO)

    init {
        scope.launch {
            runCatching {
                while (isActive) {
                    action()
                    delay(60000 * 3)
                }
            }.onFailure { exception ->
                catch(exception)
            }
        }
    }

    override fun close() {
        scope.cancel()
    }
}