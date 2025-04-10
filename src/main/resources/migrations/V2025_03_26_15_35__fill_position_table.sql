INSERT INTO position (positionId, name)
VALUES
    ('75bb8d6e-549b-4e80-a12b-0c4e82bf778a', 'Старший преподаватель'),
    ('74bb8d6e-549b-4e80-a12b-0c4e82bf778a', 'Младший преподаватель'),
    ('73bb8d6e-549b-4e80-a12b-0c4e82bf778a', 'Средний преподаватель')
on conflict do nothing;