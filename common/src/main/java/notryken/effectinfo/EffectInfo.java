package notryken.effectinfo;

import notryken.effectinfo.config.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EffectInfo {
    // Constants
    public static final String MOD_ID = "effectinfo";
    public static final String MOD_NAME = "EffectInfo";
    public static final Logger LOG = LoggerFactory.getLogger(MOD_NAME);

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
            LOG.error("Using default configuration file");
            Config newConfig = new Config();
            newConfig.writeChanges();
            return newConfig;
        }
    }

    public static void restoreDefaultConfig() {
        CONFIG = new Config();
        CONFIG.writeChanges();
    }
}