CREATE DATABASE IF NOT EXISTS dict_app;

USE dict_app;

CREATE TABLE IF NOT EXISTS Terms (
    id INT NOT NULL AUTO_INCREMENT,
    term VARCHAR(255) NOT NULL,
    description VARCHAR(1000),
    PRIMARY KEY (id),
    UNIQUE (term)
);

INSERT INTO Terms (term, description) VALUES
('PNR', 'Passenger Name Record'),
('IATA', 'The International Air Transport Association'),
('IFG','IATA Financial Gateway'),
('NDC','New Distribution Capability');