create schema "orders";

create table "orders"."order" (
	"id" bigserial primary key,
	"created" timestamp default now(),
	"customer_name" text,
	"status" varchar(15) not null default "PROCESSING"
);

create table "orders"."order_item" (
	"order_id" bigint,
	"item_id" bigint,
	"price" money not null,
	"quantity" int not null default 1,

	foreign key ("order_id")
		references "orders"."order"("id")
		on delete cascade,
	foreign key ("item_id")
		references "catalog"."item"("id")
		on delete restrict,
	primary key ("order_id", "item_id")
);

