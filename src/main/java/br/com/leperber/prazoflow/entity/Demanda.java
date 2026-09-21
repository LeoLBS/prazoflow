package br.com.leperber.prazoflow.entity;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name = "demanda")
public class Demanda {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    @Column(length = 200, nullable = false, name = "titulo")
    private String titulo;
    @Column(length = 350, nullable = false, name = "descricao")
    private String descricao;
    @Column(nullable = false, name = "data_vencimento")
    private LocalDate dataVencimento;
    @ManyToOne
    @JoinColumn(name = "tecnico_id")
    private Tecnico tecnico;
    @Column(length = 1000, name = "observacao")
    private String observacao;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, name = "status")
    private StatusDemanda status;
    @Column(nullable = false, name = "alerta_prazo_enviado")
    private boolean alertaPrazoEnviado;
    @Column(nullable = false, name = "alerta_atraso_enviado")
    private boolean alertaAtrasoEnviado;

    public Demanda() {}

    public Demanda(String titulo, String descricao, LocalDate dataVencimento, String observacao) {
        this.titulo = titulo;
        this.descricao = descricao;
        this.dataVencimento = dataVencimento;
        this.observacao = observacao;
        this.status = StatusDemanda.PENDENTE;
    }

    public Long getId() {
        return id;
    }

    public String getTitulo() {
        return titulo;
    }

    public String getDescricao() {
        return descricao;
    }

    public LocalDate getDataVencimento() {
        return dataVencimento;
    }

    public Tecnico getTecnico() {
        return tecnico;
    }

    public String getObservacao() {
        return observacao;
    }

    public StatusDemanda getStatus() {
        return status;
    }

    public boolean isAlertaPrazoEnviado() {
        return alertaPrazoEnviado;
    }

    public boolean isAlertaAtrasoEnviado() {
        return alertaAtrasoEnviado;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public void setDataVencimento(LocalDate dataVencimento) {
        this.dataVencimento = dataVencimento;
    }

    public void setTecnico(Tecnico tecnico) {
        this.tecnico = tecnico;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }

    public void setStatus(StatusDemanda status) {
        this.status = status;
    }

    public void marcarAlertaPrazoEnviado() {
        this.alertaPrazoEnviado = true;
    }

    public void marcarAlertaAtrasoEnviado() {
        this.alertaAtrasoEnviado = true;
    }

    public void reiniciarAlertas() {
        this.alertaPrazoEnviado = false;
        this.alertaAtrasoEnviado = false;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Demanda outraDemanda = (Demanda) o;
        return this.getId() != null && this.getId().equals(outraDemanda.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
