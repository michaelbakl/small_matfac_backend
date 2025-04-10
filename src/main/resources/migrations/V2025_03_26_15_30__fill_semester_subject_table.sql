INSERT INTO semester_subject (facultyId, subjectId, semNum, actualityDate)
VALUES
    ('5134c153-9d73-4c9b-a516-32a20778c426', '598c0433-d87f-4953-afee-c3990fbacae0', 1, '2023-01-01T0:0:0.000000'),
    ('5134c153-9d73-4c9b-a516-32a20778c426', '2fc924bf-f2b8-4e85-a163-0eac3779c45c', 1, '2023-01-01T0:0:0.000000'),
    ('5134c153-9d73-4c9b-a516-32a20778c426', '2fc924bf-f2b8-4e85-a163-0eac3779c45c', 1, '2023-01-01T0:0:0.000000')
on conflict do nothing ;