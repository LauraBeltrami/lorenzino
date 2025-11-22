package org.example.ids.Model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Entity
@Table(name = "acquirenti", uniqueConstraints = @UniqueConstraint(columnNames = "email"))
public class Acquirente extends AbstractUtente {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nome;
    private String cognome;

    public Acquirente() {}
    public Acquirente(Long id, String nome, String cognome, String email, String password) {
        super();
        this.id = id; this.nome = nome; this.cognome = cognome;
        this.email = email;
        this.password = password;
        this.ruolo = "ROLE_ACQUIRENTE"; // Ruolo fisso per questa classe
        this.approvato = true; // L'acquirente non serve approvazione
    }
    // .

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getCognome() { return cognome; }
    public void setCognome(String cognome) { this.cognome = cognome; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
}

