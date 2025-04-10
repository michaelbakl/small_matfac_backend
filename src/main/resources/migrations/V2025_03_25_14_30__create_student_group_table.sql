CREATE TABLE IF NOT EXISTS student_group (
                         studentGroupId uuid primary key not null,
                         studentId uuid,
                         groupId uuid,
                         startDate timestamptz,
                         endDate timestamptz,
                         actual bool,
                         foreign key (studentId) references student(studentId) on delete set null on update cascade,
                         foreign key (groupId) references "group"(groupId) on delete set null on update cascade
);
