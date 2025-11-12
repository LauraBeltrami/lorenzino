package Model;


public enum Ruolo {
    ACQUIRENTE("Acquirente"),
    VENDITORE("Venditore"),
    CURATORE("Curatore"),
    ADMIN("Admin"),
    GESTORE_PIATTAFORMA("Gestore Piattaforma");

    private final String label;

    Ruolo(String label) { this.label = label; }

    public String getLabel() { return label; }

    /** Converte stringhe tipo "venditore" o "gestore piattaforma" nell'enum corrispondente. */
    public static Ruolo parse(String value) {
        if (value == null) return null;
        String norm = value.trim().toUpperCase().replace(' ', '_');
        try { return Ruolo.valueOf(norm); }
        catch (IllegalArgumentException ex) { return null; }
    }
}

