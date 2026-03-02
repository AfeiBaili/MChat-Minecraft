package cn.afeibaili.mchat.socket.message;

import cn.afeibaili.mchat.MChatSystem;
import cn.afeibaili.mchat.config.Config;
import cn.afeibaili.mchat.logger.Logger;
import cn.afeibaili.mchat.socket.Heartbeat;
import cn.afeibaili.mchat.socket.Reader;
import cn.afeibaili.mchat.socket.Writer;
import cn.afeibaili.mchat.socket.cipher.CipherProcessor;

import java.io.Closeable;
import java.io.IOException;
import java.net.Socket;

/**
 * socket管理
 *
 * @author AfeiBaili
 * @version 2026/3/3 01:24
 */

public class SocketManager implements Closeable {
    Socket socket;
    Writer writer;
    Reader reader;
    CipherProcessor cipher;
    Heartbeat heartbeat;
    boolean isConnectable = true;
    Thread reconnectThread;

    Logger logger = Logger.getLogger();

    {
        logger.setName("SocketManager");
    }

    public void connect(Config config) {
        if (!isConnectable) {
            logger.info("MChat server is connected or closed");
            return;
        }
        isConnectable = false;
        cipher = new CipherProcessor(config.getToken());
        try {
            socket = new Socket(config.getAddress(), config.getPort());
            writer = new Writer(socket, cipher);
            reader = new Reader(socket, cipher, message -> {
                MChatSystem.INSTANCE.getMessageManager().parseMessage(message);
            }, exception -> {
                reconnect(config, "Disconnect remotely");
            });
            heartbeat = new Heartbeat(() -> {
            }, (e) -> {
            });
        } catch (Exception e) {
            e.printStackTrace();
            reconnect(config, "Unable to connect");
        }
    }

    void reconnect(Config config, String errorMessage) {
        if (reconnectThread != null) {
            reconnectThread.interrupt();
        }
        reconnectThread = new Thread(() -> {
            logger.error("Failed to connect to the server:\"" + errorMessage + "\", Reconnect after 10 seconds...");
            try {
                Thread.sleep(10000);
                clean();
                Thread.yield();
                isConnectable = true;
                connect(config);
            } catch (Exception ignored) {
            }
        });
        reconnectThread.start();
    }

    void clean() {
        if (isConnectable) return;
        if (socket != null) {
            try {
                socket.close();
            } catch (IOException ignored) {
            }
        }
        if (reader != null) reader.close();
        if (writer != null) writer.close();
        if (heartbeat != null) heartbeat.close();
        logger.info("MChat server is closed");
        isConnectable = true;
    }

    @Override
    public void close() {
        clean();
        isConnectable = false;
    }

    public void send(MessageType message) {
        if (socket != null && socket.isConnected()) {
            writer.write(message);
        }
    }
}
