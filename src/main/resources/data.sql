INSERT INTO item(public_id, name, price, seller_id) VALUES
('ASad4JAK', 'Produto 1', 15.78, 'user1'),
('BFd4Hfew', 'Produto 2', 25, 'user1'),
('12asda2K', 'Produto 3', 11, 'user1'),
('asw13ASK', 'Produto 4', 18, 'user1'),
('KJhASDAS', 'Produto 5', 123.34, 'user2'),
('AASDJaj9', 'Produto 6', 23.12, 'user2'),
('ask9ASDA', 'Produto 7', 45.12, 'user2'),
('ASK9askm', 'Produto 8', 56.01, 'user3');

INSERT INTO store(public_id, name, owner_id) VALUES
('12k3j4kj', 'Loja 1', 'user1'),
('12aasdsj', 'Loja 2', 'user1'),
('kj876bgf', 'Loja 3', 'user2'),
('as23assd', 'Loja 4', 'user2');

INSERT INTO stock(store_id, item_id, quantity) VALUES
('12k3j4kj', 'BFd4Hfew', 3),
('12k3j4kj', 'ASad4JAK', 150),
('kj876bgf', 'KJhASDAS', 52);