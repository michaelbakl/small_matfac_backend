create table if not exists game_config
(
    configId uuid not null PRIMARY KEY,
    name text,
    duration int,
    gameType text,
    difficulty text,
    allowSkips boolean,
    enableHints boolean,
    startDate timestamptz,
    finishDate timestamptz,
    foreign key (configId) references game (gameId) on delete cascade on update cascade
);
