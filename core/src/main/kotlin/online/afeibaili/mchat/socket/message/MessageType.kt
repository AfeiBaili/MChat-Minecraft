package online.afeibaili.mchat.socket.message


/**
 * 消息类型
 *
 *@author AfeiBaili
 *@version 2025/11/3 18:39
 */

sealed class MessageType(val identifier: String) {
    abstract val message: String
    val delimiter: Char = ':'
    override fun toString(): String = "$identifier$delimiter$message"

    class Text(override val message: String) : MessageType("txt")
    class Command(override val message: String) : MessageType("cmd")
    class Heartbeat(override val message: String) : MessageType("het")
    class Image(override val message: String) : MessageType("img")
}