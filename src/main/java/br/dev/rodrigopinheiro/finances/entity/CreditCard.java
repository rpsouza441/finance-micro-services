package br.dev.rodrigopinheiro.finances.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "tb_credit_card")
public class CreditCard {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "name_cc", nullable = true, length = 256)
    private String name;

    @Column(name = "limit_cc", nullable = false)
    private BigDecimal limit;

    @Column(name = "closing_day", nullable = false)
    private BigDecimal closingDay;

    @Column(name = "due_date", nullable = false)
    private BigDecimal dueDay;

    @OneToMany(mappedBy = "creditCard")
    private List<Invoice> invoices;

}
