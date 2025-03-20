package online.afeibaili.mchat;

import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MChat implements ModInitializer {
    public static final String MOD_ID = "mchat";

    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        Listener.load();
    }
}
