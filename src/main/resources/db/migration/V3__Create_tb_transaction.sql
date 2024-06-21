CREATE TABLE tb_transaction
(
    id                       BIGINT AUTO_INCREMENT PRIMARY KEY,
    description              VARCHAR(256),
    note                     VARCHAR(256),
    amount                   DECIMAL(19, 2) NOT NULL DEFAULT 0,
    interest                 DECIMAL(19, 2)          DEFAULT 0,
    discount                 DECIMAL(19, 2)          DEFAULT 0,
    transaction_type         VARCHAR(255)   NOT NULL,
    recurrent                BOOLEAN                 DEFAULT FALSE,
    effective                BOOLEAN                 DEFAULT FALSE,
    due_date                 DATETIME       NOT NULL,
    creation_date            DATETIME       NOT NULL,
    effectived_date          DATETIME,
    category_id              BIGINT,
    credit_card_statement_id BIGINT,
    bank_account_id          BIGINT,
    CONSTRAINT fk_transaction_category FOREIGN KEY (category_id) REFERENCES tb_category (id),
    CONSTRAINT fk_transaction_credit_card_statement FOREIGN KEY (credit_card_statement_id) REFERENCES tb_credit_card_statement (id),
    CONSTRAINT fk_transaction_bank_account FOREIGN KEY (bank_account_id) REFERENCES tb_bank_account (id)
);
