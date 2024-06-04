package br.dev.rodrigopinheiro.finances.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.DynamicUpdate;

import java.math.BigDecimal;
import java.util.List;

@DynamicUpdate
@Entity
@Table(name = "tb_bank_account")
public class BankAccount {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "bank_name", nullable = true, length = 256)
    private String bankName;

    @Column(name = "bank_balance")
    private BigDecimal bankBalance = BigDecimal.ZERO;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "bankAccount")
    private List<Transaction> transactions;

    public BankAccount() {
    }

    public BankAccount(String bankName, BigDecimal bankBalance, User user) {
        this.bankName = bankName;
        this.bankBalance = bankBalance;
        this.user = user;
    }

    public void debit(BigDecimal value) {
        this.bankBalance = this.bankBalance.subtract(value);
    }

    public void credit(BigDecimal value) {
        this.bankBalance = this.bankBalance.add(value);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public BigDecimal getBankBalance() {
        return bankBalance;
    }

    public void setBankBalance(BigDecimal bankBalance) {
        this.bankBalance = bankBalance;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public List<Transaction> getTransactions() {
        return transactions;
    }

    public void setTransactions(List<Transaction> transactions) {
        this.transactions = transactions;
    }

}
