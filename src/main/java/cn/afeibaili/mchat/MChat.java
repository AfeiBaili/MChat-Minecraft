package cn.afeibaili.mchat;

import cn.afeibaili.mchat.config.Config;
import cn.afeibaili.mchat.socket.message.MessageManager;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.event.FMLServerStoppingEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;

import java.io.File;

import static cn.afeibaili.mchat.MChat.mchatSystem;

@Mod(
        modid = MChat.MODID,
        name = MChat.NAME,
        version = MChat.VERSION,
        acceptableRemoteVersions = "*"
)
public class MChat {
    public static final String MODID = "mchat";
    public static final String NAME = "MChat";
    public static final String VERSION = "1.0";

    public static MChatSystem mchatSystem = new MChatSystem();

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        MChatModConfig.load(event.getSuggestedConfigurationFile());
        MinecraftForge.EVENT_BUS.register(this);
        MinecraftForge.EVENT_BUS.register(new Listener());
    }

    @EventHandler
    public void onCommonSetup(FMLServerStartingEvent event) {
        MessageManager<TextFormatting, MinecraftServer> messageManager = new MessageManager<TextFormatting, MinecraftServer>() {
            @Override
            public TextFormatting getFormat() {
                return TextFormatting.GRAY;
            }

            @Override
            public void sendToMc(String message, TextFormatting format) {
                event.getServer().getPlayerList().getPlayers().forEach(player -> {
                    player.sendMessage(new TextComponentString(message).setStyle(new Style().setColor(format)));
                });
            }
        };

        mchatSystem.buildManager(messageManager);
        mchatSystem.connect(ConfigLoader.config);
    }

    @EventHandler
    public void onCommonStop(FMLServerStoppingEvent event) {
        MChatSystem.stop();
    }
}


//class Listener implements MessageListener<ServerChatEvent, PlayerEvent.PlayerLoggedInEvent, PlayerEvent.PlayerLoggedOutEvent> {
class Listener {
    String name = ConfigLoader.config.getName();

    @SubscribeEvent
    public void onMessage(ServerChatEvent event) {
        mchatSystem.getMessageManager()
                .sendToGroup("[" + name + "] " + event.getPlayer().getName() + ": " + event.getMessage());
    }

    @SubscribeEvent
    public void onPlayerIn(PlayerEvent.PlayerLoggedInEvent event) {
        mchatSystem.getMessageManager()
                .sendLoginInOut(event.player.getName() + "加入了" + name + "服务器");
    }

    @SubscribeEvent
    public void onPlayerOut(PlayerEvent.PlayerLoggedOutEvent event) {
        mchatSystem.getMessageManager()
                .sendLoginInOut(event.player.getName() + "退出了" + name + "服务器");
    }
}

class ConfigLoader {
    public static final Config config = new Config(
            MChatModConfig.token,
            MChatModConfig.address,
            MChatModConfig.port,
            MChatModConfig.name
    );
}

class MChatModConfig {
    public static String address;
    public static int port;
    public static String token;
    public static String name;

    public static void load(File file) {
        Configuration configuration = new Configuration(file);
        configuration.load();

        address = configuration.getString("address", "general", "192.168.1.8", "server address");
        port = configuration.getInt("port", "general", 33393, 1, 65535, "server port");
        token = configuration.getString("token", "general", "c5c5d6ce-", "server token");
        name = configuration.getString("name", "general", "未命名", "server name");

        if (configuration.hasChanged()) {
            configuration.save();
        }
    }
}