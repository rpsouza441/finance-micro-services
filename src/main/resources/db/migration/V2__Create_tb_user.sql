CREATE TABLE tb_user
(
    id            BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_name     VARCHAR(255) NOT NULL,
    user_email    VARCHAR(255) NOT NULL,
    user_password VARCHAR(255) NOT NULL
);
