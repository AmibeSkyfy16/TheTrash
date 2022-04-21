package ch.skyfy.thetrash.config.core;


import net.fabricmc.loader.api.FabricLoader;

import java.nio.file.Path;

import static ch.skyfy.thetrash.TheTrash.LOGGER;
import static ch.skyfy.thetrash.TheTrash.MOD_ID;

@SuppressWarnings("unused")
public class BetterConfig {

    public static Path CONFIG_DIRECTORY = FabricLoader.getInstance().getConfigDir().resolve(MOD_ID);

    private static boolean broke = false;

    public static boolean initialize(Class<?>[] classesToLoad) {
        createConfigDirectory();
        loadConfigByReflection(classesToLoad);
        if (broke) LOGGER.error("an error occurred while loading configurations");
        else LOGGER.info("ALL CONFIG HAVE BEEN LOADED WITH SUCCESS");
        return broke;
    }

    /**
     * Create a root folder that will contain all others configurations files used by the mod
     */
    private static void createConfigDirectory() {
        try {
            var file = CONFIG_DIRECTORY.toFile();
            if (!file.exists()) broke = !file.mkdir();
        } catch (UnsupportedOperationException | SecurityException e) {
            e.printStackTrace();
            broke = true;
        }
    }

    private static void loadConfigByReflection(Class<?>[] classesToLoad) {
        for (Class<?> config : classesToLoad) {
            var canonicalName = config.getCanonicalName();
            try {
                Class.forName(canonicalName);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
                broke = true;
            }
        }
    }
}