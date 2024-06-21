CREATE TABLE tb_credit_card
(
    id               BIGINT AUTO_INCREMENT PRIMARY KEY,
    credit_card_name VARCHAR(256),
    limit_amount     DECIMAL(19, 2) NOT NULL,
    closing_day      DATE           NOT NULL,
    due_date         DATE           NOT NULL,
    user_id          BIGINT,
    CONSTRAINT fk_credit_card_user FOREIGN KEY (user_id) REFERENCES tb_user (id)
);
