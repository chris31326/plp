 -- create and select the database
 DROP DATABASE IF EXISTS mma;
 CREATE DATABASE mma;
 USE mma;
 -- create the Product table
 CREATE TABLE FileData (
	ID INT PRIMARY KEY AUTO_INCREMENT,
	EncryptedFileName VARCHAR (300),
	OrigialPath VARCHAR (100),
	Category VARCHAR(10),
    EncryptionKey VARCHAR(100),
    IsEncrypted BINARY
);

-- create a user and grant privileges to that user
GRANT SELECT, INSERT, DELETE, UPDATE 
ON mma.*
TO mma_user@localhost
IDENTIFIED BY 'sesame';