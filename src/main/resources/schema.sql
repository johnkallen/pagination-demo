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

-- Run App first to demo how indexing affects speed, before running below index commands.

-- *** INDEXES ***
--    For JOIN performance
CREATE INDEX idx_phones_user_id ON phones(user_id);
CREATE INDEX idx_addresses_user_id ON addresses(user_id);

-- For sort-based pagination (if applicable)
CREATE INDEX idx_users_id ON users(id);
-- if your query uses filtering like WHERE city = 'NY', you should also index addresses.city.

-- If your query uses WHERE user_id = ? AND phone_number = ? then create a composite index
CREATE INDEX idx_phones_user_id_phone_number ON phones(user_id, phone_number);


-- Materialized View
CREATE MATERIALIZED VIEW user_details_mv AS
SELECT
    u.id AS user_id,
    u.username,
    u.created_at,
    p.phone_number,
    a.street,
    a.city,
    a.state,
    a.zip_code
FROM users u
LEFT JOIN phones p ON u.id = p.user_id
LEFT JOIN addresses a ON u.id = a.user_id;

CREATE INDEX idx_user_details_mv_user_id ON user_details_mv(user_id);

-- Materialized views don’t auto-update (a cron job has to refresh it or you can manually refresh with below).
REFRESH MATERIALIZED VIEW user_details_mv;

@Query(value = "SELECT * FROM user_details_mv ORDER BY user_id LIMIT :limit OFFSET :offset", nativeQuery = true)
List<UserDetailsDto> findFromMaterializedView(@Param("limit") int limit, @Param("offset") int offset);

