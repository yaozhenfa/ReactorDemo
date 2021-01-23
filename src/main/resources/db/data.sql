DELETE FROM Taco_Order_Tacos;
DELETE FROM taco_ingredients;
DELETE FROM Taco;
DELETE FROM Taco_Order;
DELETE FROM Ingredient;

INSERT INTO Ingredient(id, name, type) values (1, 'Flour Tortilla', 'WRAP');
INSERT INTO Ingredient(id, name, type) values (2, 'Corn Tortilla', 'WRAP');
INSERT INTO Ingredient(id, name, type) values (3, 'Ground Beef', 'PROTEIN');
INSERT INTO Ingredient(id, name, type) values (4, 'Carnitas', 'PROTEIN');
INSERT INTO Ingredient(id, name, type) values (5, 'Diced Tomatoes', 'VEGGIES');
INSERT INTO Ingredient(id, name, type) values (6, 'Lettuce', 'VEGGIES');
INSERT INTO Ingredient(id, name, type) values (7, 'Cheddar', 'CHEESE');
INSERT INTO Ingredient(id, name, type) values (8, 'Monterrey Jack', 'CHEESE');
INSERT INTO Ingredient(id, name, type) values (9, 'Salsa', 'SAUCE');
INSERT INTO Ingredient(id, name, type) values (10, 'Sour Cream', 'SAUCE');