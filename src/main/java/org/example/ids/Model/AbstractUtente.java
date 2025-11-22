package org.example.ids.Model;


import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@MappedSuperclass
public abstract class AbstractUtente {

    @NotBlank @Email
    @Column(nullable = false, unique = true)
    protected String email; // Questo sarà lo USERNAME per il login

    @NotBlank
    @Column(nullable = false)
    protected String password; // La password cifrata

    @NotBlank
    @Column(nullable = false)
    protected String ruolo; // "ROLE_VENDITORE", "ROLE_ACQUIRENTE", etc.

    // Quello che avevamo già
    protected boolean approvato = false;

    // Getter e Setter
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getRuolo() { return ruolo; }
    public void setRuolo(String ruolo) { this.ruolo = ruolo; }

    public boolean isApprovato() { return approvato; }
    public void setApprovato(boolean approvato) { this.approvato = approvato; }
}