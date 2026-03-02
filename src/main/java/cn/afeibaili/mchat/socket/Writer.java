package cn.afeibaili.mchat.socket;

import cn.afeibaili.mchat.socket.cipher.CipherProcessor;

import java.io.Closeable;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * 写出器类
 *
 * @author AfeiBaili
 * @version 2026/3/3 01:12
 */

public class Writer implements Closeable {
    Socket socket;
    CipherProcessor cipher;

    PrintWriter writer;

    public Writer(Socket socket, CipherProcessor cipher) {
        this.socket = socket;
        this.cipher = cipher;

        try {
            writer = new PrintWriter(socket.getOutputStream(), true);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void write(Object message) {
        writer.println(cipher.encrypt(message.toString()));
    }

    @Override
    public void close() {
        writer.close();
    }
}
