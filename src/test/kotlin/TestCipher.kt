import kotlinx.coroutines.*
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.ServerSocket
import java.net.Socket
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import kotlin.test.Test

/**
 * 测试加密密码
 *
 *@author AfeiBaili
 *@version 2025/11/3 14:46
 */

class TestCipher {
    @Test
    fun test1() {
        val executor: ExecutorService = Executors.newSingleThreadExecutor()
        val asCoroutineDispatcher: ExecutorCoroutineDispatcher = executor.asCoroutineDispatcher()

        var socket: Socket? = null
        runBlocking(Dispatchers.Default) {
            val job: Job = launch(asCoroutineDispatcher) {
                val s: ServerSocket = ServerSocket(33393)
                socket = s.accept()
            }

            launch {
                val socket: Socket = Socket("localhost", 33393)
                socket.getInputStream()
                val bufferedReader: BufferedReader = BufferedReader(InputStreamReader(socket.getInputStream()))
                println("开始阻塞")
                bufferedReader.readLine()
            }

            launch(Dispatchers.IO) {
                delay(2000)
                println("关闭")
                socket!!.close()
            }
        }
    }
}