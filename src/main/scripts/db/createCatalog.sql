create schema "catalog";

create table "catalog".item (
	"id" bigserial primary key,
	"barcode" char(13) not null unique,
	"title" text,
	"status" varchar(15) not null default "CLOSED",
	"price" money
);
