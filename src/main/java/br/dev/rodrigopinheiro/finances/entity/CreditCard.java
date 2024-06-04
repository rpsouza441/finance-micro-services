package br.dev.rodrigopinheiro.finances.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "tb_credit_card")
public class CreditCard {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "credit_card_name", nullable = true, length = 256)
    private String name;

    @Column(name = "limit_amount", nullable = false)
    private BigDecimal limitAmount;

    @Column(name = "closing_day", nullable = false)
    private LocalDate closingDay;

    @Column(name = "due_date", nullable = false)
    private LocalDate dueDay;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "creditCard")
    private List<CreditCardStatement> statements;

    public CreditCard() {
    }

    public CreditCard(String name, BigDecimal limitAmount, LocalDate closingDay, LocalDate dueDay) {
        this.name = name;
        this.limitAmount = limitAmount;
        this.closingDay = closingDay;
        this.dueDay = dueDay;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getLimitAmount() {
        return limitAmount;
    }

    public void setLimitAmount(BigDecimal limitAmount) {
        this.limitAmount = limitAmount;
    }

    public LocalDate getClosingDay() {
        return closingDay;
    }

    public void setClosingDay(LocalDate closingDay) {
        this.closingDay = closingDay;
    }

    public LocalDate getDueDay() {
        return dueDay;
    }

    public void setDueDay(LocalDate dueDay) {
        this.dueDay = dueDay;
    }

    public List<CreditCardStatement> getStatements() {
        return statements;
    }

    public void setStatements(List<CreditCardStatement> statements) {
        this.statements = statements;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

}
