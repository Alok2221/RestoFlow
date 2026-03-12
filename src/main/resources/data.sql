/*
Queries generated with AI
*/

-- Initial data for RestoFlow (H2)

-- Restaurant tables (table_number as String)
INSERT INTO restaurant_tables (table_number, capacity, location, available, active) VALUES
('1', 2, 'SALA_GLOWNA', true, true),
('2', 2, 'SALA_GLOWNA', true, true),
('3', 4, 'SALA_GLOWNA', true, true),
('4', 4, 'SALA_GLOWNA', true, true),
('5', 6, 'SALA_GLOWNA', true, true),
('6', 2, 'TARAS', true, true),
('7', 4, 'TARAS', true, true),
('8', 4, 'TARAS', true, true),
('9', 8, 'SALON_VIP', true, true),
('10', 10, 'SALON_VIP', true, true);

-- Soups
INSERT INTO dishes (name, description, price, category, food_type, available, image_url) VALUES
('Tomato Soup', 'Homemade tomato soup with basil and croutons', 18.50, 'ZUPA', 'WEGETARIANSKIE', true, '/images/zupa_pomidorowa.png'),
('Chicken Broth Soup', 'Traditional chicken broth with noodles and vegetables', 20.00, 'ZUPA', 'NORMALNE', true, '/images/zupa_rosol.png'),
('White Sausage Sour Rye Soup', 'Sour rye soup with white sausage, egg and horseradish', 22.00, 'ZUPA', 'NORMALNE', true, '/images/zurek_z_biala_kielbasa.png'),
('Cream of Pumpkin Soup', 'Creamy pumpkin soup with croutons', 19.00, 'ZUPA', 'WEGETARIANSKIE', true, '/images/zupa_krem_z_dyni.png'),
('Red Beetroot Soup with Dumplings', 'Traditional beetroot soup with mushroom-filled dumplings', 21.00, 'ZUPA', 'WEGETARIANSKIE', true, '/images/barszcz_czerwony_z_uszkami.png'),
('Dill Pickle Soup', 'Pickle soup with potatoes and dill', 18.00, 'ZUPA', 'WEGETARIANSKIE', true, '/images/zupa_ogorkowa.png'),
('Dill Soup', 'Light dill soup with potatoes', 17.50, 'ZUPA', 'WEGETARIANSKIE', true, '/images/zupa_koperkowa.png'),
('Cream of Garlic Soup', 'Creamy garlic soup with croutons', 19.50, 'ZUPA', 'WEGETARIANSKIE', true, '/images/zupa_krem_czosnkowy.png'),
('Cream of Broccoli Soup', 'Creamy broccoli soup with croutons', 20.00, 'ZUPA', 'WEGETARIANSKIE', true, '/images/zupa_krem_z_brokulow.png'),
('Wild Mushroom Soup', 'Traditional wild mushroom soup with noodles', 21.50, 'ZUPA', 'WEGETARIANSKIE', true, '/images/zupa_grzybowa.png');

-- Starters
INSERT INTO dishes (name, description, price, category, food_type, available, image_url) VALUES
('Beef Tartare', 'Raw minced beef with egg yolk, onion and spices', 42.00, 'PRZYSTAWKA', 'NORMALNE', true, '/images/tatar_z_wolowiny.png'),
('Herring in Oil', 'Marinated herring in oil with onion', 24.00, 'PRZYSTAWKA', 'NORMALNE', true, '/images/sledz_w_oleju.png'),
('Fried Dumplings with Meat', 'Fried dumplings filled with minced meat', 32.00, 'PRZYSTAWKA', 'NORMALNE', true, '/images/pierogi_smazone_z_miesem.png'),
('Oscypek with Cranberries', 'Smoked sheep\'s cheese served with cranberry preserve', 28.00, 'PRZYSTAWKA', 'WEGETARIANSKIE', true, '/images/oscypek_z_zurawina.png'),
('Tomato Bruschetta', 'Crispy toast with tomatoes, basil and mozzarella', 26.00, 'PRZYSTAWKA', 'WEGETARIANSKIE', true, '/images/bruschetta_z_pomidorami.png'),
('Greek Salad', 'Fresh salad with feta, olives and vegetables', 30.00, 'PRZYSTAWKA', 'WEGETARIANSKIE', true, '/images/salatka_grecka.png'),
('Cold Cuts Platter', 'Selection of Polish cold cuts with bread and pickles', 38.00, 'PRZYSTAWKA', 'NORMALNE', true, '/images/deska_wedlin.png'),
('Mini Potato & Cheese Dumplings', 'Small dumplings with potatoes and cottage cheese', 28.00, 'PRZYSTAWKA', 'WEGETARIANSKIE', true, '/images/pierogi_ruskie_male.png');

