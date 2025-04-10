create table if not exists room
(
    roomId uuid not null PRIMARY KEY,
    name text,
    teacherId uuid,
    isClosed boolean not null,
    dateOfCreating timestampz,
    foreign key (teacherId) references teacher (teacherId) on delete cascade on update cascade
);
