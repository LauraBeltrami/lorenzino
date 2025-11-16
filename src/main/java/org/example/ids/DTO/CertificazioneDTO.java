package org.example.ids.DTO;


import java.time.LocalDateTime;

public record CertificazioneDTO(
        Long id,
        String descrizione,
        Long curatoreId,
        String curatoreNome,
        LocalDateTime dataApprovazione
) {}

