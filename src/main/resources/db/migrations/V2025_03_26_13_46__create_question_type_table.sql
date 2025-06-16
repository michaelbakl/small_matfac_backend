DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM pg_type WHERE typname = 'question_type'
    ) THEN
        EXECUTE 'CREATE TYPE question_type AS ENUM (
            ''SINGLE_CHOICE'',
            ''MULTIPLE_CHOICE'',
            ''OPEN_ANSWER''
        )';
    END IF;
END
$$;
