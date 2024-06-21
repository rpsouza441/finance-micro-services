CREATE TABLE tb_wallet
(
    id             BIGINT AUTO_INCREMENT PRIMARY KEY,
    wallet_balance DECIMAL(19, 2) NOT NULL,
    user_id        BIGINT,
    CONSTRAINT fk_wallet_user FOREIGN KEY (user_id) REFERENCES tb_user (id)
);
