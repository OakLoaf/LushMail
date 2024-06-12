package org.lushplugins.lushmail;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.bstats.bukkit.Metrics;
import org.lushplugins.lushlib.LushLib;
import org.lushplugins.lushlib.hook.Hook;
import org.lushplugins.lushlib.plugin.SpigotPlugin;
import org.lushplugins.lushmail.command.MailCommand;
import org.lushplugins.lushmail.hook.LuckPermsHook;
import org.lushplugins.lushmail.listener.PlayerListener;
import org.lushplugins.lushmail.mail.MailManager;
import org.lushplugins.lushmail.mail.MailTypes;
import org.lushplugins.lushmail.storage.StorageManager;

import java.util.Random;

public final class LushMail extends SpigotPlugin {
    private static final Gson GSON;
    private static final Random RANDOM = new Random();
    private static LushMail plugin;

    private StorageManager storageManager;
    private MailTypes mailTypes;
    private MailManager mailManager;

    static {
        GSON = new GsonBuilder()
            .setPrettyPrinting()
            .create();
    }

    @Override
    public void onLoad() {
        plugin = this;
    }

    @Override
    public void onEnable() {
        LushLib.getInstance().enable(this);
        mailTypes = new MailTypes();
        storageManager = new StorageManager();
        mailManager = new MailManager();

        addHook("LuckPerms", () ->  registerHook(new LuckPermsHook()));
        getHooks().forEach(Hook::enable);

        new PlayerListener().registerListeners();

        registerCommand(new MailCommand());

        new Metrics(this, 22228);
    }

    @Override
    public void onDisable() {
        getHooks().forEach(Hook::disable);

        mailTypes = null;

        if (storageManager != null) {
            storageManager.disable();
            storageManager = null;
        }

        LushLib.getInstance().disable();
    }

    public MailTypes getMailTypes() {
        return mailTypes;
    }

    public StorageManager getStorageManager() {
        return storageManager;
    }

    public MailManager getMailManager() {
        return mailManager;
    }

    public static Gson getGson() {
        return GSON;
    }

    public static Random getRandom() {
        return RANDOM;
    }

    public static LushMail getInstance() {
        return plugin;
    }
}
