package br.com.leperber.prazoflow.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "tecnico")
public class Tecnico {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    @Column(length = 100, nullable = false, name = "nome")
    private String nome;
    @Column(length = 254, unique = true, name = "email")
    private String email;
    @Column(length = 100, unique = true, name = "codigo_id_discord")
    private String codigoIdDiscord;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, name = "status")
    private StatusPadrao status;

    public Tecnico() {}

    public Tecnico(String nome) {
        this.nome = nome;
        this.status = StatusPadrao.ATIVO;
    }

    public Long getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public String getEmail() {
        return email;
    }

    public String getCodigoIdDiscord() {
        return codigoIdDiscord;
    }

    public StatusPadrao getStatus() {
        return status;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setCodigoIdDiscord(String codigoIdDiscord) {
        this.codigoIdDiscord = codigoIdDiscord;
    }

    public void setStatus(StatusPadrao status) {
        this.status = status;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Tecnico outroTecnico = (Tecnico) o;
        return this.getId() != null && this.getId().equals(outroTecnico.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
