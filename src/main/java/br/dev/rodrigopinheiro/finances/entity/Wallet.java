package br.dev.rodrigopinheiro.finances.entity;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "tb_wallet")
public class Wallet {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "wallet_balance", nullable = false)
  private BigDecimal balance;

  @OneToOne
  @JoinColumn(name = "user_id", referencedColumnName = "id")
  private User user;


  public void debit(BigDecimal value) {
    this.balance = this.balance.subtract(value);
}

public void credit(BigDecimal value) {
    this.balance = this.balance.add(value);
}
  public Wallet() {
  }

  public Wallet(BigDecimal balance, User user) {
    this.balance = balance;
    this.user = user;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public BigDecimal getBalance() {
    return balance;
  }

  public void setBalance(BigDecimal balance) {
    this.balance = balance;
  }

  public User getUser() {
    return user;
  }

  public void setUser(User user) {
    this.user = user;
  }

}
