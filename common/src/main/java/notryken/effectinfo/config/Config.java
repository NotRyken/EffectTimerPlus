package notryken.effectinfo.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

public class Config {
    // Constants
    public static final String DEFAULT_FILE_NAME = "effectinfo.json";
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    // Not saved, not user-accessible
    private static Path configPath;
    public static boolean configChecked;

    // Saved, not user-accessible
    private final int version = 1;

    // Saved, user-accessible
    public boolean ambientEffectTime;
    public int potencyColor;
    public int countdownColor;

    public Config() {
        ambientEffectTime = false;
        potencyColor = -1761607681; // roughly 0x99FFFFFF
        countdownColor = -1761607681;
    }

    /*
    this.r = (float)($$3 >> 16 & 255) / 255.0F;
    this.g = (float)($$3 >> 8 & 255) / 255.0F;
    this.b = (float)($$3 & 255) / 255.0F;
    this.a = (float)($$3 >> 24 & 255) / 255.0F;
     */

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

    // Accessors, mutators and utility methods go here

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
}
