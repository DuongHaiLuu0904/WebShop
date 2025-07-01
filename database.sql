-- Bảng người dùng
CREATE TABLE Customers (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(50) NOT NULL,
    fullname VARCHAR(50) NOT NULL,
    email VARCHAR(50) NOT NULL,
    photo VARCHAR(500) NOT NULL DEFAULT 'https://res.cloudinary.com/djhidgxfo/image/upload/v1751388108/user_dlgoyb.png',
    token VARCHAR(50) NOT NULL
);

-- Bảng vai trò người dùng
CREATE TABLE Roles (
    id VARCHAR(10) NOT NULL,
    name VARCHAR(50) NOT NULL,
    CONSTRAINT PK_Roles PRIMARY KEY (id)
);

-- Bảng danh mục sản phẩm
CREATE TABLE Categories (
    id CHAR(4) NOT NULL,
    name VARCHAR(50) NOT NULL,
    note VARCHAR(255) NOT NULL,
    description VARCHAR(255) NOT NULL,
    CONSTRAINT PK_Categories PRIMARY KEY (id)
);

-- Bảng sản phẩm
CREATE TABLE Products (
    id INT AUTO_INCREMENT NOT NULL,
    name VARCHAR(50) NOT NULL,
    image VARCHAR(500) NOT NULL DEFAULT 'https://res.cloudinary.com/djhidgxfo/image/upload/v1750748027/cloud-upload_c6zitf.jpg',
    price FLOAT NOT NULL DEFAULT 0,
    quantity INT NOT NULL,
    createDate DATE NOT NULL DEFAULT (CURRENT_DATE),
    available BOOLEAN NOT NULL DEFAULT 1,
    categoryId CHAR(4) NOT NULL,
    CONSTRAINT PK_Products PRIMARY KEY (id),
    CONSTRAINT FK_Products_Categories FOREIGN KEY (categoryId) REFERENCES Categories (id) ON DELETE CASCADE
);

-- Bảng đơn hàng
CREATE TABLE Orders (
    id BIGINT AUTO_INCREMENT NOT NULL,
    customerId INT NOT NULL,
    createDate DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    address VARCHAR(100) NOT NULL,
    paymentStatus VARCHAR(20) DEFAULT 'PENDING',
    vnpayTransactionId VARCHAR(100) NULL,
    totalAmount DOUBLE NULL,
    CONSTRAINT PK_Orders PRIMARY KEY (id),
    CONSTRAINT FK_Orders_Customers FOREIGN KEY (customerId) REFERENCES Customers (id) ON UPDATE CASCADE ON DELETE CASCADE
);

-- Bảng chi tiết đơn hàng
CREATE TABLE OrderDetails (
    id BIGINT AUTO_INCREMENT NOT NULL,
    orderId BIGINT NOT NULL,
    productId INT NOT NULL,
    price FLOAT NOT NULL DEFAULT 0,
    quantity INT NOT NULL DEFAULT 1,
    CONSTRAINT PK_OrderDetails PRIMARY KEY (id),
    CONSTRAINT FK_OrderDetails_Orders FOREIGN KEY (orderId) REFERENCES Orders (id) ON UPDATE CASCADE ON DELETE CASCADE,
    CONSTRAINT FK_OrderDetails_Products FOREIGN KEY (productId) REFERENCES Products (id) ON UPDATE CASCADE ON DELETE CASCADE
);

-- Bảng phân quyền người dùng
CREATE TABLE Authorities (
    id INT AUTO_INCREMENT NOT NULL,
    customerId INT NOT NULL,
    roleId VARCHAR(10) NOT NULL,
    CONSTRAINT PK_UserRoles PRIMARY KEY (id),
    CONSTRAINT FK_Authorities_Customers FOREIGN KEY (customerId) REFERENCES Customers (id) ON UPDATE CASCADE ON DELETE CASCADE,
    CONSTRAINT FK_Authorities_Roles FOREIGN KEY (roleId) REFERENCES Roles (id) ON UPDATE CASCADE ON DELETE CASCADE
);

