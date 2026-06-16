# JDBC-BASED SHOPPING CART SYSTEM

## OVERVIEW & FEATURES
- The project aims to build a menu-driven command line Shopping Cart application using JDBC.
- The system is modeled using UML Class Diagrams and object relationships translated into a relational SQL database schema using SQL DDL scripts that follow the ORM rules.
- It features transactions built by complex relational SQL queries involving joins, aggregations, subqueries, and constraint validation.
- Additionally, the transactions are atomic and feature commit/rollback mechanisms.

## UML CLASS DIAGRAM

<img width="1600" height="1428" alt="image" src="https://github.com/user-attachments/assets/7dfb08e1-fdc4-4347-ae0e-20d7e7e7a7ea" />

## DATABASE SCHEMA

## TRANSACTIONS & THEIR QUERIES
### 1. Add to Cart
- Show Products in Inventory: SELECT * FROM Product
- Check if product ID exists and requested quantity available: SELECT quantity_in_stock FROM Product WHERE product_id = ?
- Add requested quantity to the existing cart: SELECT quantity FROM Cart_Item WHERE cart_id = ? AND product_id = ?
- Update Cart: INSERT INTO Cart_Item(cart_id, product_id, quantity) VALUES (?, ?, ?) ON DUPLICATE KEY UPDATE quantity = quantity + ?

### 2. View Cart
- 

## DBMS CONCEPTS DEMONSTRATED
- 
