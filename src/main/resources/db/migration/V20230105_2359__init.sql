CREATE TABLE users
(
    ID       BIGSERIAL PRIMARY KEY,
    USERNAME VARCHAR(32) NOT NULL,
    PASSWORD text        not null,
    ROLE     TEXT        NOT NULL
);

CREATE TABLE tender
(
    id          bigserial primary key,
    title       text                    not null,
    description text,
    amount      bigint                  not null,
    status      text                    not null default 'NEW',
    user_id     bigint references users not null, -- author of tender
    supplier_id bigint references users
);

create table offer
(
    id          bigserial primary key,
    description text                     not null,
    price       bigint                   not null,
    status      text                     not null default 'NEW',
    supplier_id bigint references users  not null,
    tender_id   bigint references tender not null
);
