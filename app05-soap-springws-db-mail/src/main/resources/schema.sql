create table if not exists COUNTRIES (
    COUNTRY_I serial,
    NAME text not null ,
    COUNTRY_CODE text,
    CREATE_TS timestamp null default current_timestamp
);
