CREATE TABLE user (
                      userid INT AUTO_INCREMENT PRIMARY KEY,
                      name VARCHAR(100) NOT NULL,
                      email VARCHAR(100) NOT NULL UNIQUE,
                      password VARCHAR(255) NOT NULL,
                      phone VARCHAR(20),
                      gender VARCHAR(10),
                      address VARCHAR(255),
                      city VARCHAR(100),
                      pincode VARCHAR(20),
                      state VARCHAR(100),
                      registerdate TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
