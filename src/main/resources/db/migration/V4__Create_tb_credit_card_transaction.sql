CREATE TABLE tb_credit_card_transaction
(
    id                            BIGINT AUTO_INCREMENT PRIMARY KEY,
    date                          DATETIME,
    cc_transaction_amount         DECIMAL(19, 2),
    cc_transaction_is_refunded    BOOLEAN,
    cc_transaction_installment_id VARCHAR(255),
    statement_id                  BIGINT,
    category_id                   BIGINT,
    CONSTRAINT fk_credit_card_transaction_statement FOREIGN KEY (statement_id) REFERENCES tb_credit_card_statement (id),
    CONSTRAINT fk_credit_card_transaction_category FOREIGN KEY (category_id) REFERENCES tb_category (id)
);
