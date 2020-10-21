create table if not exists COUNTRY (
    COUNTRY_I serial,
    NAME text not null ,
    COUNTRY_CODE text,
    POPULATION numeric,
    CREATE_TS timestamp null default current_timestamp
);
