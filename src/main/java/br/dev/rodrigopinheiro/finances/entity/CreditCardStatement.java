package br.dev.rodrigopinheiro.finances.entity;

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
import java.util.List;

@Entity
@Table(name = "tb_credit_card_statement")
public class CreditCardStatement {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "month")
    private String month;

    @Column(name = "year")
    private String year;

    @OneToMany(mappedBy = "statement", fetch = FetchType.LAZY)
    private List<CreditCardTransaction> transactions;

    @ManyToOne
    @JoinColumn(name = "credit_card_id")
    private CreditCard creditCard;

    public CreditCardStatement() {
    }

    public CreditCardStatement(String month, String year, List<CreditCardTransaction> transactions,
            CreditCard creditCard) {
        this.month = month;
        this.year = year;
        this.transactions = transactions;
        this.creditCard = creditCard;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public List<CreditCardTransaction> getTransactions() {
        return transactions;
    }

    public void setTransactions(List<CreditCardTransaction> transactions) {
        this.transactions = transactions;
    }

    public CreditCard getCreditCard() {
        return creditCard;
    }

    public void setCreditCard(CreditCard creditCard) {
        this.creditCard = creditCard;
    }

}
