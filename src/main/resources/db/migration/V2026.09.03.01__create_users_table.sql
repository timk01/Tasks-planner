CREATE TABLE users (
    user_id BIGSERIAL PRIMARY KEY,
    email VARCHAR(254) UNIQUE NOT NULL,
    password VARCHAR(100) NOT NULL,

    CONSTRAINT check_password_length_check CHECK (LENGTH(email) >= 6)
);