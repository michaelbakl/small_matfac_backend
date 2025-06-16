CREATE TABLE IF NOT EXISTS teacher (
                            teacherId uuid PRIMARY KEY,
                            userId uuid,
                            surname text,
                            name text,
                            middleName text,
                            email text,
                            dateOfBirth date,
                            dateOfEntering date,
                            foreign key (userId) references "user"(userId) on delete cascade on update cascade
);
