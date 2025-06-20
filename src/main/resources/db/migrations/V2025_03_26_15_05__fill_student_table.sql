INSERT INTO student (studentId, userId, surname, name, middleName, email, dateOfBirth, dateOfEntering)
VALUES
    ('a7e77fb1-0647-4e15-9a34-4b8d0e012345', 'b77e02d3-43a7-4a16-9a7d-0a3a11a01234', 'Николаев', 'Павел', 'Степанович', 'nikolaev@example.com', '2000-01-01T0:0:0.000000', '2020-09-01T0:0:0.000000'),
    ('a8e8a5e2-3e75-43c8-a0a1-7b0e2e012345', 'b4b066b1-9c67-4a0c-a4f9-f3031cc01234', 'Борисов', 'Степан', 'Евгеньевич', 'borisov@example.com', '2003-05-15T0:0:0.000000', '2022-09-01T0:0:0.000000'),
    ('a9d9a7a8-505a-46fd-86dd-ae5f8d012345', 'b4cfcf8c-b942-4a84-9d87-d72c32f01234', 'Кудряшова', 'Светлана', 'Васильевна', 'kudryashova@example.com', '2002-09-30T0:0:0.000000', '2021-09-01T0:0:0.000000'),

    ('95146cd0-d919-4f45-a032-8328b7847efe', 'b77e02d3-43a7-4a16-9a7d-0a3a11a01234', 'Иванов', 'Александр', 'Сергеевич', 'student1@example.com', '2000-01-01T0:0:0.000000', '2022-09-01T0:0:0.000000'),
    ('96146cd0-d919-4f45-a032-8328b7847efe', 'b5b066b1-9c67-4a0c-a4f9-f3031cc01234', 'Смирнова', 'Елена', 'Викторовна', 'student2@example.com', '2000-02-02T0:0:0.000000', '2022-09-01T0:0:0.000000'),
    ('97146cd0-d919-4f45-a032-8328b7847efe', 'b6cfcf8c-b942-4a84-9d87-d72c32f01234', 'Кузнецов', 'Дмитрий', 'Алексеевич', 'student3@example.com', '2000-03-03T0:0:0.000000', '2022-09-01T0:0:0.000000'),
    ('98146cd0-d919-4f45-a032-8328b7847efe', 'b78e02d3-43a7-4a16-9a7d-0a3a11a01234', 'Попова', 'Анастасия', 'Ивановна', 'student4@example.com', '2000-04-04T0:0:0.000000', '2022-09-01T0:0:0.000000'),
    ('99146cd0-d919-4f45-a032-8328b7847efe', 'b8b066b1-9c67-4a0c-a4f9-f3031cc01234', 'Соколов', 'Иван', 'Сергеевич', 'student5@example.com', '2000-05-05T0:0:0.000000', '2022-09-01T0:0:0.000000'),
    ('90146cd0-d919-4f45-a032-8328b7847efe', 'b9cfcf8c-b942-4a84-9d87-d72c32f01234', 'Лебедева', 'Ольга', 'Владимировна', 'student6@example.com', '2000-06-06T0:0:0.000000', '2022-09-01T0:0:0.000000'),
    ('91146cd0-d919-4f45-a032-8328b7847efe', 'b79e02d3-43a7-4a16-9a7d-0a3a11a01234', 'Козлов', 'Алексей', 'Николаевич', 'student7@example.com', '2000-07-07T0:0:0.000000', '2022-09-01T0:0:0.000000'),
    ('92146cd0-d919-4f45-a032-8328b7847efe', 'b1b066b1-9c67-4a0c-a4f9-f3031cc01234', 'Новикова', 'Мария', 'Ивановна', 'student8@example.com', '2000-08-08T0:0:0.000000', '2022-09-01T0:0:0.000000'),
    ('93146cd0-d919-4f45-a032-8328b7847efe', 'b2cfcf8c-b942-4a84-9d87-d72c32f01234', 'Морозов', 'Андрей', 'Дмитриевич', 'student9@example.com', '2000-09-09T0:0:0.000000', '2022-09-01T0:0:0.000000'),
    ('94146cd0-d919-4f45-a032-8328b7847efe', 'b80e02d3-43a7-4a16-9a7d-0a3a11a01234', 'Петров', 'Артем', 'Александрович', 'student10@example.com', '2000-10-10T0:0:0.000000', '2022-09-01T0:0:0.000000'),
    ('85146cd0-d919-4f45-a032-8328b7847efe', 'b3b066b1-9c67-4a0c-a4f9-f3031cc01234', 'Волкова', 'Анастасия', 'Валерьевна', 'student11@example.com', '2000-11-11T0:0:0.000000', '2022-09-01T0:0:0.000000'),
    ('86146cd0-d919-4f45-a032-8328b7847efe', 'b0cfcf8c-b942-4a84-9d87-d72c32f01234', 'Соловьев', 'Александр', 'Игоревич', 'student12@example.com', '2000-12-12T0:0:0.000000', '2022-09-01T0:0:0.000000')
on conflict do nothing;