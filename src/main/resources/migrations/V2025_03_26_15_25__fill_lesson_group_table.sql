INSERT INTO lesson_to_group (lessonId, groupId, commentary)
VALUES
    ('aa68e4df-649c-4f54-a68a-067e45f01fbc', '4989c153-9d73-4c9b-a516-32a20778c426', ''),
    ('056e3d6b-6fad-492c-8d82-9a6f86befaba', '4989c153-9d73-4c9b-a516-32a20778c426', ''),
    ('e2b81d97-6ff1-4a8e-b5ef-f0c17a0f796b', '4989c153-9d73-4c9b-a516-32a20778c426', '')
on conflict do nothing;