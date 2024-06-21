CREATE TABLE tb_credit_card_statement
(
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    month           INT,
    year            INT,
    payed           BOOLEAN,
    amount_due      DECIMAL(19, 2),
    amount_payed    DECIMAL(19, 2),
    effectived_date DATETIME,
    credit_card_id  BIGINT,
    CONSTRAINT fk_credit_card_statement_credit_card FOREIGN KEY (credit_card_id) REFERENCES tb_credit_card (id)
);
