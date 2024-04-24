
CREATE TABLE Terms (
    id INT NOT NULL AUTO_INCREMENT,
    term VARCHAR(255) NOT NULL,
    description VARCHAR(1000),
    PRIMARY KEY (id)
);

INSERT INTO Terms (term, description) VALUES
('PNR', 'Passenger Name Record'),
('IATA', 'The International Air Transport Association'),
('IFG','IATA Financial Gateway'),
('NDC','New Distribution Capability');