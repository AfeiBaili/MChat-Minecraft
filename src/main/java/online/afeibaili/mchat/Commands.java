package online.afeibaili.mchat;

public class Commands {
    public static void parsingMessage(String message) {
        if (message.equals("!online")) {
            Message.sendToGroup(Listener.getServerPlayers());
        }
    }
}
