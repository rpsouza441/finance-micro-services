package br.dev.rodrigopinheiro.finances.controller.dto;

import java.math.BigDecimal;

public record WalletDto(
        BigDecimal ballance,
        Long userId
)  {
}
