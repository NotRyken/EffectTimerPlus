package notryken.effecttimerplus;

import notryken.effecttimerplus.config.Config;
import notryken.effecttimerplus.util.ModLogger;

public class EffectTimerPlus {
    // Constants
    public static final String MOD_ID = "effecttimerplus";
    public static final String MOD_NAME = "EffectTimerPlus";
    public static final ModLogger LOG = new ModLogger(MOD_NAME);

    // Config management
    private static Config CONFIG;

    public static void init() {
        CONFIG = Config.load();
    }

    public static Config config() {
        if (CONFIG == null) {
            throw new IllegalStateException("Config not yet available");
        }
        return CONFIG;
    }

    public static void restoreDefaultConfig() {
        CONFIG = new Config();
        CONFIG.writeToFile();
    }
}