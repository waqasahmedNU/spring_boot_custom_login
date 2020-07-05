CREATE TABLE user_authentication (
    user_id INT(10) NOT NULL,
    issued_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    expired_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    api_key VARCHAR(64) NOT NULL,
    PRIMARY KEY (user_id, api_key),
    FOREIGN KEY (user_id) REFERENCES user(id)
);