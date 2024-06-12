package org.lushplugins.lushmail.storage;

import com.zaxxer.hikari.HikariDataSource;
import org.lushplugins.lushmail.LushMail;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseSource {
    private HikariDataSource source = null;

    /**
     * SQLite source constructor
     */
    public DatabaseSource() {}

    /**
     * MySQL source constructor
     * @param source Database source
     */
    public DatabaseSource(HikariDataSource source) {
        this.source = source;
    }

    public Connection getConnection() throws SQLException {
        if (source != null) {
            return source.getConnection();
        } else {
            String dbPath = new File(LushMail.getInstance().getDataFolder(), "data.db").getAbsolutePath();
            return DriverManager.getConnection("jdbc:sqlite:" + dbPath);
        }
    }
}
