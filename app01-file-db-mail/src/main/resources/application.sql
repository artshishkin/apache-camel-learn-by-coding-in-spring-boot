create table ITEMS (
    ITEM_I serial,
    SKU text not null ,
    ITEMS_DESCRIPTION text default null,
    PRICE numeric(5,2),
    CREATE_TS timestamp null default current_timestamp
);
