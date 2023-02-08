CREATE TABLE IF NOT EXISTS billkey (
	id BIGINT AUTO_INCREMENT,
	bid VARCHAR(255) NOT NULL,
	user_id INT NOT NULL,
    card_no CHAR(16) NOT NULL,
    exp_year CHAR(2) NOT NULL,
    exp_month CHAR(2) NOT NULL,
    auth_date CHAR(8) NOT NULL,
    card_code CHAR(2) NOT NULL,
    card_name VARCHAR(10) NOT NULL,
    created_date TIMESTAMP NOT NULL,
    last_modified_date TIMESTAMP NOT NULL,
	PRIMARY KEY (id)
);

ALTER TABLE billkey ADD INDEX IDX_billkey__userId_cardNo (user_id, card_no);