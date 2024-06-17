package br.dev.rodrigopinheiro.finances.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import br.dev.rodrigopinheiro.finances.entity.enums.TransactionType;

@Entity
@Table(name = "tb_transaction")
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "description", length = 256)
    private String description;

    @Column(name = "note", length = 256)
    private String note;

    @Column(name = "amount", nullable = false)
    private BigDecimal amount = BigDecimal.ZERO;

    @Column(name = "interest")
    private BigDecimal interest = BigDecimal.ZERO;

    @Column(name = "discount")
    private BigDecimal discount = BigDecimal.ZERO;

    @Column(name = "transaction_type", nullable = false)
    private TransactionType transactionType;

    @Column(name = "recurrent")
    private boolean isRecurrent = false;

    @Column(name = "effective")
    private boolean isEffective = false;

    @Column(name = "due_date", nullable = false)
    private LocalDateTime dueDate;

    @Column(name = "creation_date", nullable = false)
    private LocalDateTime creationDate;

    @Column(name = "effectived_date")
    private LocalDateTime effectivedDate;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    @ManyToOne
    @JoinColumn(name = "credit_card_statement_id")
    private CreditCardStatement creditCardStatement;

    @ManyToOne
    @JoinColumn(name = "bank_accoint_id")
    private BankAccount bankAccount;

    public Transaction() {
    }

    public Transaction(String description, BigDecimal amount,
                       TransactionType transactionType, boolean isRecurrent,
                       boolean isEffective, LocalDateTime creationDate, LocalDateTime dueDate,
                       Category category, BankAccount bankAccount) {
        this.description = description;
        this.amount = amount;
        this.transactionType = transactionType;
        this.isRecurrent = isRecurrent;
        this.isEffective = isEffective;
        this.dueDate = dueDate;
        this.creationDate = creationDate;
        this.category = category;
        this.bankAccount = bankAccount;
    }

    public Transaction(String description, String note, BigDecimal amount, BigDecimal interest,
                       BigDecimal discount, TransactionType transactionType, boolean isRecurrent,
                       boolean isEffective, LocalDateTime creationDate, LocalDateTime dueDate,
                       Category category, BankAccount bankAccount) {
        this.description = description;
        this.note = note;
        this.amount = amount;
        this.interest = interest;
        this.discount = discount;
        this.transactionType = transactionType;
        this.isRecurrent = isRecurrent;
        this.isEffective = isEffective;
        this.dueDate = dueDate;
        this.creationDate = creationDate;
        this.category = category;
        this.bankAccount = bankAccount;
    }


    public Transaction(String description, String note, BigDecimal amount, BigDecimal interest,
                       BigDecimal discount, TransactionType transactionType, boolean isRecurrent,
                       boolean isEffective, LocalDateTime creationDate, LocalDateTime dueDate,
                       LocalDateTime effectivedDate, Category category, BankAccount bankAccount) {
        this.description = description;
        this.note = note;
        this.amount = amount;
        this.interest = interest;
        this.discount = discount;
        this.transactionType = transactionType;
        this.isRecurrent = isRecurrent;
        this.isEffective = isEffective;
        this.dueDate = dueDate;
        this.creationDate = creationDate;
        this.effectivedDate = effectivedDate;
        this.category = category;
        this.bankAccount = bankAccount;
    }

    public Transaction(String description, String note, BigDecimal amount, BigDecimal interest, BigDecimal discount,
                       TransactionType transactionType, boolean isRecurrent, boolean isEffective, LocalDateTime dueDate,
                       LocalDateTime creationDate, LocalDateTime effectivedDate, Category category,
                       CreditCardStatement creditCardStatement, BankAccount bankAccount) {
        this.description = description;
        this.note = note;
        this.amount = amount;
        this.interest = interest;
        this.discount = discount;
        this.transactionType = transactionType;
        this.isRecurrent = isRecurrent;
        this.isEffective = isEffective;
        this.dueDate = dueDate;
        this.creationDate = creationDate;
        this.effectivedDate = effectivedDate;
        this.category = category;
        this.creditCardStatement = creditCardStatement;
        this.bankAccount = bankAccount;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public BigDecimal getInterest() {
        return interest;
    }

    public void setInterest(BigDecimal interest) {
        this.interest = interest;
    }

    public BigDecimal getDiscount() {
        return discount;
    }

    public void setDiscount(BigDecimal discount) {
        this.discount = discount;
    }

    public TransactionType getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(TransactionType transactionType) {
        this.transactionType = transactionType;
    }

    public boolean isRecurrent() {
        return isRecurrent;
    }

    public void setRecurrent(boolean isRecurrent) {
        this.isRecurrent = isRecurrent;
    }

    public boolean isEffective() {
        return isEffective;
    }

    public void setEffective(boolean isEffective) {
        this.isEffective = isEffective;
    }

    public LocalDateTime getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDateTime dueDate) {
        this.dueDate = dueDate;
    }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDateTime creationDate) {
        this.creationDate = creationDate;
    }

    public LocalDateTime getEffectivedDate() {
        return effectivedDate;
    }

    public void setEffectivedDate(LocalDateTime effectivedDate) {
        this.effectivedDate = effectivedDate;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public CreditCardStatement getCreditCardStatement() {
        return creditCardStatement;
    }

    public void setCreditCardStatement(CreditCardStatement creditCardStatement) {
        this.creditCardStatement = creditCardStatement;
    }

    public BankAccount getBankAccount() {
        return bankAccount;
    }

    public void setBankAccount(BankAccount bankAccount) {
        this.bankAccount = bankAccount;
    }

}
