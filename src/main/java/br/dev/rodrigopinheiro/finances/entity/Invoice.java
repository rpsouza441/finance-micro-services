package br.dev.rodrigopinheiro.finances.entity;

import jakarta.persistence.*;

import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "tb_fatura")
public class Invoice {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "month_invoice")
    private String month = "";

    @Column(name = "year_invoice")
    private String year = "";

    @OneToMany(mappedBy = "invoice", fetch = FetchType.LAZY)
    private List<CardExpense> cardExpenses;

    @ManyToOne
    @JoinColumn(name = "cartao_credito_id")
    private CreditCard creditCard;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "transacao_id", referencedColumnName = "id")
    private Transacao transacao;

}
