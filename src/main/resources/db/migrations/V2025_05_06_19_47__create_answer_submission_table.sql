CREATE TABLE if not exists answer_submission (
    submission_id UUID PRIMARY KEY,
    session_id UUID NOT NULL REFERENCES game_session(session_id),
    question_id UUID NOT NULL REFERENCES question(questionId),
    selected_option_id UUID,
    typed_answer TEXT,
    is_correct BOOLEAN NOT NULL,
    submitted_at TIMESTAMPTZ NOT NULL
);