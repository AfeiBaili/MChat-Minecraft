package online.afeibaili.mchat;

import net.minecraft.util.Formatting;

import java.io.*;
import java.net.Socket;

import static online.afeibaili.mchat.MChat.LOGGER;

public class SocketHandle {
    public static Client client;

    public static void load() {
        if (client == null) {
            client = new Client("123.56.82.129", 33393);
        }
    }

    public static void unload() {
        try {
            if (client != null) {
                client.close();
                client = null;
            }
        } catch (Exception e) {
            LOGGER.info("客户端关闭时出错！");
        }
    }

    public static void send(String message) {
        if (client == null) {
            client = new Client("123.56.82.129", 33393);
        }
        if (client.socket == null || client.socket.isClosed()) {
            try {
                client.close();
            } catch (Exception ignored) {
            }
            client = new Client("123.56.82.129", 33393);
        }

        try {
            if (client.writer != null) {
                client.writer.write(message + "\n");
                client.writer.flush();
            }
        } catch (IOException e) {
            LOGGER.info("发送消息时出错！");
        }
    }

    public static class Client implements AutoCloseable {
        String message = "";
        Formatting style = Formatting.GRAY;
        Socket socket;

        BufferedWriter writer;

        public Client(String host, int port) {
            try {
                this.socket = new Socket(host, port);
                initReceiveMessage();
                initSendMessage();

                LOGGER.info("服务器开启成功！");
            } catch (IOException e) {
                LOGGER.info("连接到远程失败！");
            }
        }

        public void initReceiveMessage() {
            new Thread(() -> {
                try (
                        InputStream is = socket.getInputStream();
                        InputStreamReader isr = new InputStreamReader(is);
                        BufferedReader reader = new BufferedReader(isr)
                ) {
                    while ((message = reader.readLine()) != null) {
                        Commands.parsingMessage(message);
                        Message.sendToMC(message, style);
                    }
                    LOGGER.info("服务器端开了连接！");
                } catch (IOException e) {
                    LOGGER.info("建立连接失败！");
                }
            }, "reader").start();
        }

        public void initSendMessage() throws IOException {
            OutputStream os = socket.getOutputStream();
            OutputStreamWriter osw = new OutputStreamWriter(os);
            writer = new BufferedWriter(osw);
            writer.write("校验050516\n");
            writer.flush();
        }

        @Override
        public void close() throws Exception {
            if (socket != null) socket.close();
            if (writer != null) writer.close();
        }
    }
}
