package online.afeibaili.mchat.socket

import online.afeibaili.mchat.socket.cipher.CipherProcessor
import java.io.Closeable
import java.io.PrintWriter
import java.net.Socket


/**
 * 输出器
 *
 *@author AfeiBaili
 *@version 2026/2/15 12:12
 */

class Writer(socket: Socket, val cipher: CipherProcessor) : Closeable {
    private val writer = PrintWriter(socket.outputStream, true)

    fun write(message: Any) {
        writer.println(cipher.encrypt(message.toString()))
    }

    override fun close() {
        writer.close()
    }
}