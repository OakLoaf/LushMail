package org.lushplugins.lushmail.mail;

import com.google.common.collect.HashMultimap;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.lushplugins.lushmail.LushMail;
import org.lushplugins.lushmail.data.ReceivedGroupMail;
import org.lushplugins.lushmail.storage.StorageManager;
import org.lushplugins.lushmail.util.IdGenerator;

import java.util.*;
import java.util.concurrent.CompletableFuture;

public class MailManager {
    private static final int ID_LENGTH = 6;
    private static final int MAX_GENERATION_ATTEMPTS = 10;

    private final HashMultimap<String, ReceivedGroupMail> groupMails = HashMultimap.create();

    public MailManager() {
        Bukkit.getScheduler().runTaskTimerAsynchronously(LushMail.getInstance(), () -> {
            LushMail.getInstance().getStorageManager().getGroupMails().thenAccept(groupMails -> {
                this.groupMails.clear();

                for (ReceivedGroupMail groupMail : groupMails) {
                    this.groupMails.put(groupMail.getGroup(), groupMail);
                }
            });
        }, 0, 1200);
    }

    public CompletableFuture<List<String>> getAllUnopenedMailIds(UUID uuid) {
        Player player = Bukkit.getPlayer(uuid);

        return LushMail.getInstance().getStorageManager().getReceivedMailIds(uuid, Mail.State.UNOPENED).thenApply(mailIds -> {
            for (String group : groupMails.keySet()) {
                if (group.equals("all") || (player != null && player.hasPermission("group." + group))) {
                    mailIds.addAll(groupMails.get(group).stream().map(ReceivedGroupMail::getId).toList());
                }
            }

            return mailIds;
        });
    }

    public CompletableFuture<Set<String>> getUnopenedGroupMailIds(Player player) {
        StorageManager storageManager = LushMail.getInstance().getStorageManager();
        return storageManager.getGroupsWithMail().thenCompose(allGroups -> {
            List<CompletableFuture<List<String>>> futures = new ArrayList<>();
            HashSet<String> mailIds = new HashSet<>();
            for (String group : allGroups) {
                if (group.equals("all") || player.hasPermission("group." + group)) {
                    CompletableFuture<List<String>> mailIdsFuture = storageManager.getUnopenedGroupMailIds(player.getUniqueId(), group);
                    futures.add(mailIdsFuture);
                    mailIdsFuture.thenAccept(mailIds::addAll);
                }
            }

            return CompletableFuture.allOf(futures.toArray(CompletableFuture[]::new))
                .thenApply(ignored -> mailIds);
        });
    }

    public CompletableFuture<Boolean> canSendMailTo(CommandSender sender, UUID receiver) {
        CompletableFuture<Boolean> future;
        if (sender instanceof Player player) {
            if (!sender.hasPermission("lushmail.ignore.bypass")) {
                future = LushMail.getInstance().getStorageManager().canSendMailTo(player.getUniqueId(), receiver);
            } else {
                future = CompletableFuture.completedFuture(true);
            }
        } else {
            future = CompletableFuture.completedFuture(true);
        }

        return future;
    }

    public CompletableFuture<String> generateUniqueMailId() {
        CompletableFuture<String> future = new CompletableFuture<>();
        generateUniqueMailId(future, 0);
        return future;
    }

    private void generateUniqueMailId(CompletableFuture<String> future, int attempt) {
        if (attempt >= MAX_GENERATION_ATTEMPTS) {
            future.complete(null);
            return;
        }

        String id = IdGenerator.generateRandomAlphanumeric(ID_LENGTH);
        LushMail.getInstance().getStorageManager().isMailIdAvailable(id).thenAccept(available -> {
            if (available) {
                future.complete(id);
            } else {
                generateUniqueMailId(future, attempt + 1);
            }
        });
    }
}
