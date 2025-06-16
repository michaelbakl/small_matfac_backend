CREATE TABLE IF NOT EXISTS teacher_versions (
                         teacherId uuid,
                         userId uuid,
                         surname text,
                         name text,
                         middleName text,
                         email text,
                         dateOfBirth date,
                         dateOfChanging date,
                         primary key (teacherId, dateOfChanging),
                         foreign key (userId) references "user"(userId) on delete cascade on update cascade
);
