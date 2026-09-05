INSERT INTO users (username, password)
VALUES ('testuser', 'some_hash');

INSERT INTO tasks (header, text, owner_id, task_status)
VALUES ('Test task', 'Test text', 1, 'CREATED');

DROP SCHEMA public CASCADE;
CREATE SCHEMA public;