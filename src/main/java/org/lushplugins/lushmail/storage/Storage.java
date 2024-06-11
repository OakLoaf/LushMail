package org.lushplugins.lushmail.storage;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.lushplugins.lushlib.utils.SimpleItemStack;
import org.lushplugins.lushmail.data.MailUser;
import org.lushplugins.lushmail.data.OfflineMailUser;
import org.lushplugins.lushmail.data.ReceivedMail;
import org.lushplugins.lushmail.mail.Mail;

import java.util.List;
import java.util.UUID;

/*
 * Mail Data: id, preview item, reward
 * User Data: uuid, username, unopened_mail, opened_mail
 */
public interface Storage {

    default void enable() {}

    default void disable() {}

    boolean isMailIdAvailable(String id);

    @NotNull List<String> getGroupsWithMail();

    @NotNull List<String> getGroupMailIds(String group);

    @NotNull List<String> getUnopenedGroupMailIds(UUID receiver, String group);

    @NotNull List<String> getReceivedMailIds(UUID receiver);

    @NotNull List<String> getReceivedMailIds(UUID receiver, String state);

    ReceivedMail getReceivedMail(UUID receiver, String mailId);

    boolean hasReceivedMail(UUID receiver, String mailId);

    @Nullable Mail loadMail(String id);

    @Nullable SimpleItemStack loadMailPreviewItem(String id);

    void saveMail(Mail mail);

    void sendMail(String sender, UUID receiver, String mailId, long timeout);

    void setMailState(UUID uuid, String mailId, String state);

    void removeMailFor(UUID uuid, String mailId);

    @Nullable MailUser loadMailUser(UUID uuid);

    @Nullable MailUser loadMailUser(String username);

    @Nullable OfflineMailUser loadOfflineMailUser(UUID uuid);

    @Nullable OfflineMailUser loadOfflineMailUser(String username);

    void saveMailUser(MailUser mailUser);

    @Nullable List<UUID> getIgnoredUsers(UUID uuid);

    boolean canSendMailTo(UUID sender, UUID receiver);

    void addIgnoredUser(UUID uuid, UUID ignoredUser);

    void removeIgnoredUser(UUID uuid, UUID ignoredUser);
}
