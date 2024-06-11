package org.lushplugins.lushmail.config;

import org.bukkit.configuration.file.YamlConfiguration;
import org.lushplugins.lushmail.LushMail;

import java.io.File;

public class StorageConfig {
    private static final File STORAGE_CONFIG_FILE = new File(LushMail.getInstance().getDataFolder(), "storage.yml");

    private final String storageType;
    private StorageInfo storageInfo;

    public StorageConfig() {
        LushMail.getInstance().saveDefaultResource("storage.yml");
        YamlConfiguration config = YamlConfiguration.loadConfiguration(STORAGE_CONFIG_FILE);

        storageType = config.getString("type");
        if (storageType == null) {
            LushMail.getInstance().getLogger().severe("No storage type defined.");
            return;
        }

        switch (storageType.toLowerCase()) {
            case "mysql", "mariadb" -> {
                String host = config.getString("host");
                int port = config.getInt("port");
                String databaseName = config.getString("database-name");
                String username = config.getString("username");
                String password = config.getString("password");

                storageInfo = new MySqlInfo(host, port, databaseName, username, password);
            }
            case "sqlite" -> {}
        }
    }

    public String getStorageType() {
        return storageType;
    }

    public StorageInfo getStorageInfo() {
        return storageInfo;
    }

    public interface StorageInfo {}
    public record MySqlInfo(String host, int port, String databaseName, String user, String password) implements StorageInfo {}
}
