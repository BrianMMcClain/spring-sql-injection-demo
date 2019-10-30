CREATE TABLE IF NOT EXISTS product (
	id int NOT NULL auto_increment,
	name varchar(64),
	price real,
    PRIMARY KEY (id)
);

INSERT INTO product (id, name, price) VALUES (1, 'Football', 19.99);
INSERT INTO product (id, name, price) VALUES (2, 'Baseball', 14.99);
INSERT INTO product (id, name, price) VALUES (3, 'Basketball', 17.99);
INSERT INTO product (id, name, price) VALUES (4, 'Soccer Ball', 18.99);
INSERT INTO product (id, name, price) VALUES (5, 'Helmet', 24.99);