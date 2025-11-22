package org.example.ids.Model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "curatori")
public class Curatore extends AbstractUtente{

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(nullable = false)
    private String nome;

    @OneToMany(mappedBy = "curatoreValidatore", fetch = FetchType.LAZY)
    private Set<Certificazione> certificazioni = new HashSet<>();

    public Curatore() {}
    public Curatore(Long id, String nome, String email, String password) {
        super();
        this.id = id;
        this.email = email;
        this.nome = nome;
        this.password = password;
        this.ruolo = "ROLE_CURATORE";
        this.approvato = false; // Serve approvazione
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public Set<Certificazione> getCertificazioni() { return certificazioni; }
    public void setCertificazioni(Set<Certificazione> certificazioni) { this.certificazioni = certificazioni; }
}

