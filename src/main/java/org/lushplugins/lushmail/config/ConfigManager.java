package org.lushplugins.lushmail.config;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.lushplugins.lushlib.gui.inventory.GuiFormat;
import org.lushplugins.lushlib.utils.SimpleItemStack;
import org.lushplugins.lushmail.LushMail;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ConfigManager {
    private String consoleName;
    private GuiFormat mailGuiFormat;
    private final ConcurrentHashMap<String, SimpleItemStack> guiItems = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, String> messages = new ConcurrentHashMap<>();

    public ConfigManager() {
        LushMail.getInstance().saveDefaultConfig();
    }

    public void reloadConfig() {
        LushMail plugin = LushMail.getInstance();
        guiItems.clear();
        messages.clear();

        plugin.reloadConfig();
        FileConfiguration config = plugin.getConfig();

        consoleName = config.getString("console-name", "Admin");
        mailGuiFormat = new GuiFormat(config.getStringList("gui.format"));

        ConfigurationSection guiItemsSection = config.getConfigurationSection("gui.items");
        if (guiItemsSection != null) {
            for (Map.Entry<String, Object> entry : guiItemsSection.getValues(false).entrySet()) {
                if (entry.getValue() instanceof ConfigurationSection itemSection) {
                    String key = entry.getKey();
                    SimpleItemStack guiItem = new SimpleItemStack(itemSection);

                    if (key.equals("border")) {
                        mailGuiFormat.setItemReference('#', guiItem);
                    }

                    guiItems.put(entry.getKey(), guiItem);
                }
            }
        }

        ConfigurationSection messagesSection = config.getConfigurationSection("messages");
        if (messagesSection != null) {
            for (Map.Entry<String, Object> entry : messagesSection.getValues(false).entrySet()) {
                if (entry.getValue() instanceof String message) {
                    messages.put(entry.getKey(), message);
                }
            }
        }
    }

    public String getConsoleName() {
        return consoleName;
    }

    public GuiFormat getMailGuiFormat() {
        return mailGuiFormat;
    }

    public boolean hasGuiItem(String key) {
        return guiItems.containsKey(key);
    }

    public @Nullable SimpleItemStack getGuiItem(String key) {
        return guiItems.get(key).clone();
    }

    public @Nullable String getMessage(String key) {
        return messages.get(key);
    }

    public @NotNull String getMessage(String key, @NotNull String def) {
        return messages.getOrDefault(key, def);
    }
}
