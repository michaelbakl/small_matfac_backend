INSERT INTO roles (name, requires_approval) VALUES
    ('STUDENT', FALSE),
    ('TEACHER', TRUE),
    ('ADMIN', TRUE),
    ('MODERATOR', TRUE)
on conflict do nothing;
