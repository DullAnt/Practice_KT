-- Create databases for each service
CREATE DATABASE userdb;
CREATE DATABASE coursedb;
CREATE DATABASE ratingdb;
CREATE DATABASE recommendationdb;

-- Grant privileges
GRANT ALL PRIVILEGES ON DATABASE userdb TO postgres;
GRANT ALL PRIVILEGES ON DATABASE coursedb TO postgres;
GRANT ALL PRIVILEGES ON DATABASE ratingdb TO postgres;
GRANT ALL PRIVILEGES ON DATABASE recommendationdb TO postgres;
