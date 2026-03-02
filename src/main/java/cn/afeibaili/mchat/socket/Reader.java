package cn.afeibaili.mchat.socket;

import cn.afeibaili.mchat.socket.cipher.CipherProcessor;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.function.Consumer;

/**
 * 读取器类
 *
 * @author AfeiBaili
 * @version 2026/3/3 00:26
 */

public class Reader implements Closeable {
    Socket socket;
    CipherProcessor cipher;
    Consumer<String> action;
    Consumer<Exception> cat;

    Thread readThread;
    BufferedReader reader;

    public Reader(Socket socket, CipherProcessor cipher, Consumer<String> action, Consumer<Exception> cat) {
        this.socket = socket;
        this.cipher = cipher;
        this.action = action;
        this.cat = cat;

        try {
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        readThread = new Thread(() -> {
            try {
                String line;
                while (!Thread.currentThread().isInterrupted() && (line = reader.readLine()) != null) {
                    action.accept(cipher.decrypt(line));
                }
                throw new RuntimeException("MChat server disconnect");
            } catch (Exception e) {
                e.printStackTrace();
                cat.accept(e);
            }
        });
        readThread.start();
    }

    @Override
    public void close() {
        try {
            reader.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        readThread.interrupt();
    }
}