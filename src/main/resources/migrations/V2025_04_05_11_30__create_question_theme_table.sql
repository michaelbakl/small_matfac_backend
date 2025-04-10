CREATE TABLE if not exists question_theme_relations (
    questionId UUID REFERENCES question(questionId),
    themeId UUID REFERENCES question_themes(themeId),
    PRIMARY KEY (questionId, themeId)
);
