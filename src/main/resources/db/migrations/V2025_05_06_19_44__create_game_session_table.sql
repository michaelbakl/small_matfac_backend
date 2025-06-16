CREATE TABLE if not exists game_session (
    session_id UUID PRIMARY KEY,
    game_id UUID NOT NULL REFERENCES game(gameId) on delete cascade on update cascade,
    student_id UUID NOT NULL REFERENCES student(studentId) on delete cascade on update cascade,
    started_at TIMESTAMPTZ NOT NULL,
    finished_at TIMESTAMPTZ,
    current_question_index INT NOT NULL DEFAULT 0,
    UNIQUE(game_id, student_id)
);