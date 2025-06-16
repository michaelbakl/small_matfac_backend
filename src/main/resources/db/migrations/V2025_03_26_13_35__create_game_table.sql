create table if not exists game
(
    gameId uuid not null PRIMARY KEY,
    roomId uuid,
    creatorId uuid,
    status text,
    foreign key (roomId) references room (roomId) on delete cascade on update cascade,
    foreign key (creatorId) references teacher (teacherId) on delete cascade on update cascade
);
