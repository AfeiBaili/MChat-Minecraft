package online.afeibaili.mchat.socket

import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import online.afeibaili.mchat.MChat

/**
 * 心跳类
 *
 *@author AfeiBaili
 *@version 2025/11/3 18:46
 */

class Heartbeat(action: () -> Unit, catch: (e: Throwable) -> Unit = {}) {
    val job: Job = MChat.scope.launch {
        runCatching {
            while (isActive) {
                action.invoke()
                delay(60000 * 3)
            }
        }.onFailure { exception ->
            catch.invoke(exception)
        }
    }
}