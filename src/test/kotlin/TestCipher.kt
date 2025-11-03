import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.net.ServerSocket
import java.net.Socket
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
        val serverSocket: ServerSocket = ServerSocket(33333)
        serverSocket.soTimeout = 5000
        runBlocking {
            val accept: Socket = serverSocket.accept()
            println(accept)
            delay(6000)
        }
    }
}