package notryken.effecttimerplus.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

/**
 * Configuration options class. Loosely based on the design used by
 * <a href="https://github.com/CaffeineMC/sodium-fabric/">Sodium</a>
 */
public class Config {
    // Constants and defaults
    public static final String DEFAULT_FILE_NAME = "effecttimerplus.json";
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
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
        return (color & -67108864) == 0 ? color | 0xFF000000 : color;
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

    public static Config load() {
        return load(DEFAULT_FILE_NAME);
    }

    public static Config load(String name) {
        configPath = Path.of("config").resolve(name);
        Config config;

        if (Files.exists(configPath)) {
            try (FileReader reader = new FileReader(configPath.toFile())) {
                config = GSON.fromJson(reader, Config.class);
                config.validate();
            } catch (IOException e) {
                throw new RuntimeException("Unable to parse config", e);
            }
        }
        else {
            config = new Config();
        }

        config.writeChanges();
        return config;
    }

    public void writeChanges() {
        Path dir = configPath == null ? Path.of("config") : configPath.getParent();

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
