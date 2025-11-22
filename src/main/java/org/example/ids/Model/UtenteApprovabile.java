package org.example.ids.Model;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;

@MappedSuperclass // Indica che i campi di questa classe finiranno nelle tabelle dei figli
public abstract class UtenteApprovabile {

    // Default false: serve l'admin
    @Column(nullable = false)
    protected boolean approvato = false;

    public boolean isApprovato() {
        return approvato;
    }

    public void setApprovato(boolean approvato) {
        this.approvato = approvato;
    }
}