package br.dev.rodrigopinheiro.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table(name = "transacao")
public class Transacao {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID uuid;

    @Column(name = "descricao", nullable = true, length = 256)
    private String descricao = "";

    @Column(name = "observacao", nullable = true, length = 256)
    private String observacao = "";

    @Column(name = "valor", nullable = false)
    private BigDecimal valor = BigDecimal.ZERO;

    @Column(name = "tipo_transacao", nullable = false)
    @Enumerated(EnumType.ORDINAL)
    private TipoTransacao tipoTransacao;

    @Column(name = "recorrente", nullable = false)
    private boolean recorrente = false;

    @Column(name = "efetivada", nullable = false)
    private boolean efetivada = false;

    @Temporal(TemporalType.DATE)
    @Column(name = "data_vencimento", nullable = false)
    private Date dataVencimento = null;

    @Temporal(TemporalType.DATE)
    @Column(name = "data_lancamento", nullable = false)
    private Date dataLancamento = null;

    @Temporal(TemporalType.DATE)
    @Column(name = "data_efetivado", nullable = true)
    private Date dataEfetivado = null;

    @ManyToOne
    @JoinColumn(name = "categoria_id")
    private Categoria categoria;

    @ManyToOne
    @JoinColumn(name = "conta_id")
    private Conta conta;

    @OneToOne(mappedBy = "transacao", fetch = FetchType.LAZY,
            cascade = CascadeType.ALL)
    //@PrimaryKeyJoinColumn
    private Fatura fatura;


    @PrePersist
    protected void prePersist() {
        if (this.dataLancamento == null) dataLancamento = Calendar.getInstance().getTime();
//        if (this.atualizadoEm == null) atualizadoEm = LocalDateTime.now();
    }

//    @PreUpdate
//    protected void preUpdate() {
//        this.atualizadoEm = LocalDateTime.now();
//    }


}
