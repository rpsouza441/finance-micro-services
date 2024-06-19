package br.dev.rodrigopinheiro.finances.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import br.dev.rodrigopinheiro.finances.entity.enums.CategoryType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "tb_credit_card_transaction")
public class CreditCardTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "date")
    private LocalDateTime date;

    @Column(name = "cc_transcation_amount")
    private BigDecimal amount;

    @Column(name = "cc_transacation_is_refunded")
    private boolean isRefunded;

    @Column(name = "cc_transaction_installment_id")
    private String installmentId;

    @ManyToOne
    @JoinColumn(name = "statement_id")
    private CreditCardStatement statement;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;


    public CreditCardTransaction() {
    }

    public CreditCardTransaction(LocalDateTime date,
                                 BigDecimal amount,
                                 boolean isRefunded,
                                 String installmentId,
                                 CreditCardStatement statement,
                                 Category category) {
        this.date = date;
        this.amount = amount;
        this.isRefunded = isRefunded;
        this.installmentId = installmentId;
        this.statement = statement;
        this.category = category;
    }

    public CreditCardTransaction(LocalDateTime date,
                                 BigDecimal amount,
                                 String installmentId,
                                 CreditCardStatement statement,
                                 Category category) {
        this.date = date;
        this.amount = amount;
        this.category = category;
        this.statement = statement;
        this.isRefunded = false;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public boolean isRefunded() {
        return isRefunded;
    }

    public void setRefunded(boolean isRefunded) {
        this.isRefunded = isRefunded;
    }

    public String getInstallmentId() {
        return installmentId;
    }

    public void setInstallmentId(String installmentId) {
        this.installmentId = installmentId;
    }

    public CreditCardStatement getStatement() {
        return statement;
    }

    public void setStatement(CreditCardStatement statement) {
        this.statement = statement;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }
}
