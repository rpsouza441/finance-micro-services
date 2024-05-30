package br.dev.rodrigopinheiro.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table(name = "transferencia")
public class Transferencia {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID uuid ;

    @Column(name="descricao", nullable = true, length = 256)
    private String descricao="";

    @Column(name = "observacao", nullable = true, length = 256)
    private String observacao= "";

    @Column(name="valor", nullable = false)
    private BigDecimal valor= BigDecimal.ZERO;

    @Column(name="recorrente",nullable = false)
    private boolean recorrente = false;

    @Column(name="efetivada",nullable = false)
    private boolean efetivada= false;

    @Temporal(TemporalType.DATE)
    @Column(name="data_vencimento", nullable = false)
    private Date dataVencimento = null;

    @Temporal(TemporalType.DATE)
    @Column(name="data_lancamento", nullable = false)
    private Date dataLancamento = null;

    @Temporal(TemporalType.DATE)
    @Column(name="data_efetivado", nullable = true)
    private Date dataEfetivado = null;

    @ManyToOne
    @JoinColumn(name = "categoria_id")
    private Categoria categoria;

    @ManyToOne
    @JoinColumn(name = "conta_saida")
    private Conta contaSaida;

    @ManyToOne
    @JoinColumn(name = "conta_entrada")
    private Conta contaEntrada;
}
