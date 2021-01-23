CREATE TABLE IF NOT EXISTS Ingredient(
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(25) NOT NULL,
    type VARCHAR(10) NOT NULL
);

CREATE TABLE IF NOT EXISTS Taco(
    id BIGINT PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    createdAt TIMESTAMP NOT NULL
);

CREATE TABLE IF NOT EXISTS taco_ingredients (
    taco BIGINT NOT NULL,
    ingredient BIGINT NOT NULL
);

ALTER TABLE taco_ingredients ADD FOREIGN KEY (taco) REFERENCES Taco(id);
ALTER TABLE taco_ingredients ADD FOREIGN KEY (ingredient) REFERENCES Ingredient(id);

CREATE TABLE IF NOT EXISTS Taco_Order (
    id BIGINT PRIMARY KEY,
    deliveryName VARCHAR(50) NOT NULL,
    deliveryStreet VARCHAR(50) NOT NULL,
    deliveryCity VARCHAR(50) NOT NULL,
    deliveryState VARCHAR(2) NOT NULL,
    deliveryZip VARCHAR(10) NOT NULL,
    ccNumber VARCHAR(16) NOT NULL,
    ccExpiration VARCHAR(5) NOT NULL,
    ccCVV VARCHAR(3) NOT NULL,
    placedAt TIMESTAMP NOT NULL
);

CREATE TABLE IF NOT EXISTS Taco_Order_Tacos (
    tacoOrder BIGINT NOT NULL,
    taco BIGINT NOT NULL
);

ALTER TABLE Taco_Order_Tacos ADD FOREIGN KEY (tacoOrder) REFERENCES Taco_Order(id);
ALTER TABLE Taco_Order_Tacos ADD FOREIGN KEY (taco) REFERENCES Taco(id);