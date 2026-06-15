alter table Wallet
add constraint fk_wallet
foreign key (user_id) references User(user_id) on delete cascade;

alter table Cart
add constraint fk_cart
foreign key (user_id) references User(user_id) on delete cascade;

alter table Cart_Item
add constraint fk_cartitem
foreign key (cart_id) references Cart(cart_id) on delete cascade;

alter table Cart_Item
add constraint fk_productIsIn
foreign key (product_id) references Product(product_id);