CREATE TABLE if not exists question_theme_relations (
    questionId UUID REFERENCES question(questionId) on delete cascade on update cascade,
    themeId UUID REFERENCES theme(themeId) on delete cascade on update cascade,
    PRIMARY KEY (questionId, themeId)
);
