CREATE TABLE account (
                         id SERIAL PRIMARY KEY,
                         login text not null,
                         password text not null,
                         size_storage INTEGER
);

CREATE TABLE details_object (
                                id SERIAL PRIMARY KEY,
                                account_id INTEGER,
                                object_name text not null,
                                object_type text not null,
                                object_size INTEGER not null,
                                object_location text,
                                upload_date timestamp not null,
                                FOREIGN KEY (account_id) REFERENCES account(id)
);