-- Main Courses
INSERT INTO dishes (name, description, price, category, food_type, available, image_url) VALUES
('Pork Schnitzel', 'Breaded pork cutlet with potatoes and coleslaw', 42.00, 'DANIE_GLOWNE', 'NORMALNE', true, '/images/kotlet_schabowy.png'),
('Dumplings with Meat', 'Traditional dumplings filled with minced meat', 35.00, 'DANIE_GLOWNE', 'NORMALNE', true, '/images/pierogi_z_miesem.png'),
('Hunter\'s Stew', 'Traditional Polish bigos with cabbage and meat', 38.00, 'DANIE_GLOWNE', 'NORMALNE', true, '/images/bigos_staropolski.png'),
('Roasted Pork Knuckle', 'Roasted pork knuckle with cabbage and horseradish', 48.00, 'DANIE_GLOWNE', 'NORMALNE', true, '/images/golonka_pieczona.png'),
('Duck with Apples', 'Roasted duck with apples and potatoes', 65.00, 'DANIE_GLOWNE', 'NORMALNE', true, '/images/kaczka_z_jablkami.png'),
('Potato Pancakes with Goulash', 'Crispy potato pancakes served with beef goulash', 36.00, 'DANIE_GLOWNE', 'NORMALNE', true, '/images/placki_ziemniaczane_z_gulaszem.png'),
('Stuffed Pork Loin', 'Pork loin stuffed with dried plums', 52.00, 'DANIE_GLOWNE', 'NORMALNE', true, '/images/schab_faszerowany.png'),
('Cabbage Rolls with Rice', 'Cabbage rolls with meat and rice in tomato sauce', 40.00, 'DANIE_GLOWNE', 'NORMALNE', true, '/images/golabki_z_ryzem.png'),
('Dumplings with Sauerkraut and Mushrooms', 'Dumplings with sauerkraut and wild mushrooms', 34.00, 'DANIE_GLOWNE', 'WEGETARIANSKIE', true, '/images/pierogi_z_kapusta_i_grzybami.png'),
('Chicken de Volaille', 'Tender breaded chicken fillet with herb butter', 44.00, 'DANIE_GLOWNE', 'NORMALNE', true, '/images/kotlety_de_volaille.png');

-- Pizza
INSERT INTO dishes (name, description, price, category, food_type, available, image_url) VALUES
('Pizza Margherita', 'Classic pizza with mozzarella, tomatoes and basil', 28.00, 'PIZZA', 'WEGETARIANSKIE', true, '/images/pizza_margherita.png'),
('Pizza Capriciosa', 'Pizza with ham, mushrooms, olives and mozzarella', 34.00, 'PIZZA', 'NORMALNE', true, '/images/pizza_capriciosa.png'),
('Pizza Pepperoni', 'Pizza with pepperoni salami and mozzarella', 36.00, 'PIZZA', 'NORMALNE', true, '/images/pizza_pepperoni.png'),
('Pizza Hawaii', 'Pizza with ham, pineapple and mozzarella', 35.00, 'PIZZA', 'NORMALNE', true, '/images/pizza_hawajska.png'),
('Pizza Four Cheeses', 'Pizza with mozzarella, gorgonzola, parmesan and ricotta', 38.00, 'PIZZA', 'WEGETARIANSKIE', true, '/images/pizza_cztery_sery.png'),
('Pizza Vegetariana', 'Pizza with vegetables and mozzarella', 32.00, 'PIZZA', 'WEGETARIANSKIE', true, '/images/pizza_vegetariana.png'),
('Pizza Diavola', 'Spicy pizza with salami, chili peppers and mozzarella', 37.00, 'PIZZA', 'NORMALNE', true, '/images/pizza_diavola.png'),
('Pizza Funghi', 'Pizza with mushrooms and mozzarella', 30.00, 'PIZZA', 'WEGETARIANSKIE', true, '/images/pizza_funghi.png'),
('Pizza Ham & Mushrooms', 'Pizza with ham, mushrooms and mozzarella', 33.00, 'PIZZA', 'NORMALNE', true, '/images/pizza_szynka_pieczarki.png'),
('Kebab Pizza', 'Pizza with kebab meat, vegetables and sauce', 39.00, 'PIZZA', 'NORMALNE', true, '/images/pizza_kebab.png');