-- Bảng giỏ hàng (thêm mới)
CREATE TABLE CartItems (
    customerId INT NOT NULL,
    product_id INT NOT NULL,
    quantity INT NOT NULL DEFAULT 1,
    price FLOAT NOT NULL,
    added_date DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (customerId, product_id),
    FOREIGN KEY (customerId) REFERENCES Customers(id) ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY (product_id) REFERENCES Products(id) ON DELETE CASCADE ON UPDATE CASCADE
);

-- Bảng sản phẩm yêu thích
CREATE TABLE Favorites (
    customerId INT NOT NULL,
    productId INT NOT NULL,
    added_date DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (customerId, productId),
    FOREIGN KEY (customerId) REFERENCES Customers(id) ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY (productId) REFERENCES Products(id) ON DELETE CASCADE ON UPDATE CASCADE
);

INSERT INTO Roles (id, name)
VALUES
    ('DIRE', 'Directors'),
    ('STAF', 'Staffs'),
    ('CUST', 'Customers');

INSERT INTO Categories (id, name, note, description) VALUES
    ('1000', 'Đồng hồ đeo tay', 'Đồng hồ', 'Đồng hồ đeo tay chất lượng cao'),
    ('1001', 'Máy tính xách tay', 'Máy tính', 'Các loại máy tính xách tay hiện đại'),
    ('1002', 'Máy ảnh', 'Máy ảnh', 'Máy ảnh chụp hình chuyên nghiệp và nghiệp dư'),
    ('1003', 'Điện thoại', 'Điện thoại', 'Điện thoại di động thông minh'),
    ('1004', 'Nước hoa', 'Nước hoa', 'Nước hoa thơm ngát các loại'),
    ('1005', 'Nữ trang', 'Nữ trang', 'Nữ trang sang trọng và tinh xảo'),
    ('1006', 'Nón thời trang', 'Nón', 'Nón thời trang đa dạng kiểu dáng'),
    ('1007', 'Túi xách du lịch', 'Túi xách', 'Túi xách du lịch bền bỉ và tiện lợi');

INSERT INTO Customers (id, username, password, fullname, email, photo, token) VALUES
    (1, 'admin', '123', 'Dương Hải Lưu', 'dluu57328@gmail.com', 'user.png', 'token'),
    (2, 'staff', '123', 'Lê Thị Đậu', 'duyplus1999@gmail.com', 'user.png', 'token'),
    (3, 'user', '123', 'Nguyễn Văn Tèo', 'duyplus0909@gmail.com', 'user.png', 'token'),
    (4, 'anatr', 'anatr', 'Ana Trujillo', 'anatr@gmail.com', 'user.png', 'token'),
    (5, 'anton', 'anton', 'Antonio Moreno', 'anton@gmail.com', 'user.png', 'token'),
    (6, 'arout', 'arout', 'Thomas Hardy', 'arout@gmail.com', 'user.png', 'token'),
    (7, 'bergs', 'bergs', 'Christina Berglund', 'bergs@gmail.com', 'user.png', 'token'),
    (8, 'blaus', 'blaus', 'Hanna Moos', 'blaus@gmail.com', 'user.png', 'token'),
    (9, 'blonp', 'blonp', 'Frédérique Citeaux', 'blonp@gmail.com', 'user.png', 'token'),
    (12, 'bottm', 'bottm', 'Elizabeth Lincoln', 'bottm@gmail.com', 'user.png', 'token'),
    (13, 'bsbev', 'bsbev', 'Victoria Ashworth', 'bsbev@gmail.com', 'user.png', 'token'),
    (14, 'cactu', 'cactu', 'Patricio Simpson', 'cactu@gmail.com', 'user.png', 'token'),
    (15, 'centc', 'centc', 'Francisco Chang', 'centc@gmail.com', 'user.png', 'token'),
    (16, 'chops', 'chops', 'Yang Wang', 'chops@gmail.com', 'user.png', 'token'),
    (17, 'commi', 'commi', 'Pedro Afonso', 'commi@gmail.com', 'user.png', 'token'),
    (18, 'consh', 'consh', 'Elizabeth Brown', 'consh@gmail.com', 'user.png', 'token'),
    (19, 'dracd', 'dracd', 'Sven Ottlieb', 'dracd@gmail.com', 'user.png', 'token'),
    (20, 'dumon', 'dumon', 'Janine Labrune', 'dumon@gmail.com', 'user.png', 'token'),
    (21, 'eastc', 'eastc', 'Ann Devon', 'eastc@gmail.com', 'user.png', 'token'),
    (22, 'ernsh', 'ernsh', 'Roland Mendel', 'ernsh@gmail.com', 'user.png', 'token'),
    (23, 'famia', 'famia', 'Aria Cruz', 'famia@gmail.com', 'user.png', 'token'),
    (24, 'fissa', 'fissa', 'Diego Roel', 'fissa@gmail.com', 'user.png', 'token'),
    (25, 'folig', 'folig', 'Martine Rancé', 'folig@gmail.com', 'user.png', 'token');

