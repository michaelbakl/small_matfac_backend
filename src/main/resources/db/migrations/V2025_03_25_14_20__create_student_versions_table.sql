CREATE TABLE IF NOT EXISTS student_versions (
                         studentId uuid,
                         userId uuid,
                         surname text,
                         name text,
                         middleName text,
                         email text,
                         dateOfBirth date,
                         dateOfEntering date,
                         dateOfChanging timestamptz,
                         primary key (studentId, dateOfChanging),
                         foreign key (userId) references "user"(userId) on delete cascade on update cascade
);
