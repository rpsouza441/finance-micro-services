CREATE TABLE tb_bank_account
(
    id           BIGINT AUTO_INCREMENT PRIMARY KEY,
    bank_name    VARCHAR(256),
    bank_balance DECIMAL(19, 2) DEFAULT 0,
    user_id      BIGINT,
    CONSTRAINT fk_bank_account_user FOREIGN KEY (user_id) REFERENCES tb_user (id)
);
