# JDBC-BASED SHOPPING CART SYSTEM

## OVERVIEW & FEATURES
- The project aims to build a menu-driven command line Shopping Cart application using JDBC.
- The system is modeled using UML Class Diagrams and object relationships translated into a relational SQL database schema using SQL DDL scripts that follow the ORM rules.
- It features transactions built by complex relational SQL queries (prepared using JDBC Prepared Statements) involving joins, aggregations, subqueries, and constraint validation.
- Additionally, the transactions are atomic and feature commit/rollback mechanisms.

## UML CLASS DIAGRAM

<img width="1600" height="1428" alt="image" src="https://github.com/user-attachments/assets/7dfb08e1-fdc4-4347-ae0e-20d7e7e7a7ea" />

## DATABASE SCHEMA

- Shop(<ins>user_id</ins> , user_name)
- Product(<ins>product_id</ins> , product_name, quantity_in_stock, price)
- Wallet(<ins>wallet_id</ins> , user_id, balance)
- Cart(<ins>cart_id</ins> , user_id)
- Cart_Item(<ins>cart_id, product_id</ins> , quantity)
