package cn.afeibaili.mchat.socket;

import java.io.Closeable;
import java.util.function.Consumer;

/**
 * 心跳
 *
 * @author AfeiBaili
 * @version 2026/3/3 01:16
 */

public class Heartbeat implements Closeable {
    Runnable action;
    Consumer<Exception> cat;

    Thread heartbeatThread;

    public Heartbeat(Runnable action, Consumer<Exception> cat) {
        this.action = action;
        this.cat = cat;

        heartbeatThread = new Thread(() -> {
            try {
                while (!Thread.currentThread().isInterrupted()) {
                    action.run();
                    Thread.sleep(60000 * 3);
                }
            } catch (Exception e) {
                cat.accept(e);
            }
        });
        heartbeatThread.start();
    }

    @Override
    public void close() {
        heartbeatThread.interrupt();
    }
}
