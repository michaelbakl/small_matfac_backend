create table if not exists picture
(
    pictureId uuid not null PRIMARY KEY,
    name text,
    picture text
);
