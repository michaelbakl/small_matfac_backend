CREATE TABLE if not exists roles (
    name VARCHAR PRIMARY KEY,
    requires_approval BOOLEAN NOT NULL DEFAULT TRUE
);
