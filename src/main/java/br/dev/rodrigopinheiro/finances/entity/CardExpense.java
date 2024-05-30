package br.dev.rodrigopinheiro.finances.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.Date;
import java.util.UUID;

@Entity
@Table(name = "tb_card_expenses")
public class CardExpense {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "expense_description", length = 256)
    private String description = "";

    @Column(name = "expense_observation", length = 256)
    private String observation = "";

    @Column(name = "expense_value", nullable = false)
    private BigDecimal value = BigDecimal.ZERO;

    @Column(name = "recurring", nullable = false)
    private boolean recurring = false;

    @Column(name = "paid", nullable = false)
    private boolean paid = false;

    @Temporal(TemporalType.DATE)
    @Column(name = "month_year_invoice", nullable = false)
    private Date monthYearInvoice = null;

    @Temporal(TemporalType.DATE)
    @Column(name = "record_date", nullable = false)
    private Date recordDate = null;

    @ManyToOne
    @JoinColumn(name = "categoria_id")
    private Category category;

    @ManyToOne
    @JoinColumn(name = "fatura_id")
    private Fatura fatura;

}
