-- Users table
CREATE TABLE IF NOT EXISTS users (
  id SERIAL PRIMARY KEY,
  username VARCHAR(100),
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Phones table: linked to users
CREATE TABLE phones (
    id SERIAL PRIMARY KEY,
    user_id INT NOT NULL,
    phone_number VARCHAR(20) NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Addresses table: linked to users
CREATE TABLE addresses (
    id SERIAL PRIMARY KEY,
    user_id INT NOT NULL,
    street VARCHAR(255),
    city VARCHAR(100),
    state VARCHAR(100),
    zip_code VARCHAR(20),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Insert sample User data
INSERT INTO users (username)
SELECT 'user' || generate_series(1, 10000000);

-- Insert Linked Phone Data
INSERT INTO phones (user_id, phone_number)
SELECT u.id, '555-01' || LPAD(u.id::text, 4, '0')
FROM users u;

-- Insert Linked Address Data
INSERT INTO addresses (user_id, street, city, state, zip_code)
SELECT
  u.id,
  'Street ' || u.id,
  'City' || (u.id % 10),
  'ST',
  LPAD((10000 + u.id)::text, 5, '0')
FROM users u;

