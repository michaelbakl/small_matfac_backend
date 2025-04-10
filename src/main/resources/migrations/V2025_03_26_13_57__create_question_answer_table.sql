create table if not exists question_answer
(
    questionId uuid,
    answerId uuid,
    isRight boolean,
    points int,
    PRIMARY KEY (questionId, answerId),
    foreign key (questionId) references question (questionId) on delete cascade on update cascade,
    foreign key (answerId) references answer (answerId) on delete cascade on update cascade
);
