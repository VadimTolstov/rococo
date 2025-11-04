create extension if not exists "uuid-ossp";

-- Создание таблицы user
CREATE TABLE IF NOT EXISTS "user" (
    id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    username VARCHAR(255) NOT NULL UNIQUE,
    firstname VARCHAR(255),
    lastname VARCHAR(255),
    avatar BYTEA
);