package notryken.effectinfo;

import notryken.effectinfo.config.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EffectInfo {
    // Constants
    public static final String MOD_ID = "effectinfo";
    public static final String MOD_NAME = "EffectInfo";
    public static final Logger LOG = LoggerFactory.getLogger(MOD_NAME);

    // Config management
    private static Config CONFIG;

    public static void init() {
        CONFIG = loadConfig();
    }

    public static Config config() {
        if (CONFIG == null) {
            throw new IllegalStateException("Config not yet available");
        }
        return CONFIG;
    }

    private static Config loadConfig() {
        try {
            return Config.load();
        } catch (Exception e) {
            LOG.error("Failed to load configuration file", e);
            LOG.error("Reverting to default configuration");
            return restoreDefaultConfig();
        }
    }

    public static Config restoreDefaultConfig() {
        CONFIG = new Config();
        CONFIG.writeChanges();
        return CONFIG;
    }
}