INSERT INTO user_roles (userId, role)
VALUES
    ('76bb8d6e-549b-4e80-a12b-0c4e82bf778a', 'USER'),
    ('76bb8d6e-549b-4e80-a12b-0c4e82bf778a', 'ADMIN'),
    ('76bb8d6e-549b-4e80-a12b-0c4e82bf778a', 'TEACHER')
on conflict do nothing;