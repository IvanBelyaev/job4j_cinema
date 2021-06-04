CREATE TABLE account (
  id SERIAL PRIMARY KEY,
  username VARCHAR NOT NULL,
  email VARCHAR NOT NULL UNIQUE,
  phone VARCHAR NOT NULL UNIQUE
);

CREATE TABLE ticket (
    id SERIAL PRIMARY KEY,
    session_id INT NOT NULL,
    row INT NOT NULL,
    cell INT NOT NULL,
    account_id INT NOT NULL REFERENCES account(id),
    UNIQUE (session_id, row, cell)
);

insert into account (id, username, email, phone) values
(1, 'user', 'user@mail.ru', '+79990000001');

insert into ticket (id, session_id, row, cell, account_id) values
(1, 1, 2, 3, 1),
(2, 1, 3, 3, 1);