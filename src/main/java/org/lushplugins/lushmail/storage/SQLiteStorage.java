package org.lushplugins.lushmail.storage;

import org.lushplugins.lushmail.config.StorageConfig;

public class SQLiteStorage extends SQLStorage {

    public SQLiteStorage(StorageConfig.StorageInfo info) {
        super(info);
    }

    @Override
    public void enable() {
        setupDatabase("storage/sqlite_setup.sql");
    }

    @Override
    protected DatabaseSource initDataSource(StorageConfig.StorageInfo info) {
        DatabaseSource source = new DatabaseSource();
        testDataSource(source);

        return source;
    }
}
