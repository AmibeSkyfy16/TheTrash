package ch.skyfy.thetrash;

import ch.skyfy.thetrash.config.core.BetterConfig;
import net.fabricmc.api.ModInitializer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class TheTrash implements ModInitializer {

    public static final String MOD_ID = "thetrash";

    public static final Logger LOGGER = LogManager.getLogger("ExampleConfig");

    @Override
    public void onInitialize() {
        BetterConfig.initialize(new Class[]{});
    }
}
