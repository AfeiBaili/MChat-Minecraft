package online.afeibaili.mchat.listener


/**
 * 消息监听器
 *
 *@author AfeiBaili
 *@version 2025/11/3 13:51
 */

interface MessageListener<Event> {
    fun onMessage(event: Event)
}