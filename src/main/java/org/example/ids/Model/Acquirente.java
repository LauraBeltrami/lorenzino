package org.example.ids.Model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Entity
@Table(name = "acquirenti", uniqueConstraints = @UniqueConstraint(columnNames = "email"))
public class Acquirente {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank @Column(nullable = false) private String nome;
    @NotBlank
    @Column(nullable = false) private String cognome;
    @NotBlank @Email
    @Column(nullable = false, unique = true) private String email;

    public Acquirente() {}
    public Acquirente(Long id, String nome, String cognome, String email) {
        this.id = id; this.nome = nome; this.cognome = cognome; this.email = email;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getCognome() { return cognome; }
    public void setCognome(String cognome) { this.cognome = cognome; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
}

