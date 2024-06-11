package org.lushplugins.lushmail.storage;

import com.google.gson.JsonParser;
import com.mysql.cj.jdbc.MysqlConnectionPoolDataSource;
import com.mysql.cj.jdbc.MysqlDataSource;
import org.jetbrains.annotations.NotNull;
import org.lushplugins.lushlib.utils.SimpleItemStack;
import org.lushplugins.lushmail.LushMail;
import org.lushplugins.lushmail.config.StorageConfig;
import org.lushplugins.lushmail.data.MailUser;
import org.lushplugins.lushmail.data.OfflineMailUser;
import org.lushplugins.lushmail.data.ReceivedMail;
import org.lushplugins.lushmail.mail.Mail;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/*
 * Tables:
 *   mail: id, type, preview_item, data
 *   mail_users: uuid, username
 *   ignored_users: uuid, ignored_uuid
 *   received_mail: uuid, mail_id, state, favourited, time_sent, timeout
 *   group_mail: mail_id, group, time_sent, timeout
 */
public class MySqlStorage implements Storage {
    private final MysqlDataSource dataSource;

    public MySqlStorage(StorageConfig.MySqlInfo info) {
        dataSource = initDataSource(
            info.host(),
            info.port(),
            info.databaseName(),
            info.user(),
            info.password());
    }

