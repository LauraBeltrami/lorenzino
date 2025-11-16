package org.example.ids.Service;


import org.example.ids.DTO.BundleDTO;
import org.example.ids.DTO.BundleMapper;
import org.example.ids.Model.*;
import org.example.ids.Repository.BundleItemRepository;
import org.example.ids.Repository.BundleRepository;
import org.example.ids.Repository.DistributoreRepository;
import org.example.ids.Repository.ProdottoRepository;
import org.example.ids.Exceptions.BusinessException;
import org.example.ids.Exceptions.NotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@Transactional
public class DistributoreService {

    private final DistributoreRepository distributoreRepo;
    private final ProdottoRepository prodottoRepo;
    private final BundleRepository bundleRepo;
    private final BundleItemRepository bundleItemRepo;
    private final ProdottoService prodottoService;

    public DistributoreService(DistributoreRepository distributoreRepo,
                               ProdottoRepository prodottoRepo,
                               BundleRepository bundleRepo,
                               BundleItemRepository bundleItemRepo,
                               ProdottoService prodottoService) {
        this.distributoreRepo = distributoreRepo;
        this.prodottoRepo = prodottoRepo;
        this.bundleRepo = bundleRepo;
        this.bundleItemRepo = bundleItemRepo;
        this.prodottoService = prodottoService;
    }

    // ----- prodotti singoli (già org.example.ids.DTO nel ProdottoService) -----
    // public ProdottoDTO creaProdottoSingolo(...)

    // ----- bundle -----
    public BundleDTO creaBundle(Long distributoreId, String nome, BigDecimal prezzo) {
        Distributore dist = distributoreRepo.findById(distributoreId)
                .orElseThrow(() -> new NotFoundException("Distributore non trovato: " + distributoreId));

        bundleRepo.findByDistributoreId(distributoreId).stream()
                .filter(b -> b.getNome().equalsIgnoreCase(nome))
                .findAny()
                .ifPresent(b -> {
                    throw new BusinessException("Bundle già esistente con questo nome.");
                });

        Bundle b = new Bundle(null, nome, prezzo, dist);
        b = bundleRepo.save(b);
        // ricarico con fetch-join (se usi i metodi graph) per sicurezza
        b = bundleRepo.findGraphById(b.getId()).orElse(b);
        return BundleMapper.toDTO(b);
    }

    public BundleDTO aggiungiProdottoABundle(Long bundleId, Long prodottoId, int quantita) {
        if (quantita <= 0) throw new BusinessException("Quantità deve essere > 0.");

        Bundle bundle = bundleRepo.findGraphById(bundleId)
                .orElseThrow(() -> new NotFoundException("Bundle non trovato: " + bundleId));

        Prodotto p = prodottoRepo.findById(prodottoId)
                .orElseThrow(() -> new NotFoundException("Prodotto non trovato: " + prodottoId));

        if (p.getStato() != StatoProdotto.APPROVATO)
            throw new BusinessException("Solo prodotti APPROVATI nel bundle.");

        BundleItem existing = bundleItemRepo.findByBundleIdAndProdottoId(bundle.getId(), p.getId()).orElse(null);
        if (existing == null) {
            bundle.getItems().add(new BundleItem(null, bundle, p, quantita));
        } else {
            existing.setQuantita(existing.getQuantita() + quantita);
        }
        // il dirty checking salva; poi rimappiamo
        return BundleMapper.toDTO(bundle);
    }

    public BundleDTO aggiornaQuantitaInBundle(Long bundleId, Long prodottoId, int quantita) {
        Bundle bundle = bundleRepo.findGraphById(bundleId)
                .orElseThrow(() -> new NotFoundException("Bundle non trovato."));
        BundleItem item = bundleItemRepo.findByBundleIdAndProdottoId(bundleId, prodottoId)
                .orElseThrow(() -> new NotFoundException("Prodotto non presente nel bundle."));
        if (quantita <= 0) {
            bundle.getItems().remove(item);
            bundleItemRepo.delete(item);
        } else {
            item.setQuantita(quantita);
        }
        return BundleMapper.toDTO(bundle);
    }

    public BundleDTO rimuoviProdottoDaBundle(Long bundleId, Long prodottoId) {
        return aggiornaQuantitaInBundle(bundleId, prodottoId, 0);
    }

    public BundleDTO aggiornaPrezzoBundle(Long bundleId, BigDecimal nuovoPrezzo) {
        Bundle bundle = bundleRepo.findGraphById(bundleId)
                .orElseThrow(() -> new NotFoundException("Bundle non trovato."));
        bundle.setPrezzo(nuovoPrezzo);
        return BundleMapper.toDTO(bundle);
    }

    public BundleDTO getBundle(Long bundleId) {
        Bundle b = bundleRepo.findGraphById(bundleId)
                .orElseThrow(() -> new NotFoundException("Bundle non trovato: " + bundleId));
        return BundleMapper.toDTO(b);
    }

    public List<BundleDTO> listaBundles(Long distributoreId) {
        List<Bundle> list = bundleRepo.findGraphByDistributoreId(distributoreId);
        return BundleMapper.toDTO(list);
    }
}
