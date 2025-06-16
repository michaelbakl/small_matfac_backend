INSERT INTO "group" (groupId, name, dateOfCreating, classNum)
VALUES
    ('4989c153-9d73-4c9b-a516-32a20778c426', 'ММБ-000', current_timestamp, 5),
    ('5989c153-9d73-4c9b-a516-32a20778c426', 'ММБ-001', current_timestamp, 6),
    ('6989c153-9d73-4c9b-a516-32a20778c426', 'ММБ-002', current_timestamp, 7)
on conflict do nothing;