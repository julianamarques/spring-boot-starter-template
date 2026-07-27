CREATE TABLE "user" (
    id UUID PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL,
    password VARCHAR(255) NOT NULL,
    active BOOLEAN NOT NULL,
    CONSTRAINT uk_email UNIQUE (email)
);

CREATE TABLE role (
    id UUID PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    CONSTRAINT uk_name UNIQUE (name)
);

CREATE TABLE roleuser (
    rolefk UUID,
    userfk UUID,
    PRIMARY KEY (rolefk, userfk),
    CONSTRAINT fk_role FOREIGN KEY (rolefk) REFERENCES role(id),
    CONSTRAINT fk_user FOREIGN KEY (userfk) REFERENCES "user"(id)
);

CREATE INDEX idx_roleuser_userfk ON roleuser(userfk);

INSERT INTO role(id, name) VALUES('d8f6b9ca-471b-45ca-a6df-a64d20e883c1', 'USER');
INSERT INTO role(id, name) VALUES('06813833-fad6-4240-8c12-992e60e26bed', 'ADMIN');
