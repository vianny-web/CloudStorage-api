CREATE database cloud_storage_db;

CREATE TABLE account (
                         id SERIAL PRIMARY KEY,
                         login text not null,
                         password text not null,
                         size_storage INTEGER
);

CREATE TABLE details_file (
                              id SERIAL PRIMARY KEY,
                              account_id INTEGER,
                              file_name text not null,
                              file_size INTEGER not null,
                              file_location text not null,
                              upload_date timestamp not null,
                              FOREIGN KEY (account_id) REFERENCES account(id)
);