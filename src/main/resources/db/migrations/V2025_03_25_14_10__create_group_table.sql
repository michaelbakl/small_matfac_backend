CREATE TABLE IF NOT EXISTS "group" (
                         groupId uuid PRIMARY KEY,
                         name text,
                         dateOfCreating timestamptz,
                         classNum smallint
);
