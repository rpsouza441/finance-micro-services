package br.dev.rodrigopinheiro.finances.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "tb_credit_card_statement")
public class CreditCardStatement {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "month")
    private int month;

    @Column(name = "year")
    private int year;

    @Column(name = "payed")
    private boolean isPayed;

    @Column(name = "amount_due")
    private BigDecimal amountDue;

    @Column(name = "amount_payed")
    private BigDecimal amountPayed;

    @Column(name = "effectived_date")
    private LocalDateTime effectivedDate;

    @OneToMany(mappedBy = "statement", fetch = FetchType.LAZY)
    private List<CreditCardTransaction> creditCardTransactions;

    @OneToMany(mappedBy = "creditCardStatement", fetch = FetchType.LAZY)
    private List<Transaction> transactions;

    @ManyToOne
    @JoinColumn(name = "credit_card_id")
    private CreditCard creditCard;

    public CreditCardStatement() {
    }

    public CreditCardStatement(int month, int year, boolean isPayed, BigDecimal amountDue,
                               LocalDateTime effectivedDate, List<CreditCardTransaction> creditCardTransactions, CreditCard creditCard) {
        this.month = month;
        this.year = year;
        this.isPayed = isPayed;
        this.amountDue = amountDue;
        this.effectivedDate = effectivedDate;
        this.creditCardTransactions = creditCardTransactions;
        this.creditCard = creditCard;
    }

    public CreditCardStatement(int month, int year, BigDecimal amountDue, CreditCard creditCard) {
        this.month = month;
        this.year = year;
        this.amountDue = amountDue;
        this.creditCard = creditCard;
    }

    public Long getId() {
        return id;
    }

    public boolean isPayed() {
        return isPayed;
    }

    public void setPayed(boolean isPayed) {
        this.isPayed = isPayed;
    }

    public LocalDateTime getEffectivedDate() {
        return effectivedDate;
    }

    public void setEffectivedDate(LocalDateTime effectivedDate) {
        this.effectivedDate = effectivedDate;
    }

    public List<CreditCardTransaction> getCreditCardTransactions() {
        return creditCardTransactions;
    }

    public void setCreditCardTransactions(List<CreditCardTransaction> creditCardTransactions) {
        this.creditCardTransactions = creditCardTransactions;
    }

    public List<Transaction> getTransactions() {
        return transactions;
    }

    public void setTransactions(List<Transaction> transactions) {
        this.transactions = transactions;
    }

    public CreditCard getCreditCard() {
        return creditCard;
    }

    public void setCreditCard(CreditCard creditCard) {
        this.creditCard = creditCard;
    }

    public BigDecimal getAmountDue() {
        return amountDue;
    }

    public void setAmountDue(BigDecimal amountDue) {
        this.amountDue = amountDue;
    }

    public BigDecimal getAmountPayed() {
        return amountPayed;
    }

    public void setAmountPayed(BigDecimal amountPayed) {
        this.amountPayed = amountPayed;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public void addAmountDue(BigDecimal amount) {
        this.amountDue = this.amountDue.add(amount);
    }
}
