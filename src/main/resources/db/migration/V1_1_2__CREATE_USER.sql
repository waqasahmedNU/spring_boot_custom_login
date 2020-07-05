CREATE TABLE user(
    id INTEGER NOT NULL AUTO_INCREMENT,
    role_id INTEGER NOT NULL,
    name varchar(64) NOT NULL,
    email varchar(64) NOT NULL,
    username varchar(16) NOT NULL,
    password varchar(64) NOT NULL,
    active BOOLEAN NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NULL,
    deleted_at TIMESTAMP NULL,
    PRIMARY KEY (id),
    UNIQUE KEY username (username),
    FOREIGN KEY (role_id) REFERENCES user_roles(id)
)ENGINE=InnoDB DEFAULT CHARSET=utf8;