    @Override
    public boolean isMailIdAvailable(String id) {
        try (Connection conn = conn(); PreparedStatement stmt = conn.prepareStatement("SELECT * FROM mail WHERE id = ?;")) {
            stmt.setString(1, id);

            ResultSet resultSet = stmt.executeQuery();
            return !resultSet.next();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return true;
    }

    @Override
    public @NotNull List<String> getGroupsWithMail() {
        try (Connection conn = conn(); PreparedStatement stmt = conn.prepareStatement("SELECT DISTINCT group FROM group_mail;")) {
            List<String> groups = new ArrayList<>();
            ResultSet resultSet = stmt.executeQuery();
            while (resultSet.next()) {
                String group = resultSet.getString("group");
                groups.add(group);
            }

            return groups;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return new ArrayList<>();
    }

    @Override
    public @NotNull List<String> getGroupMailIds(String group) {
        try (Connection conn = conn(); PreparedStatement stmt = conn.prepareStatement("SELECT mail_id FROM group_mail WHERE group = ?;")) {
            stmt.setString(1, group);

            List<String> mailIds = new ArrayList<>();
            ResultSet resultSet = stmt.executeQuery();
            while (resultSet.next()) {
                String mailId = resultSet.getString("mail_id");
                mailIds.add(mailId);
            }

            return mailIds;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return new ArrayList<>();
    }

    @Override
    public @NotNull List<String> getUnopenedGroupMailIds(UUID receiver, String group) {
        // TODO: Consider checking timestamps
        try (Connection conn = conn(); PreparedStatement stmt = conn.prepareStatement("SELECT gm.mail_id FROM group_mail AS gm LEFT JOIN received_mail AS rm ON gm.mail_id = rm.mail_id WHERE rm.uuid = ? AND gm.group = ? AND (rm.state IS NULL OR rm.state = ?);")) {
            stmt.setString(1, receiver.toString());
            stmt.setString(2, group);
            stmt.setString(3, Mail.State.UNOPENED);

            List<String> mailIds = new ArrayList<>();
            ResultSet resultSet = stmt.executeQuery();
            while (resultSet.next()) {
                String mailId = resultSet.getString("mail_id");
                mailIds.add(mailId);
            }

            return mailIds;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return new ArrayList<>();
    }

    @Override
    public @NotNull List<String> getReceivedMailIds(UUID receiver) {
        // TODO: Consider checking timestamps
        try (Connection conn = conn(); PreparedStatement stmt = conn.prepareStatement("SELECT mail_id FROM received_mail WHERE uuid = ?;")) {
            stmt.setString(1, receiver.toString());

            List<String> mailIds = new ArrayList<>();
            ResultSet resultSet = stmt.executeQuery();
            while (resultSet.next()) {
                String mailId = resultSet.getString("mail_id");
                mailIds.add(mailId);
            }

            return mailIds;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return new ArrayList<>();
    }

    @Override
    public @NotNull List<String> getReceivedMailIds(UUID receiver, String state) {
        // TODO: Consider checking timestamps
        try (Connection conn = conn(); PreparedStatement stmt = conn.prepareStatement("SELECT mail_id FROM received_mail WHERE uuid = ? AND state = ?;")) {
            stmt.setString(1, receiver.toString());
            stmt.setString(2, state);

            List<String> mailIds = new ArrayList<>();
            ResultSet resultSet = stmt.executeQuery();
            while (resultSet.next()) {
                String mailId = resultSet.getString("mail_id");
                mailIds.add(mailId);
            }

            return mailIds;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return new ArrayList<>();
    }

    @Override
    public ReceivedMail getReceivedMail(UUID receiver, String mailId) {ae
        try (Connection conn = conn(); PreparedStatement stmt = conn.prepareStatement("SELECT state, favourited FROM received_mail WHERE uuid = ? AND mail_id = ?;")) {
            stmt.setString(1, receiver.toString());
            stmt.setString(2, mailId);

            ResultSet resultSet = stmt.executeQuery();
            if (resultSet.next()) {
                String state = resultSet.getString("state");
                boolean favourited = resultSet.getBoolean("favourited");

                new ReceivedMail(receiver, mailId, state, favourited);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public boolean hasReceivedMail(UUID receiver, String mailId) {
        try (Connection conn = conn(); PreparedStatement stmt = conn.prepareStatement("SELECT * FROM received_mail WHERE uuid = ? AND mail_id = ?;")) {
            stmt.setString(1, receiver.toString());
            stmt.setString(2, mailId);

            ResultSet resultSet = stmt.executeQuery();
            return resultSet.next();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    @Override
    public Mail loadMail(String id) {
        try (Connection conn = conn(); PreparedStatement stmt = conn.prepareStatement("SELECT * FROM mail WHERE id = ?;")) {
            stmt.setString(1, id);

            ResultSet resultSet = stmt.executeQuery();
            if (resultSet.next()) {
                String type = resultSet.getString("type");
                String jsonRaw = resultSet.getString("data");
                if (jsonRaw != null) {
                    return LushMail.getInstance().getMailTypes().constructMail(type, JsonParser.parseString(jsonRaw));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public SimpleItemStack loadMailPreviewItem(String id) {
        try (Connection conn = conn(); PreparedStatement stmt = conn.prepareStatement("SELECT preview_item FROM mail WHERE id = ?;")) {
            stmt.setString(1, id);

            ResultSet resultSet = stmt.executeQuery();
            if (resultSet.next()) {
                String jsonRaw = resultSet.getString("preview_item");
                if (jsonRaw != null) {
                    return LushMail.getGson().fromJson(JsonParser.parseString(jsonRaw), SimpleItemStack.class);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public void saveMail(Mail mail) {
        try (Connection conn = conn(); PreparedStatement stmt = conn.prepareStatement("REPLACE INTO mail (id, type, preview_item, data) VALUES (?, ?, ?, ?);")) {
            stmt.setString(1, mail.getId());
            stmt.setString(2, mail.getType());
            stmt.setString(3, LushMail.getGson().toJson(mail.getPreviewItem()));
            stmt.setString(4, LushMail.getGson().toJson(mail));

            stmt.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void sendMail(String sender, UUID receiver, String mailId, long timeout) {
        try (Connection conn = conn(); PreparedStatement stmt = conn.prepareStatement("REPLACE INTO received_mail (uuid, mail_id, state, time_sent, timeout) VALUES (?, ?, ?, ?, ?);")) {
            stmt.setString(1, receiver.toString());
            stmt.setString(2, mailId);
            stmt.setString(3, Mail.State.UNOPENED);
            stmt.setLong(4, Instant.now().getEpochSecond());
            stmt.setLong(5, timeout);

            stmt.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setMailState(UUID uuid, String mailId, String state) {
        try (Connection conn = conn(); PreparedStatement stmt = conn.prepareStatement("UPDATE received_mail SET state = ? WHERE uuid = ? AND mail_id = ?;")) {
            stmt.setString(1, state);
            stmt.setString(2, uuid.toString());
            stmt.setString(3, mailId);

            stmt.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void removeMailFor(UUID uuid, String mailId) {
        try (Connection conn = conn(); PreparedStatement stmt = conn.prepareStatement("DELETE FROM received_mail WHERE uuid = ? AND mail_id = ?;")) {
            stmt.setString(1, uuid.toString());
            stmt.setString(2, mailId);

            stmt.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public MailUser loadMailUser(UUID uuid) {
        // TODO
        return null;
    }

    @Override
    public MailUser loadMailUser(String username) {
        // TODO
        return null;
    }

    @Override
    public OfflineMailUser loadOfflineMailUser(UUID uuid) {
        try (Connection conn = conn(); PreparedStatement stmt = conn.prepareStatement("SELECT * FROM mail_users WHERE uuid = ?;")) {
            stmt.setString(1, uuid.toString());

            ResultSet resultSet = stmt.executeQuery();
            if (resultSet.next()) {
                String username = resultSet.getString("username");
                return new OfflineMailUser(uuid, username);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public OfflineMailUser loadOfflineMailUser(String username) {
        try (Connection conn = conn(); PreparedStatement stmt = conn.prepareStatement("SELECT * FROM mail_users WHERE username = ?;")) {
            stmt.setString(1, username);

            ResultSet resultSet = stmt.executeQuery();
            if (resultSet.next()) {
                UUID uuid = UUID.fromString(resultSet.getString("uuid"));
                return new OfflineMailUser(uuid, username);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public void saveMailUser(MailUser mailUser) {
        try (Connection conn = conn(); PreparedStatement stmt = conn.prepareStatement("REPLACE INTO mail_users (uuid, username) VALUES (?, ?);")) {
            stmt.setString(1, mailUser.getUniqueId().toString());
            stmt.setString(2, mailUser.getUsername());

            stmt.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<UUID> getIgnoredUsers(@NotNull UUID uuid) {
        try (Connection conn = conn(); PreparedStatement stmt = conn.prepareStatement("SELECT ignored_uuid FROM ignored_users WHERE uuid = ?;")) {
            stmt.setString(1, uuid.toString());

            List<UUID> ignoredUsers = new ArrayList<>();
            ResultSet resultSet = stmt.executeQuery();
            while(resultSet.next()) {
                UUID ignoredUser = UUID.fromString(resultSet.getString("ignored_uuid"));
                ignoredUsers.add(ignoredUser);
            }

            return ignoredUsers;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public boolean canSendMailTo(@NotNull UUID sender, @NotNull UUID receiver) {
        try (Connection conn = conn(); PreparedStatement stmt = conn.prepareStatement("SELECT * FROM ignored_users WHERE uuid = ? AND ignored_uuid = ?;")) {
            stmt.setString(1, receiver.toString());
            stmt.setString(2, sender.toString());

            ResultSet resultSet = stmt.executeQuery();
            return !resultSet.next();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return true;
    }

    @Override
    public void addIgnoredUser(@NotNull UUID uuid, @NotNull UUID ignoredUser) {
        try (Connection conn = conn(); PreparedStatement stmt = conn.prepareStatement("REPLACE INTO ignored_users (uuid, ignored_uuid) VALUES (?, ?);")) {
            stmt.setString(1, uuid.toString());
            stmt.setString(2, ignoredUser.toString());

            stmt.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void removeIgnoredUser(@NotNull UUID uuid, @NotNull UUID ignoredUser) {
        try (Connection conn = conn(); PreparedStatement stmt = conn.prepareStatement("DELETE FROM ignored_users WHERE uuid = ? AND ignored_uuid = ?;")) {
            stmt.setString(1, uuid.toString());
            stmt.setString(2, ignoredUser.toString());

            stmt.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Connection conn() {
        try {
            return dataSource.getConnection();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            return null;
        }
    }

    private MysqlDataSource initDataSource(String host, int port, String dbName, String user, String password) {
        MysqlDataSource dataSource = new MysqlConnectionPoolDataSource();
        dataSource.setServerName(host);
        dataSource.setPortNumber(port);
        dataSource.setDatabaseName(dbName);
        dataSource.setUser(user);
        dataSource.setPassword(password);
        testDataSource(dataSource);
        return dataSource;
    }

    private void testDataSource(DataSource dataSource) {
        try (Connection conn = dataSource.getConnection()) {
            if (!conn.isValid(1000)) {
                throw new SQLException("Could not establish database connection.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
