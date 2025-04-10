create table if not exists room_users
(
    roomId uuid,
    userId uuid,
    role VARCHAR(20) NOT NULL CHECK (role IN ('TEACHER', 'STUDENT')),
    joined_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    PRIMARY KEY (roomId, answerId),
    foreign key (roomId) references room (roomId) on delete cascade on update cascade,
    foreign key (userId) references user (userId) on delete set null on update cascade
);
