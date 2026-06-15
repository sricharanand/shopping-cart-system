create database SHOP;
use SHOP;

create table User(  
    user_id int not null,
    user_name varchar(30) not null unique,

    constraint pk_user primary key (user_id)
);

create table Product(
	product_id int not null,
    product_name varchar(30) not null,
    quantity_in_stock int not null,
    price decimal(10,2) not null,

    constraint pk_product primary key (product_id)
);

create table Wallet(
    wallet_id int not null,
    user_id int not null unique,
    balance decimal(12,2) not null,

    constraint pk_wallet primary key (wallet_id)
);

create table Cart(
    cart_id int not null,
    user_id int not null unique,

    constraint pk_cart primary key (cart_id)
);

create table Cart_Item(
    cart_id int not null,
    product_id int not null,
    quantity int not null,

    constraint pk_cart_item primary key (cart_id, product_id)
);
