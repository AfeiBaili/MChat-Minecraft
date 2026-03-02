package cn.afeibaili.mchat.socket.message;

/**
 * 消息类型
 *
 * @author AfeiBaili
 * @version 2026/3/3 00:36
 */

public abstract class MessageType {
    abstract String getIdentifier();

    abstract String getMessage();

    char delimiter = ':';

    @Override
    public String toString() {
        return getIdentifier() + delimiter + getMessage();
    }

    static class Text extends MessageType {
        String message;

        public Text(String message) {
            this.message = message;
        }

        @Override
        String getIdentifier() {
            return "txt";
        }

        @Override
        String getMessage() {
            return message;
        }
    }

    static class Command extends MessageType {
        String message;

        public Command(String message) {
            this.message = message;
        }

        @Override
        String getIdentifier() {
            return "cmd";
        }

        @Override
        String getMessage() {
            return message;
        }
    }

    static class Heartbeat extends MessageType {
        String message;

        public Heartbeat(String message) {
            this.message = message;
        }

        @Override
        String getIdentifier() {
            return "het";
        }

        @Override
        String getMessage() {
            return message;
        }
    }
}
