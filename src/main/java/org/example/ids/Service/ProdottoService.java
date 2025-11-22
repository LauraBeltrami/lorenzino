package org.example.ids.Service;


import org.example.ids.DTO.ProdottoDTO;
import org.example.ids.DTO.ProdottoMapper;
import org.example.ids.Model.*;
import org.example.ids.Repository.CertificazioneRepository;
import org.example.ids.Repository.CuratoreRepository;
import org.example.ids.Repository.ProdottoRepository;
import org.example.ids.Repository.VenditoreRepository;
import org.example.ids.Exceptions.BusinessException;
import org.example.ids.Exceptions.NotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class ProdottoService {

    private final ProdottoRepository prodottoRepo;
    private final VenditoreRepository venditoreRepo;
    private final CuratoreRepository curatoreRepo;
    private final CertificazioneRepository certificazioneRepo;

    public ProdottoService(ProdottoRepository prodottoRepo,
                           VenditoreRepository venditoreRepo,
                           CuratoreRepository curatoreRepo,
                           CertificazioneRepository certificazioneRepo) {
        this.prodottoRepo = prodottoRepo;
        this.venditoreRepo = venditoreRepo;
        this.curatoreRepo = curatoreRepo;
        this.certificazioneRepo = certificazioneRepo;
    }

    public ProdottoDTO creaProdotto(Long venditoreId, String nome, BigDecimal prezzo,int quantita) {
        Venditore v = venditoreRepo.findById(venditoreId)
                .orElseThrow(() -> new NotFoundException("Venditore non trovato: " + venditoreId));
        Prodotto p = new Prodotto();
        p.setNome(nome);
        p.setPrezzo(prezzo);
        p.setVenditore(v);
        p.setStato(StatoProdotto.IN_VALIDAZIONE);
        p.setQuantitaDisponibile(quantita);
        p = prodottoRepo.save(p);

        return ProdottoMapper.toDTO(p);
    }

    public ProdottoDTO getById(Long prodottoId) {
        Prodotto p = prodottoRepo.findById(prodottoId)
                .orElseThrow(() -> new NotFoundException("Prodotto non trovato: " + prodottoId));
        return ProdottoMapper.toDTO(p);
    }

    public List<ProdottoDTO> listaVendibili() {
        return ProdottoMapper.toDTO(prodottoRepo.findByStato(StatoProdotto.APPROVATO));
    }

    public ProdottoDTO approvaProdotto(Long prodottoId, Long curatoreId, String descrizioneCertificazione) {
        Prodotto p = prodottoRepo.findById(prodottoId)
                .orElseThrow(() -> new NotFoundException("Prodotto non trovato: " + prodottoId));
        if (p.getStato() == StatoProdotto.APPROVATO) throw new BusinessException("Già approvato.");
        if (p.getStato() == StatoProdotto.RIFIUTATO) throw new BusinessException("Rifiutato: non approvabile.");
        if (certificazioneRepo.existsByProdottoId(prodottoId))
            throw new BusinessException("Certificazione già presente.");

        Curatore c = curatoreRepo.findById(curatoreId)
                .orElseThrow(() -> new NotFoundException("Curatore non trovato: " + curatoreId));

        Certificazione cert = new Certificazione();
        cert.setProdotto(p);
        cert.setCuratoreValidatore(c);
        cert.setDescrizione(descrizioneCertificazione);
        cert.setDataApprovazione(LocalDateTime.now());
        certificazioneRepo.save(cert);

        p.setStato(StatoProdotto.APPROVATO);
        p.setCertificazione(cert);

        return ProdottoMapper.toDTO(p);
    }

    public ProdottoDTO rifiutaProdotto(Long prodottoId, String motivo) {
        Prodotto p = prodottoRepo.findById(prodottoId)
                .orElseThrow(() -> new NotFoundException("Prodotto non trovato: " + prodottoId));
        if (p.getStato() == StatoProdotto.APPROVATO) throw new BusinessException("Già approvato.");
        if (certificazioneRepo.existsByProdottoId(prodottoId))
            throw new BusinessException("Certificazione già presente.");
        p.setStato(StatoProdotto.RIFIUTATO);
        return ProdottoMapper.toDTO(p);
    }

    public ProdottoDTO aggiornaPrezzo(Long prodottoId, BigDecimal nuovoPrezzo) {
        Prodotto p = prodottoRepo.findById(prodottoId)
                .orElseThrow(() -> new NotFoundException("Prodotto non trovato: " + prodottoId));
        p.setPrezzo(nuovoPrezzo);
        return ProdottoMapper.toDTO(p);
    }

    public void elimina(Long prodottoId) {
        prodottoRepo.delete(prodottoRepo.findById(prodottoId)
                .orElseThrow(() -> new NotFoundException("Prodotto non trovato: " + prodottoId)));
    }
}
