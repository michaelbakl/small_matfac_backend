CREATE TABLE if not exists game_questions (
    game_questions_id UUID PRIMARY KEY,
    game_id UUID NOT NULL REFERENCES game(gameId),
    question_id UUID NOT NULL REFERENCES question(questionId),
    UNIQUE(game_id, question_id)
);
