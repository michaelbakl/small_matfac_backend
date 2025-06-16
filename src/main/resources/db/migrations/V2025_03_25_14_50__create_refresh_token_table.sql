create table if not exists refresh_tokens
(
    userId uuid not null PRIMARY KEY,
    token text  not null,
    enabled boolean not null,
    expirationTime text not null,
    created_at timestamptz DEFAULT NOW(),
    updated_at timestamptz DEFAULT NOW(),
    unique (token),
    foreign key (userId) references "user" (userId) on delete cascade on update cascade
);
