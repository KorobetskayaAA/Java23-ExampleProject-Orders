-- RUN WITH postgres USER
CREATE USER orders_user WITH PASSWORD 'orders';
CREATE DATABASE orders OWNER orders_user;