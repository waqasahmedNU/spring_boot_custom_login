CREATE TABLE user_roles(
    id INTEGER NOT NULL AUTO_INCREMENT,
    name varchar(64) NOT NULL,
    active BOOLEAN,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NULL,
    deleted_at TIMESTAMP NULL,
    PRIMARY KEY (id)
)ENGINE=InnoDB DEFAULT CHARSET=utf8;