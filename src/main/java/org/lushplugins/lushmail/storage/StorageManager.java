package org.lushplugins.lushmail.storage;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.lushplugins.lushlib.utils.SimpleItemStack;
import org.lushplugins.lushmail.LushMail;
import org.lushplugins.lushmail.config.StorageConfig;
import org.lushplugins.lushmail.data.MailUser;
import org.lushplugins.lushmail.data.OfflineMailUser;
import org.lushplugins.lushmail.data.ReceivedGroupMail;
import org.lushplugins.lushmail.data.ReceivedMail;
import org.lushplugins.lushmail.mail.Mail;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class StorageManager {
    private final ExecutorService threads = Executors.newFixedThreadPool(1);
    private Storage storage;

    public StorageManager() {
        reload();
    }

    public void reload() {
        disable();

        StorageConfig config = new StorageConfig();
        switch (config.getStorageType()) {
            case "mysql", "mariadb" -> storage = new SQLStorage(config.getStorageInfo());
            case "sqlite" -> storage = new SQLiteStorage(config.getStorageInfo());
        }

        storage.enable();
    }

    public void disable() {
        if (storage != null) {
            storage.disable();
        }
    }

    /**
     * @param id The mail id to check
     * @return Whether the mail id is available to be used
     */
    public CompletableFuture<Boolean> isMailIdAvailable(String id) {
        return runAsync(() -> storage.isMailIdAvailable(id));
    }

    /**
     * @return All group mails that have not timed out
     */
    public CompletableFuture<List<ReceivedGroupMail>> getGroupMails() {
        return runAsync(() -> storage.getGroupMails());
    }

    public CompletableFuture<List<String>> getGroupsWithMail() {
        return runAsync(() -> storage.getGroupsWithMail());
    }

    public CompletableFuture<List<String>> getGroupMailIds(String group) {
        return runAsync(() -> storage.getGroupMailIds(group));
    }

    public CompletableFuture<List<String>> getUnopenedGroupMailIds(UUID receiver, String group) {
        return runAsync(() -> storage.getUnopenedGroupMailIds(receiver, group));
    }

    public CompletableFuture<List<String>> getReceivedMailIds(UUID receiver) {
        return runAsync(() -> storage.getReceivedMailIds(receiver));
    }

    public CompletableFuture<List<String>> getReceivedMailIds(UUID receiver, String state) {
        return runAsync(() -> storage.getReceivedMailIds(receiver, state));
    }

    public CompletableFuture<ReceivedMail> getReceivedMail(UUID receiver, String mailId) {
        return runAsync(() -> storage.getReceivedMail(receiver, mailId));
    }

    public CompletableFuture<Boolean> hasReceivedMail(UUID receiver, String mailId) {
        return runAsync(() -> storage.hasReceivedMail(receiver, mailId));
    }

    public CompletableFuture<Mail> loadMail(String id) {
        return runAsync(() -> storage.loadMail(id));
    }

    public CompletableFuture<SimpleItemStack> loadMailPreviewItem(String id) {
        return runAsync(() -> storage.loadMailPreviewItem(id));
    }

    public CompletableFuture<Void> saveMail(Mail mail) {
        return runAsync(() -> storage.saveMail(mail));
    }

    public CompletableFuture<Void> sendMail(String sender, UUID receiver, String mailId) {
        return sendMail(sender, receiver, mailId, -1);
    }


    public CompletableFuture<Void> sendMail(String sender, UUID receiver, String mailId, long timeout) {
        return runAsync(() -> storage.sendMail(sender, receiver, mailId, timeout));
    }

    public CompletableFuture<Void> setMailState(UUID uuid, String mailId, String state) {
        return runAsync(() -> storage.setMailState(uuid, mailId, state));
    }

    public CompletableFuture<Void> removeMailFor(UUID uuid, String mailId) {
        return runAsync(() -> storage.removeMailFor(uuid, mailId));
    }

    public CompletableFuture<UUID> loadUniqueId(String uuidOrUsername) {
        try {
            UUID uuid = UUID.fromString(uuidOrUsername);
            return CompletableFuture.completedFuture(uuid);
        } catch (IllegalArgumentException e) {
            Player player = Bukkit.getPlayer(uuidOrUsername);
            if (player != null) {
                return CompletableFuture.completedFuture(player.getUniqueId());
            }

            return LushMail.getInstance().getStorageManager().loadOfflineMailUser(uuidOrUsername).thenApply(OfflineMailUser::getUniqueId);
        }
    }

    public CompletableFuture<MailUser> loadMailUser(UUID uuid) {
        return runAsync(() -> storage.loadMailUser(uuid));
    }

    public CompletableFuture<MailUser> loadMailUser(String username) {
        return runAsync(() -> storage.loadMailUser(username));
    }

    public CompletableFuture<OfflineMailUser> loadOfflineMailUser(UUID uuid) {
        return runAsync(() -> storage.loadOfflineMailUser(uuid));
    }

    public CompletableFuture<OfflineMailUser> loadOfflineMailUser(String username) {
        return runAsync(() -> storage.loadOfflineMailUser(username));
    }

    public CompletableFuture<Void> saveOfflineMailUser(OfflineMailUser mailUser) {
        return runAsync(() -> storage.saveOfflineMailUser(mailUser));
    }

    public CompletableFuture<List<UUID>> getIgnoredUsers(UUID uuid) {
        return runAsync(() -> storage.getIgnoredUsers(uuid));
    }

    public CompletableFuture<Boolean> canSendMailTo(UUID sender, UUID receiver) {
        return runAsync(() -> storage.canSendMailTo(sender, receiver));
    }

    public CompletableFuture<Void> addIgnoredUser(UUID uuid, UUID ignoredUser) {
        return runAsync(() -> storage.addIgnoredUser(uuid, ignoredUser));
    }

    public CompletableFuture<Void> removeIgnoredUser(UUID uuid, UUID ignoredUser) {
        return runAsync(() -> storage.removeIgnoredUser(uuid, ignoredUser));
    }

    private <T> CompletableFuture<T> runAsync(Callable<T> callable) {
        CompletableFuture<T> future = new CompletableFuture<>();
        threads.submit(() -> {
            try {
                future.complete(callable.call());
            } catch (Throwable e) {
                e.printStackTrace();
                future.completeExceptionally(e);
            }
        });
        return future;
    }

    private CompletableFuture<Void> runAsync(Runnable runnable) {
        CompletableFuture<Void> future = new CompletableFuture<>();
        threads.submit(() -> {
            try {
                runnable.run();
                future.complete(null);
            } catch (Throwable e) {
                e.printStackTrace();
                future.completeExceptionally(e);
            }
        });
        return future;
    }
}
