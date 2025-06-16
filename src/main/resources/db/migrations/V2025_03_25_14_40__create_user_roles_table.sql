CREATE TABLE IF NOT EXISTS user_roles (
                         userId uuid,
                         role text,
                         primary key (userId, role),
                         foreign key (userId) references "user"(userId) on delete cascade on update cascade
);
