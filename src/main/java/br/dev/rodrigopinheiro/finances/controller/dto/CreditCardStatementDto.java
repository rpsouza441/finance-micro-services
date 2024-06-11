package br.dev.rodrigopinheiro.finances.controller.dto;

import br.dev.rodrigopinheiro.finances.entity.CreditCardStatement;

public record CreditCardStatementDto (

){

    public CreditCardStatement toCreditCardStatement(){}
}