INSERT INTO Authorities (id, customerId, roleId) VALUES
    (1, 1, 'DIRE'),
    (2, 1, 'STAF'),
    (3, 1, 'CUST'),
    (4, 2, 'STAF'),
    (5, 2, 'CUST'),
    (6, 3, 'CUST'),
    (7, 3, 'STAF'),
    (10, 4, 'CUST'),
    (11, 5, 'CUST'),
    (12, 6, 'CUST'),
    (13, 7, 'CUST'),
    (14, 8, 'CUST'),
    (15, 9, 'CUST'),
    (16, 12, 'CUST'),
    (17, 13, 'CUST'),
    (18, 14, 'CUST'),
    (19, 15, 'CUST'),
    (20, 16, 'CUST'),
    (21, 17, 'CUST'),
    (22, 18, 'CUST'),
    (23, 19, 'CUST'),
    (24, 20, 'CUST'),
    (25, 21, 'CUST'),
    (26, 22, 'CUST');

INSERT INTO Products (id, name, image, price, quantity, createDate, available, categoryId) VALUES
    (1001, 'Aniseed Syrup', 'https://res.cloudinary.com/djhidgxfo/image/upload/v1750695649/images/fsrdtvizfdlcvczw0ppe.jpg', 190.0, 1, '1980-03-29', 1, '1000'),
    (1002, 'Change', 'https://res.cloudinary.com/djhidgxfo/image/upload/v1750696012/images/gulmnwabn4lhfxtjim5r.jpg', 19.0, 3, '2025-06-23', 1, '1000'),
    (1003, 'Aniseed Syrup', 'https://res.cloudinary.com/djhidgxfo/image/upload/v1750696043/images/fquabyzc5ehcoltbinfr.jpg', 10.0, 5, '2025-06-23', 1, '1001'),
    (1004, 'Chef Cajun Seasoning', 'https://res.cloudinary.com/djhidgxfo/image/upload/v1750696061/images/lmoap4oqklay6d7sosbn.jpg', 22.0, 5, '2025-06-23', 1, '1001'),
    (1005, 'Chef Antons Gumbo Mix', 'https://res.cloudinary.com/djhidgxfo/image/upload/v1750696077/images/p72vszd9vtq5ny5ah5ck.jpg', 21.35, 5, '2025-06-23', 1, '1002'),
    (1006, 'Grandmas Boysenberry', 'https://res.cloudinary.com/djhidgxfo/image/upload/v1750696132/images/geugldro87b7dd6oi68n.jpg', 25.0, 2, '2025-06-23', 1, '1001'),
    (1007, 'Uncle Bobs Dried', 'https://res.cloudinary.com/djhidgxfo/image/upload/v1750696147/images/d9fuybdn08upvkvmmnnb.jpg', 30.0, 2, '2025-06-23', 1, '1006'),
    (1008, 'Northwood Cranberry', 'https://res.cloudinary.com/djhidgxfo/image/upload/v1750696169/images/r9b6yb0ed5yp6rsrnhby.jpg', 40.0, 2, '2025-06-23', 1, '1001'),
    (1009, 'Mishi Kobe Niku', 'https://res.cloudinary.com/djhidgxfo/image/upload/v1750696191/images/koz0bhuggzllwja6pc0e.jpg', 97.0, 29, '2025-06-23', 1, '1005'),
    (1010, 'Ikura', 'https://res.cloudinary.com/djhidgxfo/image/upload/v1750696246/images/adgolcvamozirxvtottq.jpg', 31.0, 31, '2025-06-23', 1, '1007'),
    (1011, 'Queso Cabrales', 'https://res.cloudinary.com/djhidgxfo/image/upload/v1750696274/images/f21b5npdn5knrox8g39s.jpg', 30.0, 5, '2025-06-23', 1, '1003'),
    (1012, 'Queso Manchego', 'https://res.cloudinary.com/djhidgxfo/image/upload/v1750696291/images/vckkpjtlx3ta5ar1kgp3.jpg', 38.0, 86, '2025-06-23', 1, '1003'),
    (1013, 'Konbu', 'https://res.cloudinary.com/djhidgxfo/image/upload/v1750696313/images/akw9vvatt9uu7p2q244w.jpg', 6.0, 24, '2025-06-23', 1, '1007'),
    (1014, 'Tofu', 'https://res.cloudinary.com/djhidgxfo/image/upload/v1750696335/images/n0mafzafk5xtpqfbzqrp.jpg', 23.25, 35, '2025-06-23', 1, '1006'),
    (1015, 'Genen Shouyu', 'https://res.cloudinary.com/djhidgxfo/image/upload/v1750696353/images/t2bqtum4qbcbzoseznsy.jpg', 15.5, 39, '2025-06-23', 1, '1001'),
    (1016, 'Pavlova', 'https://res.cloudinary.com/djhidgxfo/image/upload/v1750696372/images/uz1ksvmfyfbvhsb6ke2x.jpg', 17.45, 29, '2025-06-23', 0, '1002'),
    (1017, 'Alice Mutton', 'https://res.cloudinary.com/djhidgxfo/image/upload/v1750696394/images/fzftrjb8rhuotplvx8it.jpg', 39.0, 0, '2025-06-23', 1, '1005'),
    (1018, 'Carnarvon Tigers', 'https://res.cloudinary.com/djhidgxfo/image/upload/v1750696414/images/uokwmf7pdkzqsrqlbshy.jpg', 62.5, 42, '2025-06-23', 1, '1007'),
    (1019, 'Teatime Chocolate Biscuits', 'https://res.cloudinary.com/djhidgxfo/image/upload/v1750696436/images/bwmvpypb7gdlpme0xls3.jpg', 9.2, 25, '2025-06-23', 1, '1002'),
    (1020, 'Sir Rodney Scones', 'https://res.cloudinary.com/djhidgxfo/image/upload/v1750696460/images/azq9fomwmmifpn2jdiah.jpg', 10.0, 0, '2025-06-23', 1, '1002'),
    (1021, 'Gustaf S Knackebrod', 'https://res.cloudinary.com/djhidgxfo/image/upload/v1750696740/images/mv3wlo5zx8mzizkwdg4p.jpg', 21.0, 76, '2025-06-23', 1, '1002'),
    (1022, 'Tunnbrod', 'https://res.cloudinary.com/djhidgxfo/image/upload/v1750696757/images/f1mhhv6yxoqvgwoiymtf.jpg', 9.0, 67, '2025-06-23', 1, '1003'),
    (1023, 'Guarana Fantastica', 'https://res.cloudinary.com/djhidgxfo/image/upload/v1750696782/images/jbdnjevnusn4wbkxqufw.jpg', 4.5, 20, '2025-06-23', 1, '1004'),
    (1024, 'NuNuCa Nuß-Nougat', 'https://res.cloudinary.com/djhidgxfo/image/upload/v1750696805/images/kze4ldnpjdfdmrwp0cag.jpg', 14.0, 39, '2006-02-19', 1, '1000'),
    (1025, 'Gumbars', 'https://res.cloudinary.com/djhidgxfo/image/upload/v1750696828/images/zxfiabtmavrzgf9hqggj.jpg', 31.23, 15, '2025-06-23', 1, '1002'),
    (1026, 'Schoggi Schokolade', 'https://res.cloudinary.com/djhidgxfo/image/upload/v1750696886/images/ixopvbrymzynfqy0u9nb.jpg', 43.9, 49, '2025-06-23', 1, '1002'),
    (1027, 'Rosenblum Bratwurst', 'https://res.cloudinary.com/djhidgxfo/image/upload/v1750696919/images/ydnsr1f8m7t428iq5ffj.jpg', 17.45, 0, '2025-06-23', 1, '1002'),
    (1028, 'Thuringer Rostbratwurst', 'https://res.cloudinary.com/djhidgxfo/image/upload/v1750696937/images/xpsjgqh874nlkzfq25io.jpg', 12.5, 32, '2025-06-23', 1, '1006'),
    (1029, 'Sasquatch Ale', 'https://res.cloudinary.com/djhidgxfo/image/upload/v1750696952/images/wl82jpzji870ys4czppm.jpg', 14.0, 6, '2025-06-23', 1, '1005'),
    (1030, 'Steeleye Stout', 'https://res.cloudinary.com/djhidgxfo/image/upload/v1750696968/images/et9wammvikgr8mekknti.jpg', 18.0, 55, '2025-06-23', 1, '1007'),
    (1031, 'Inlagd Sill', 'https://res.cloudinary.com/djhidgxfo/image/upload/v1750697013/images/enimozs2eappkktdwbli.jpg', 19.0, 42, '2025-06-23', 1, '1003'),
    (1032, 'Gravad lax', 'https://res.cloudinary.com/djhidgxfo/image/upload/v1750697038/images/ucqs6pwqtv7qldhd0oej.jpg', 26.0, 21, '2025-06-23', 1, '1003'),
    (1033, 'Cote de Blaye', 'https://res.cloudinary.com/djhidgxfo/image/upload/v1750697058/images/gatzafahzxudixh2l1xt.jpg', 263.5, 0, '2025-06-23', 1, '1002'),
    (1034, 'Chartreuse verte', 'https://res.cloudinary.com/djhidgxfo/image/upload/v1750697077/images/gxhmx4rpu5kbojre67pl.jpg', 18.0, 25, '2025-06-23', 1, '1000'),
    (1035, 'Boston Crab Meat', 'https://res.cloudinary.com/djhidgxfo/image/upload/v1750697094/images/xrnvihflxxaxwfobujet.jpg', 18.4, 123, '2025-06-23', 1, '1000'),
    (1036, 'Jack s New England Clam Chowder', 'https://res.cloudinary.com/djhidgxfo/image/upload/v1750697114/images/fxzuputxu0hnojxnlmru.jpg', 9.65, 85, '2025-06-23', 1, '1007'),
    (1037, 'Singaporean Hokkien Fried Mee', 'https://res.cloudinary.com/djhidgxfo/image/upload/v1750697132/images/ahuqvuncdqvatzwsmlaj.jpg', 12.0, 26, '2025-06-23', 1, '1007'),
    (1038, 'Ipoh Coffee', 'https://res.cloudinary.com/djhidgxfo/image/upload/v1750697160/images/rlafhoxgmksidj6b97ms.jpg', 46.0, 17, '2025-06-23', 1, '1000'),
    (1039, 'Gula Malacca', 'https://res.cloudinary.com/djhidgxfo/image/upload/v1750697179/images/txyo63u8mb0tvd0jolyu.jpg', 19.45, 27, '2025-06-23', 1, '1000'),
    (1040, 'Rogede sild', 'https://res.cloudinary.com/djhidgxfo/image/upload/v1750697195/images/oagufp7izv6qbxthssfj.jpg', 9.5, 5, '2025-06-23', 1, '1007'),
    (1041, 'Spegesild', 'https://res.cloudinary.com/djhidgxfo/image/upload/v1750697252/images/avsvhxoybtbu8uof219v.jpg', 12.0, 95, '2025-06-23', 1, '1007'),
    (1042, 'Zaanse koeken', 'https://res.cloudinary.com/djhidgxfo/image/upload/v1750697266/images/qiwe9u8nkzwfsppyw16z.jpg', 9.5, 36, '2025-06-23', 1, '1005'),
    (1043, 'Chocolade', 'https://res.cloudinary.com/djhidgxfo/image/upload/v1750697290/images/mpveowsbxymgzhekylox.jpg', 12.75, 15, '2025-06-23', 1, '1000'),
    (1044, 'Maxilaku', 'https://res.cloudinary.com/djhidgxfo/image/upload/v1750697306/images/km9ja5w3wvulw9spiwfi.jpg', 20.0, 10, '2025-06-23', 1, '1001'),
    (1045, 'Valkoinen suklaa', 'https://res.cloudinary.com/djhidgxfo/image/upload/v1750697326/images/ipgz2cijjldxmfrf00fx.jpg', 16.25, 65, '2025-06-23', 1, '1007'),
    (1046, 'Manjar blanco', 'https://res.cloudinary.com/djhidgxfo/image/upload/v1750697342/images/wuq3ngrrpjo693kzyg2x.jpg', 39.0, 23, '2025-06-23', 1, '1007'),
    (1047, 'Gnocchi di nonna Alice', 'https://res.cloudinary.com/djhidgxfo/image/upload/v1750697360/images/ailz7d7cs5rogizo5t2w.jpg', 38.0, 21, '2025-06-23', 1, '1002'),
    (1048, 'Ravioli Angelo', 'https://res.cloudinary.com/djhidgxfo/image/upload/v1750697384/images/xi2ayps3pw0mybqq1rjr.jpg', 19.5, 36, '2025-06-23', 1, '1002'),
    (1049, 'Escargots de Bourgogne', 'https://res.cloudinary.com/djhidgxfo/image/upload/v1750697417/images/t47kau5qcctupqivbccc.jpg', 13.25, 62, '2025-06-23', 1, '1002'),
    (1050, 'Raclette Courdavault', 'https://res.cloudinary.com/djhidgxfo/image/upload/v1750697434/images/fjw6sxpez4cbjalbystz.jpg', 55.0, 79, '2025-06-23', 1, '1002'),
    (1051, 'Camembert Pierrot', 'https://res.cloudinary.com/djhidgxfo/image/upload/v1750697455/images/zk7eqjztmcvuzr9i4hye.jpg', 34.0, 19, '2025-06-23', 1, '1006'),
    (1052, 'Sirop d erable', 'https://res.cloudinary.com/djhidgxfo/image/upload/v1750697472/images/xuesspjlbzobtatsjaer.jpg', 28.5, 113, '2025-06-23', 1, '1005'),
    (1053, 'Tarte au sucre', 'https://res.cloudinary.com/djhidgxfo/image/upload/v1750697498/images/haxtfbutp6rdtdevwwi5.jpg', 49.3, 17, '2025-06-23', 1, '1005'),
    (1054, 'Vegie-spread', 'https://res.cloudinary.com/djhidgxfo/image/upload/v1750697522/images/bcobsbsfg3rhg1mvjazg.jpg', 43.9, 24, '2025-06-23', 1, '1005'),
    (1055, 'Wimmers Koniglich', 'https://res.cloudinary.com/djhidgxfo/image/upload/v1750697537/images/fgvycyg53oobnplkabcs.jpg', 12.6, 1, '2025-06-23', 1, '1005'),
    (1056, 'Mozzarella di Giovanni', 'https://res.cloudinary.com/djhidgxfo/image/upload/v1750697552/images/npvartxkp1obijxg9lgy.jpg', 34.8, 14, '2025-06-23', 1, '1004'),
    (1057, 'Röd Kaviar', 'https://res.cloudinary.com/djhidgxfo/image/upload/v1750697570/images/ijj5si3glqfrgwndxyxn.jpg', 36.5, 60, '2025-06-23', 1, '1004'),
    (1058, 'Longlife Tofu', 'https://res.cloudinary.com/djhidgxfo/image/upload/v1750697588/images/dwyaedxbfut02xrkzboo.jpg', 10.0, 4, '2025-06-23', 1, '1001'),
    (1059, 'White Clover Honey', 'https://res.cloudinary.com/djhidgxfo/image/upload/v1750697609/images/umi5glo0dclm9xikguro.jpg', 15.75, 52, '2025-06-23', 1, '1003'),
    (1060, 'Monte Carlo', 'https://res.cloudinary.com/djhidgxfo/image/upload/v1750697628/images/uejavviy3yv6er57gwja.jpg', 7.75, 1, '2025-06-23', 1, '1003'),
    (1061, 'Alice s Kitchen Apple Pie', 'https://res.cloudinary.com/djhidgxfo/image/upload/v1750697651/images/vprrk5k2eouhyarjk7ek.jpg', 9.5, 12, '2025-06-23', 1, '1001'),
    (1062, 'Sirop d orgeat', 'https://res.cloudinary.com/djhidgxfo/image/upload/v1750697671/images/vvqahmfis7fgbvqi98an.jpg', 18.0, 5, '2025-06-23', 1, '1002'),
    (1063, 'Queen of Puddings', 'https://res.cloudinary.com/djhidgxfo/image/upload/v1750697688/images/k7u5wghn0vsch8infkk0.jpg', 18.4, 20, '2025-06-23', 1, '1001'),
    (1064, 'Wimmers Semmelknadel', 'https://res.cloudinary.com/djhidgxfo/image/upload/v1750697712/images/be1kkwwapz80x8xv0c7b.jpg', 33.25, 7, '2025-06-23', 1, '1004'),
    (1065, 'Louisiana Fiery Sauce', 'https://res.cloudinary.com/djhidgxfo/image/upload/v1750697765/images/qaapvykqslob810vrqei.jpg', 21.05, 7, '2025-06-23', 1, '1001'),
    (1066, 'Louisiana Spiced Okra', 'https://res.cloudinary.com/djhidgxfo/image/upload/v1750697786/images/wbduixjghyyhtnxxajub.jpg', 17.0, 5, '2025-06-23', 1, '1001'),
    (1067, 'Laughing Lumberjack', 'https://res.cloudinary.com/djhidgxfo/image/upload/v1750697802/images/qunlfsdifqglfk1favxz.jpg', 14.0, 5, '2025-06-23', 1, '1000'),
    (1068, 'Scottish Longbreads', 'https://res.cloudinary.com/djhidgxfo/image/upload/v1750697824/images/ilabewwj79o30hflvqv4.jpg', 12.5, 5, '2025-06-23', 1, '1002'),
    (1069, 'Gudbrandsdalsost', 'https://res.cloudinary.com/djhidgxfo/image/upload/v1750697841/images/e769gtb3whekgm6fb744.jpg', 36.0, 5, '2025-06-23', 1, '1003'),
    (1070, 'Outback Lager', 'https://res.cloudinary.com/djhidgxfo/image/upload/v1750697859/images/s0xxyzq6dcyccrmdjyji.jpg', 15.0, 5, '2025-06-23', 1, '1000'),
    (1071, 'Flotemysost', 'https://res.cloudinary.com/djhidgxfo/image/upload/v1750697878/images/cgspqacrdlsr8xvymboe.jpg', 21.5, 5, '2025-06-23', 1, '1003'),
    (1072, 'Mozzarella Giovanni', 'https://res.cloudinary.com/djhidgxfo/image/upload/v1750697890/images/rmwcpjkijfds7t2epofc.jpg', 34.8, 5, '2025-06-23', 1, '1003'),
    (1073, 'Rad Kaviar', 'https://res.cloudinary.com/djhidgxfo/image/upload/v1750697906/images/sc9eaaemosnwvfcoame3.jpg', 15.0, 5, '2025-06-23', 1, '1007'),
    (1074, 'Longlife Tofu', 'https://res.cloudinary.com/djhidgxfo/image/upload/v1750697925/images/abdjmhr7muqnljo8ztq7.jpg', 10.0, 5, '2025-06-23', 1, '1006'),
    (1075, 'RhanbrAu Klosterbier', 'https://res.cloudinary.com/djhidgxfo/image/upload/v1750697943/images/sdxbxvyjrqv4rsv1g9ki.jpg', 7.75, 5, '2025-06-23', 1, '1000'),
    (1076, 'Lakkalik Ari', 'https://res.cloudinary.com/djhidgxfo/image/upload/v1750697964/images/mfsb4sslnkioym746rb7.jpg', 18.0, 5, '2025-06-23', 1, '1000'),
    (1077, 'Original Frankfurter', 'https://res.cloudinary.com/djhidgxfo/image/upload/v1750698014/images/zou2i9yj4n0bmogftbrj.gif', 13.0, 5, '2025-06-24', 1, '1001');