package org.lushplugins.lushmail.config;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.lushplugins.lushmail.LushMail;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MainConfig {
    private String consoleName;
    private final ConcurrentHashMap<String, String> messages = new ConcurrentHashMap<>();

    public MainConfig() {
        LushMail.getInstance().saveDefaultConfig();
    }

    public void reloadConfig() {
        LushMail plugin = LushMail.getInstance();
        messages.clear();

        plugin.reloadConfig();
        FileConfiguration config = plugin.getConfig();

        consoleName = config.getString("console-name", "Admin");

        ConfigurationSection messagesSection = config.getConfigurationSection("messages");
        if (messagesSection != null) {
            for (Map.Entry<String, Object> entry : messagesSection.getValues(false).entrySet()) {
                messages.put(entry.getKey(), (String) entry.getValue());
            }
        }
    }

    public String getConsoleName() {
        return consoleName;
    }

    public String getMessage(String key) {
        return messages.get(key);
    }
}
