CREATE TABLE "users" (
        user_id int NOT NULL,
        username varchar(40),
        password varchar(100),
        coins int,
        elorating int,
        logged bool,
        bio varchar(255),
        img varchar(25),
        name varchar(25),
    CONSTRAINT user_id
    PRIMARY KEY (user_id)
);

create table "score" (
    score_id int not null,
    user_id int not null,
    wins int not null,
    loses int not null,
    draws int not null,
    CONSTRAINT score_id
        PRIMARY KEY (score_id)
);

CREATE TABLE "cards" (
        card_id varchar(40) NOT NULL,
        name varchar(40),
        damage NUMERIC(5,2),
        deck int,
    CONSTRAINT card_id
    PRIMARY KEY (card_id)
);

CREATE TABLE "users_ticket" (
    user_id int NOT NULL,
    card_id varchar(40) NOT NULL,
    CONSTRAINT user_ticket
    PRIMARY KEY (user_id, card_id),
    FOREIGN KEY(user_id)
    REFERENCES users(user_id),
    FOREIGN KEY(card_id)
    REFERENCES cards(card_id)
);

create table "packages" (
    package_id int NOT NULL,
    card_id varchar(40) NOT NULL,
    name varchar(40),
    damage NUMERIC(5,2),
    CONSTRAINT package_card_id
    PRIMARY KEY (package_id, card_id)
);



create sequence user_id_seq
   owned by users.user_id;

-- create sequence package_id_seq
--    owned by packages.package_id;

alter table users
   alter column user_id set default nextval('user_id_seq');

-- alter table packages
--    alter column package_id set default nextval('package_id_seq');

create sequence score_id_seq
    owned by score.score_id;

alter table score
    alter column score_id set default nextval('score_id_seq');