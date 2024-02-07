create schema if not exists "catalog";
create schema if not exists "orders";

drop view if exists "orders"."order_statistics";
drop table if exists "orders"."order_item";
drop table if exists "catalog"."item";
drop table if exists "orders"."order";

create table "catalog"."item" (
	"id" bigserial primary key,
	"barcode" char(13) not null unique,
	"title" text,
	"status" varchar(15) not null default 'CLOSED',
	"price" decimal(10,2)
);

create table "orders"."order" (
	"id" bigserial primary key,
	"created" timestamp default now(),
	"customer_name" text,
	"status" varchar(15) not null default 'PROCESSING'
);

create table "orders"."order_item" (
	"order_id" bigint,
	"item_id" bigint,
	"price" decimal(10,2) not null,
	"quantity" int not null default 1,

	foreign key ("order_id")
		references "orders"."order"("id")
		on delete cascade,
	foreign key ("item_id")
		references "catalog"."item"("id")
		on delete restrict,
	primary key ("order_id", "item_id")
);