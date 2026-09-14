package br.com.leperber.prazoflow.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "usuario")
public class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    @Column(length = 50, nullable = false, unique = true, name = "usuario")
    private String usuario;
    @Column(length = 150, nullable = false, name = "senha")
    private String senha;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, name = "status")
    private StatusPadrao status;

    public Usuario() {}

    public Usuario(String usuario, String senha) {
        this.usuario = usuario;
        this.senha = senha;
        this.status = StatusPadrao.ATIVO;
    }

    public Long getId() {
        return id;
    }

    public String getUsuario() {
        return usuario;
    }

    public String getSenha() {
        return senha;
    }

    public StatusPadrao getStatus() {
        return status;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public void setStatus(StatusPadrao status) {
        this.status = status;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Usuario outroUsuario = (Usuario) o;
        return this.getId() != null && this.getId().equals(outroUsuario.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode() ;
    }
}