-- Soft Drinks
INSERT INTO dishes (name, description, price, category, food_type, available, image_url) VALUES
('Cola', 'Carbonated soft drink 0.5L', 8.00, 'NAPOJ_BEZ_ALKOHOLOWE', 'NORMALNE', true, '/images/cola.png'),
('Sprite', 'Lemon-lime soft drink 0.5L', 8.00, 'NAPOJ_BEZ_ALKOHOLOWE', 'NORMALNE', true, '/images/sprite.png'),
('Fanta', 'Orange-flavoured soft drink 0.5L', 8.00, 'NAPOJ_BEZ_ALKOHOLOWE', 'NORMALNE', true, '/images/fanta.png'),
('Sparkling Water', 'Sparkling mineral water 0.5L', 6.00, 'NAPOJ_BEZ_ALKOHOLOWE', 'NORMALNE', true, '/images/woda_gazowana.png'),
('Still Water', 'Still mineral water 0.5L', 6.00, 'NAPOJ_BEZ_ALKOHOLOWE', 'NORMALNE', true, '/images/woda_niegazowana.png'),
('Apple Juice', 'Fresh apple juice 0.3L', 12.00, 'NAPOJ_BEZ_ALKOHOLOWE', 'WEGANSKIE', true, '/images/sok_jablkowy.png'),
('Orange Juice', 'Fresh orange juice 0.3L', 12.00, 'NAPOJ_BEZ_ALKOHOLOWE', 'WEGANSKIE', true, '/images/sok_pomaranczowy.png'),
('Strawberry Lemonade', 'Homemade strawberry lemonade 0.4L', 14.00, 'NAPOJ_BEZ_ALKOHOLOWE', 'WEGANSKIE', true, '/images/lemoniada_truskawkowa.png'),
('Black Tea', 'Black tea with lemon or milk', 8.00, 'NAPOJ_BEZ_ALKOHOLOWE', 'WEGANSKIE', true, '/images/herbata_czarna.png'),
('Black Coffee', 'Black espresso coffee', 8.00, 'NAPOJ_BEZ_ALKOHOLOWE', 'WEGANSKIE', true, '/images/kawa_czarna.png'),
('Non-Alcoholic Beer', 'Non-alcoholic beer 0.5L', 10.00, 'NAPOJ_BEZ_ALKOHOLOWE', 'NORMALNE', true, '/images/piwo_bezalkoholowe.png');

-- Alcoholic Drinks
INSERT INTO dishes (name, description, price, category, food_type, available, image_url) VALUES
('Zubr Beer', 'Pale lager beer 0.5L', 12.00, 'NAPOJE_ALKOHOLOWE', 'NORMALNE', true, '/images/piwo_zubr.png'),
('Tyskie Beer', 'Pale lager beer 0.5L', 12.00, 'NAPOJE_ALKOHOLOWE', 'NORMALNE', true, '/images/piwo_tyskie.png'),
('Lech Beer', 'Pale lager beer 0.5L', 12.00, 'NAPOJE_ALKOHOLOWE', 'NORMALNE', true, '/images/piwo_lech.png'),
('Zubrowka Vodka', 'Zubrowka bison grass vodka 50ml', 15.00, 'NAPOJE_ALKOHOLOWE', 'NORMALNE', true, '/images/wodka_zubrowka.png'),
('Zubrowka Black Vodka', 'Zubrowka Black vodka 50ml', 16.00, 'NAPOJE_ALKOHOLOWE', 'NORMALNE', true, '/images/wodka_zubrowka_czarna.png'),
('Craft Beer', 'Craft beer 0.5L', 18.00, 'NAPOJE_ALKOHOLOWE', 'NORMALNE', true, '/images/piwo_kraftowe.png'),
('White Wine', 'Dry white wine 0.15L', 20.00, 'NAPOJE_ALKOHOLOWE', 'NORMALNE', true, '/images/wino_biale.png'),
('Red Wine', 'Dry red wine 0.15L', 20.00, 'NAPOJE_ALKOHOLOWE', 'NORMALNE', true, '/images/wino_czerwone.png'),
('Vodka Shot', 'Shot of plain vodka 50ml', 12.00, 'NAPOJE_ALKOHOLOWE', 'NORMALNE', true, '/images/shot_wodka_czysta.png');

-- Admin account (password: admin123)
INSERT INTO accounts (email, password_hash, first_name, last_name, phone, address, role, registered_at) VALUES
('admin@restoflow.pl', 'admin123', 'Admin', 'System', NULL, NULL, 'ADMIN', CURRENT_TIMESTAMP);

-- Employee account (password: worker123)
INSERT INTO accounts (email, password_hash, first_name, last_name, phone, address, role, registered_at) VALUES
('pracownik@restoflow.pl', 'worker123', 'Jan', 'Kowalski', '123456789', NULL, 'PRACOWNIK', CURRENT_TIMESTAMP);
