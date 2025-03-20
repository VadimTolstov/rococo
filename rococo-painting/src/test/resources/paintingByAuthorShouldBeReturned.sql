CREATE TABLE IF NOT EXISTS museum (
    id UUID PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description TEXT NOT NULL
);

CREATE TABLE IF NOT EXISTS artist (
    id UUID PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    biography TEXT NOT NULL
);

INSERT INTO museum (id, title, description) VALUES
    ('555e4567-e89b-12d3-a456-426614174000', 'Louvre Museum', 'A museum in Paris');

INSERT INTO artist (id, name, biography) VALUES
    ('666e4567-e89b-12d3-a456-426614174001', 'Jean-Honoré Fragonard', 'Famous Rococo artist');

INSERT INTO painting (id, title, description, content, museum_id, artist_id) VALUES
    ('777e4567-e89b-12d3-a456-426614174000', 'The Swing', 'A famous Rococo painting', 'data:image/jpeg;base64,/9j/4AAQSkZ', '555e4567-e89b-12d3-a456-426614174000', '666e4567-e89b-12d3-a456-426614174001'),
    ('888e4567-e89b-12d3-a456-426614174002', 'Girl with a Pearl Earring', 'A masterpiece by Vermeer', 'data:image/jpeg;base64,/9j/4AAQSkZ', '555e4567-e89b-12d3-a456-426614174000', '666e4567-e89b-12d3-a456-426614174001');