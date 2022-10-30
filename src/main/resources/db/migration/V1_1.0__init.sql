create table if not exists "phone_info"(
    "id" uuid primary key default gen_random_uuid(),
    "brand" text,
    "device" text,
    "band" text,
    "who_booked" text,
    "status" text default 'AVAILABLE',
    "created_at" timestamp default now(),
    "updated_at" timestamp default now()
);

insert into "phone_info"(brand, device, band)
values ('Samsung', 'Galaxy S9', 'LTE'),
       ('Samsung', 'Galaxy S8', '4G'),
       ('Samsung', 'Galaxy S8', '4G'),
       ('Motorola', 'Nexus 6', 'LTE'),
       ('Apple', 'iPhone 13', '5G'),
       ('Apple', 'iPhone 12', '5G'),
       ('Apple', 'iPhone 11', '4G'),
       ('Apple', 'iPhone X', '4G'),
       ('Nokia', '3310', '2G')
