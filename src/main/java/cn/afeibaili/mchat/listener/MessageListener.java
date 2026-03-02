package cn.afeibaili.mchat.listener;


/**
 * 监听器接口
 *
 * @author AfeiBaili
 * @version 2026/3/3 00:11
 */

public interface MessageListener<MessageEvent, PlayerIn, PlayerOut> {
    void onMessage(MessageEvent event);

    void onPlayerIn(PlayerIn event);

    void onPlayerOut(PlayerOut event);
}
