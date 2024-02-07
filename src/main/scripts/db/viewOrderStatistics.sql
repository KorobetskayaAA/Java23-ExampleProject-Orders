create view orders.order_statistics as
with o as (
    select
        cast( date_trunc('month', o.created) as date) "month"
      , o.status
      , oi.item_id
      , oi.price
      , oi.quantity
      , oi.price * oi.quantity amount
    from orders."order" o
        join orders.order_item oi on o.id  = oi.order_id
)
select
    "month"
  , SUM(amount) total_amount
  , SUM(quantity) total_quantity
  , COUNT(distinct item_id) total_items_count
  , SUM(case when status = 'CANCELED'
        then amount
        else cast(0.0 as decimal(10, 2))
        end) cancel_amount
  , SUM(case when status = 'CANCELED'
        then quantity
        else 0
        end) cancel_quantity
from o
group by "month"