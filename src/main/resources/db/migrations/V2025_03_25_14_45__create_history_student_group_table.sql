CREATE TABLE IF NOT EXISTS history_student_group (
                                    historyId uuid primary key,
                                    studentGroupId uuid,
                                    dateOfChanging timestamptz,
                                    foreign key (studentGroupId) references student_group(studentGroupId) on delete cascade on update cascade
);
