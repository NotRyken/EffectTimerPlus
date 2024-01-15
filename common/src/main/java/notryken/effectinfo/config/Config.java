package notryken.effectinfo.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

public class Config {
    // Constants and defaults
    public static final String DEFAULT_FILE_NAME = "effectinfo.json";
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    public static final int DEFAULT_COLOR = -1761607681; // roughly 0x99FFFFFF
    public static final int DEFAULT_WARN_COLOR = -65536; // max red, max alpha
    public static final int DEFAULT_POTENCY_LOCATION = 1;
    public static final int DEFAULT_COUNTDOWN_LOCATION = 2;

    // Not saved, not user-accessible
    private static Path configPath;
    public static boolean configChecked;

    // Saved, not user-accessible
    private final int version = 1;

    // Saved, user-accessible
    public boolean potencyEnabled;
    public boolean countdownEnabled;
    public boolean ambientCountdownEnabled;
    public boolean countdownWarnEnabled;
    public boolean countdownFlashEnabled;
    public int countdownWarnTime;
    public int potencyColor;
    public int countdownColor;
    public int warnColor;
    public int potencyLocation;
    public int countdownLocation;

    public Config() {
        potencyEnabled = true;
        countdownEnabled = true;
        ambientCountdownEnabled = false;
        countdownWarnEnabled = true;
        countdownFlashEnabled = true;
        countdownWarnTime = 20;
        potencyColor = DEFAULT_COLOR;
        countdownColor = DEFAULT_COLOR;
        warnColor = DEFAULT_WARN_COLOR;
        potencyLocation = DEFAULT_POTENCY_LOCATION;
        countdownLocation = DEFAULT_COUNTDOWN_LOCATION;
    }

    public int getCountdownWarnTime() {
        return countdownWarnTime;
    }

    public void setCountdownWarnTime(int countdownWarnTime) {
        this.countdownWarnTime = countdownWarnTime;
    }

    public int getPotencyColor() {
        return potencyColor;
    }

    public void setPotencyColor(int potencyColor) {
        this.potencyColor = potencyColor;
    }

    public int getCountdownColor() {
        return countdownColor;
    }

    public void setCountdownColor(int countdownColor) {
        this.countdownColor = countdownColor;
    }

    public int getWarnColor() {
        return warnColor;
    }

    public void setWarnColor(int warnColor) {
        this.warnColor = warnColor;
    }

    // Config load and save

    public static Config load() {
        return load(DEFAULT_FILE_NAME);
    }

    public static Config load(String name) {
        configPath = Path.of("config").resolve(name);
        Config config;

        if (Files.exists(configPath)) {
            try (FileReader reader = new FileReader(configPath.toFile())) {
                config = GSON.fromJson(reader, Config.class);
            } catch (IOException e) {
                throw new RuntimeException("Could not parse config", e);
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

            // Write the file to temporary location
            Files.writeString(tempPath, GSON.toJson(this));

            // Atomically replace the old config file (if it exists) with the temporary file
            Files.move(tempPath, configPath, StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
        }
        catch (IOException e) {
            throw new RuntimeException("Couldn't update config file", e);
        }

    }
}
