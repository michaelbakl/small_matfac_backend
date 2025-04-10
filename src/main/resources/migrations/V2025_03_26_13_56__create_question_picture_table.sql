create table if not exists question_picture
(
    questionId uuid,
    pictureId uuid,
    PRIMARY KEY (questionId, pictureId),
    foreign key (questionId) references question (questionId) on delete cascade on update cascade,
    foreign key (pictureId) references picture (pictureId) on delete cascade on update cascade
);
