CREATE TABLE if not exists registration_requests (
    id UUID PRIMARY KEY,
    email text NOT NULL,
    password_hash text NOT NULL,
    surname text NOT NULL,
    name text NOT NULL,
    middle_name text,
    requested_role text NOT NULL, -- 'STUDENT', 'TEACHER'
    status VARCHAR NOT NULL DEFAULT 'PENDING', -- 'PENDING', 'APPROVED', 'REJECTED'
    submitted_at TIMESTAMP DEFAULT now(),
    reviewed_at TIMESTAMP,
    reviewer_id UUID
);
