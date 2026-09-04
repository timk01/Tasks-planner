CREATE TYPE task_status_enum_type AS ENUM('CREATED', 'IN_PROCESS', 'DONE');

CREATE TABLE tasks (
    task_id BIGSERIAL PRIMARY KEY,
    header VARCHAR(60) NOT NULL,
    text TEXT NOT NULL,
    task_status task_status_enum_type NOT NULL,
    completion_time TIMESTAMPTZ,
    owner_id BIGINT NOT NULL,

    CONSTRAINT fk_tasks_user
        FOREIGN KEY (owner_id)
            REFERENCES users(user_id)
);