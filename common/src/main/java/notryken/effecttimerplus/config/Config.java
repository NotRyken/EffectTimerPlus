package notryken.effecttimerplus.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import notryken.effecttimerplus.EffectTimerPlus;
import notryken.effecttimerplus.util.MiscUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

/**
 * Includes derivative work of code used by
 * <a href="https://github.com/CaffeineMC/sodium-fabric/">Sodium</a>
 */
public class Config {
    // Constants and defaults
    public static final String DEFAULT_FILE_NAME = "effecttimerplus.json";
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    public static final float DEFAULT_SCALE = 1.0F;
    public static final int DEFAULT_COLOR = -1711276033; // 0x99FFFFFF
    public static final int DEFAULT_BACK_COLOR = -1776213727;
    public static final int DEFAULT_WARN_COLOR = -65536; // 0xFFFF0000
    public static final int DEFAULT_WARN_TIME = 20;
    public static final int DEFAULT_POTENCY_LOCATION = 2;
    public static final int DEFAULT_COUNTDOWN_LOCATION = 6;

    // Not saved, not modifiable by user
    private static Path configPath;

    // Saved, not modifiable by user
    private final int version = 1;

    // Saved, modifiable by user
    public double scale;
    public boolean potencyEnabled;
    public boolean timerEnabled;
    public boolean timerEnabledAmbient;
    public boolean timerWarnEnabled;
    public boolean timerFlashEnabled;
    private int timerWarnTime;
    private int potencyColor;
    private int potencyBackColor;
    private int timerColor;
    private int timerWarnColor;
    private int timerBackColor;
    private int potencyLocation;
    private int timerLocation;

    public Config() {
        scale = DEFAULT_SCALE;
        potencyEnabled = true;
        timerEnabled = true;
        timerEnabledAmbient = false;
        timerWarnEnabled = true;
        timerFlashEnabled = true;
        timerWarnTime = DEFAULT_WARN_TIME;
        potencyColor = DEFAULT_COLOR;
        potencyBackColor = DEFAULT_BACK_COLOR;
        timerColor = DEFAULT_COLOR;
        timerBackColor = DEFAULT_BACK_COLOR;
        timerWarnColor = DEFAULT_WARN_COLOR;
        potencyLocation = DEFAULT_POTENCY_LOCATION;
        timerLocation = DEFAULT_COUNTDOWN_LOCATION;
    }

    // Set and get

    public int getTimerWarnTime() {
        return timerWarnTime;
    }

    public void setTimerWarnTime(int time) {
        this.timerWarnTime = time < 0 ? DEFAULT_WARN_TIME : time;
    }

    public int getPotencyColor() {
        return potencyColor;
    }

    public void setPotencyColor(int color) {
        this.potencyColor = adjustColor(color);
    }

    public int getPotencyBackColor() {
        return potencyBackColor;
    }

    public void setPotencyBackColor(int color) {
        this.potencyBackColor = adjustColor(color);
    }

    public int getTimerColor() {
        return timerColor;
    }

    public void setTimerColor(int color) {
        this.timerColor = adjustColor(color);
    }

    public int getTimerBackColor() {
        return timerBackColor;
    }

    public void setTimerBackColor(int color) {
        this.timerBackColor = adjustColor(color);
    }

    public int getTimerWarnColor() {
        return timerWarnColor;
    }

    public void setTimerWarnColor(int color) {
        this.timerWarnColor = adjustColor(color);
    }

    public int getPotencyLocation() {
        return potencyLocation;
    }

    public void setPotencyLocation(int locIndex) {
        this.potencyLocation = locIndex;
    }

    public int getTimerLocation() {
        return timerLocation;
    }

    public void setTimerLocation(int locIndex) {
        this.timerLocation = locIndex;
    }

    // Validate and reset

    private int adjustColor(int color) {
        if (MiscUtil.toAlpha.applyAsInt(color) < 4) {
            return MiscUtil.withAlpha.applyAsInt(color, MiscUtil.fromAlpha.applyAsInt(4));
        }
        return color;
    }

    /**
     * Class initialization using Gson does not guarantee that values are valid.
     * To be called whenever config is loaded from file.
     */
    private void validate() {
        setTimerWarnTime(timerWarnTime);
        setPotencyColor(potencyColor);
        setPotencyBackColor(potencyBackColor);
        setTimerColor(timerColor);
        setTimerBackColor(timerBackColor);
        setTimerWarnColor(timerWarnColor);
        setPotencyLocation(potencyLocation);
        setTimerLocation(timerLocation);
    }

    public void resetPotencyConfig() {
        potencyEnabled = true;
        potencyColor = DEFAULT_COLOR;
        potencyBackColor = DEFAULT_BACK_COLOR;
        potencyLocation = DEFAULT_POTENCY_LOCATION;
    }

    public void resetTimerConfig() {
        timerEnabled = true;
        timerEnabledAmbient = false;
        timerWarnEnabled = true;
        timerFlashEnabled = true;
        timerWarnTime = DEFAULT_WARN_TIME;
        timerColor = DEFAULT_COLOR;
        timerWarnColor = DEFAULT_WARN_COLOR;
        timerBackColor = DEFAULT_BACK_COLOR;
        timerLocation = DEFAULT_POTENCY_LOCATION;
    }

    // Load and save

    public static @NotNull Config load() {
        Config config = load(DEFAULT_FILE_NAME);
        if (config == null) {
            EffectTimerPlus.LOG.info("Using default configuration.");
            config = new Config();
        }
        config.writeToFile();
        return config;
    }

    public static @Nullable Config load(String name) {
        configPath = Path.of("config").resolve(name);
        Config config = null;

        if (Files.exists(configPath)) {
            try (FileReader reader = new FileReader(configPath.toFile())) {
                config = GSON.fromJson(reader, Config.class);
            } catch (Exception e) {
                EffectTimerPlus.LOG.error("Unable to load config from file '{}'.", configPath, e);
            }
        } else {
            EffectTimerPlus.LOG.warn("Unable to locate config file '{}'.", name);
        }
        return config;
    }

    public void writeToFile() {
        Path dir = configPath.getParent();

        try {
            if (!Files.exists(dir)) {
                Files.createDirectories(dir);
            } else if (!Files.isDirectory(dir)) {
                throw new IOException("Not a directory: " + dir);
            }

            // Use a temporary location next to the config's final destination
            Path tempPath = configPath.resolveSibling(configPath.getFileName() + ".tmp");

            // Write the file to the temporary location
            Files.writeString(tempPath, GSON.toJson(this));

            // Atomically replace the old config file (if it exists) with the temporary file
            Files.move(tempPath, configPath, StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
        }
        catch (IOException e) {
            throw new RuntimeException("Unable to update config file", e);
        }
    }
}
