create table if not exists answer_picture
(
    answerId uuid,
    pictureId uuid,
    PRIMARY KEY (answerId, pictureId),
    foreign key (answerId) references answer (answerId) on delete cascade on update cascade,
    foreign key (pictureId) references picture (pictureId) on delete cascade on update cascade
);
