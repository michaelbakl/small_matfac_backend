create table if not exists question
(
    questionId uuid not null PRIMARY KEY,
    ownerId uuid references teacher (teacherId) on delete set null on update cascade,
    "type" question_type not null,
    title text not null,
    description text not null
);
