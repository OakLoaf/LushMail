CREATE TABLE IF NOT EXISTS mail_data
(
    id VARCHAR(10) NOT NUlL PRIMARY KEY,
    type TEXT NOT NULL,
    preview_item JSON,
    data JSON
);
|
CREATE TABLE IF NOT EXISTS mail_users
(
    uuid BINARY(16) NOT NULL PRIMARY KEY,
    username CHAR(17)
);
|
CREATE TABLE IF NOT EXISTS ignored_users
(
    row_id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
    uuid BINARY(16) NOT NULL,
    ignored_uuid BINARY(16) NOT NULL
);
|
CREATE TABLE IF NOT EXISTS received_mail
(
    row_id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
    mail_id VARCHAR(10) NOT NULL,
    uuid BINARY(16) NOT NULL,
    state VARCHAR(64),
    favourited BOOL NOT NULL,
    time_sent BIGINT NOT NULL,
    timeout BIGINT
);
|
CREATE TABLE IF NOT EXISTS group_mail
(
    row_id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
    mail_id VARCHAR(10) NOT NULL,
    group_name TEXT NOT NULL,
    state VARCHAR(64),
    time_sent BIGINT NOT NULL,
    timeout BIGINT
);