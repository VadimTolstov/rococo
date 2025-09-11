INSERT INTO country (id, name) VALUES
('111e4567-e89b-12d3-a456-426614174000', 'France');

INSERT INTO geo (id, city, country_id) VALUES
('333e4567-e89b-12d3-a456-426614174000', 'Paris', '111e4567-e89b-12d3-a456-426614174000');

INSERT INTO museum (id, title, description, photo, geo_id) VALUES
('555e4567-e89b-12d3-a456-426614174000', 'Louvre Museum', 'Famous museum in Paris', 'data:image/jpeg;base64,/9j/4AAQSkZ', '333e4567-e89b-12d3-a456-426614174000');