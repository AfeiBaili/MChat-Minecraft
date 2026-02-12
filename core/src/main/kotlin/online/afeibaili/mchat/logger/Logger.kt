package online.afeibaili.mchat.logger

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


/**
 * 日志器
 *
 *@author AfeiBaili
 *@version 2026/2/10 01:18
 */

interface Logger {
    val name: String
    fun info(message: Any)
    fun error(message: Any)
    fun warn(message: Any)
    fun print(level: String, message: Any) {
        scope.launch {
            println("[$level] ${getDataTime()}: $message")
        }
    }

    private fun getDataTime() = LocalDateTime.now().format(formatter)

    companion object {
        val scope = CoroutineScope(Dispatchers.Default)
        private val formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss.SSS")
        fun getLogger(name: Any): Logger {
            return object : Logger {
                override val name: String = name.toString()

                override fun info(message: Any) {
                    print("INFO", message)
                }

                override fun error(message: Any) {
                    print("ERROR", message)
                }

                override fun warn(message: Any) {
                    print("WARN", message)
                }
            }
        }
    }
}