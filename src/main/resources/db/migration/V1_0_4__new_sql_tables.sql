create table if not exists  product(
    product_id bigint primary key not null,
    name       varchar null
);

create table if not exists  client(
    client_id   bigint primary key not null,
    hash_client varchar null
);

create table if not exists  client_has_product(
    client_client_id  bigint NOT NULL,
    product_product_id bigint NOT NULL,
    PRIMARY KEY (client_client_id, product_product_id),
    CONSTRAINT fk_client FOREIGN KEY (client_client_id) REFERENCES client (client_id),
    CONSTRAINT fk_product FOREIGN KEY (product_product_id) REFERENCES product (product_id)
);