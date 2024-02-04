-- RUN WITH postgres USER
CREATE USER IF NOT EXISTS orders_user WITH PASSWORD 'orders';
CREATE DATABASE orders OWNER orders_user;