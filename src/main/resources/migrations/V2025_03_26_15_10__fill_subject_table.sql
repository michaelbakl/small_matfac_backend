INSERT INTO subject (subjectId, name)
VALUES
    ('598c0433-d87f-4953-afee-c3990fbacae0', 'Философия'),
    ('2fc924bf-f2b8-4e85-a163-0eac3779c45c', 'Физика'),
    ('35c924cf-f2b8-4e85-a163-0eac3779c45c', 'Математика') on conflict do nothing ;