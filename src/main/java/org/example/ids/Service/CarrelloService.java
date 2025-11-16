package org.example.ids.Service;



import org.example.ids.DTO.CarrelloDTO;
import org.example.ids.DTO.CarrelloMapper;
import org.example.ids.Exceptions.BusinessException;
import org.example.ids.Exceptions.NotFoundException;
import org.example.ids.Model.*;
import org.example.ids.Repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class CarrelloService {

    private final CarrelloRepository carrelloRepo;
    private final CarrelloItemRepository carrelloItemRepo;
    private final CarrelloBundleItemRepository carrelloBundleItemRepo;
    private final AcquirenteRepository acquirenteRepo;
    private final ProdottoRepository prodottoRepo;
    private final BundleRepository bundleRepo;

    public CarrelloService(CarrelloRepository carrelloRepo,
                           CarrelloItemRepository carrelloItemRepo,
                           CarrelloBundleItemRepository carrelloBundleItemRepo,
                           AcquirenteRepository acquirenteRepo,
                           ProdottoRepository prodottoRepo,
                           BundleRepository bundleRepo) {
        this.carrelloRepo = carrelloRepo;
        this.carrelloItemRepo = carrelloItemRepo;
        this.carrelloBundleItemRepo = carrelloBundleItemRepo;
        this.acquirenteRepo = acquirenteRepo;
        this.prodottoRepo = prodottoRepo;
        this.bundleRepo = bundleRepo;
    }

    private Carrello loadOrCreateGraph(Long acquirenteId) {
        return carrelloRepo.findGraphByAcquirenteId(acquirenteId).orElseGet(() -> {
            Acquirente a = acquirenteRepo.findById(acquirenteId)
                    .orElseThrow(() -> new NotFoundException("Acquirente non trovato: " + acquirenteId));
            Carrello c = new Carrello();
            c.setAcquirente(a);
            return carrelloRepo.save(c);
        });
    }

    public CarrelloDTO acquista(Long acquirenteId) {
        Carrello c = loadOrCreateGraph(acquirenteId);

        if (c.getItems().isEmpty() && c.getBundleItems().isEmpty()) {
            throw new BusinessException("Carrello vuoto, impossibile acquistare.");
        }

        // --- FASE 1: CONTROLLO DISPONIBILITÀ ---

        // 1a. Controlla scorte Prodotti Singoli (usa Prodotto.quantitaDisponibile)
        for (CarrelloItem item : c.getItems()) {
            Prodotto p = prodottoRepo.findById(item.getProdotto().getId())
                    .orElseThrow(() -> new NotFoundException("Prodotto " + item.getProdotto().getNome() + " non più esistente."));

            int richiesti = item.getQuantita();
            if (p.getQuantitaDisponibile() < richiesti) {
                throw new BusinessException("Quantità non disponibile per " + p.getNome() +
                        ". Richiesti: " + richiesti +
                        ", Disponibili: " + p.getQuantitaDisponibile());
            }
        }

        // 1b. Controlla scorte Bundle (usa Bundle.quantitaDisponibile)
        for (CarrelloBundleItem bundleItem : c.getBundleItems()) {
            Bundle b = bundleRepo.findById(bundleItem.getBundle().getId())
                    .orElseThrow(() -> new NotFoundException("Bundle " + bundleItem.getBundle().getNome() + " non più esistente."));

            int richiesti = bundleItem.getQuantita();
            if (b.getQuantitaDisponibile() < richiesti) {
                throw new BusinessException("Quantità non disponibile per il bundle " + b.getNome() +
                        ". Richiesti: " + richiesti +
                        ", Disponibili: " + b.getQuantitaDisponibile());
            }
        }

        // --- FASE 2: RIDUZIONE QUANTITÀ (COMMIT) ---
        // Se siamo qui, tutto è disponibile. @Transactional salva le modifiche.

        // 2a. Riduci scorte Prodotti Singoli
        for (CarrelloItem item : c.getItems()) {
            Prodotto p = prodottoRepo.findById(item.getProdotto().getId()).get(); // Sicuro di trovarlo
            p.setQuantitaDisponibile(p.getQuantitaDisponibile() - item.getQuantita());
        }

        // 2b. Riduci scorte Bundles
        for (CarrelloBundleItem bundleItem : c.getBundleItems()) {
            Bundle b = bundleRepo.findById(bundleItem.getBundle().getId()).get(); // Sicuro di trovarlo
            b.setQuantitaDisponibile(b.getQuantitaDisponibile() - bundleItem.getQuantita());
        }

        // --- FASE 3: SVUOTA IL CARRELLO ---
        c.getItems().clear();
        c.getBundleItems().clear();

        return CarrelloMapper.toDTO(c);
    }

    public CarrelloDTO getDettaglio(Long acquirenteId) {
        return CarrelloMapper.toDTO(loadOrCreateGraph(acquirenteId));
    }

    public CarrelloDTO clear(Long acquirenteId) {
        Carrello c = loadOrCreateGraph(acquirenteId);
        c.getItems().clear();
        c.getBundleItems().clear();
        return CarrelloMapper.toDTO(c);
    }

    // -------- prodotti --------
    public CarrelloDTO addItem(Long acquirenteId, Long prodottoId, int quantita) {
        if (quantita <= 0) throw new BusinessException("Quantità deve essere > 0.");
        Carrello c = loadOrCreateGraph(acquirenteId);

        Prodotto p = prodottoRepo.findById(prodottoId)
                .orElseThrow(() -> new NotFoundException("Prodotto non trovato: " + prodottoId));
        if (p.getStato() != StatoProdotto.APPROVATO)
            throw new BusinessException("Prodotto non approvato.");

        CarrelloItem item = carrelloItemRepo.findByCarrelloIdAndProdottoId(c.getId(), p.getId()).orElse(null);
        if (item == null) {
            item = new CarrelloItem(c, p, quantita);
            c.getItems().add(item);
        } else {
            item.setQuantita(item.getQuantita() + quantita);
        }
        return CarrelloMapper.toDTO(c);
    }

    public CarrelloDTO updateQuantita(Long acquirenteId, Long prodottoId, int quantita) {
        Carrello c = loadOrCreateGraph(acquirenteId);
        CarrelloItem item = carrelloItemRepo.findByCarrelloIdAndProdottoId(c.getId(), prodottoId)
                .orElseThrow(() -> new NotFoundException("Articolo non presente nel carrello."));
        if (quantita <= 0) {
            c.getItems().remove(item);
            carrelloItemRepo.delete(item);
        } else {
            item.setQuantita(quantita);
        }
        return CarrelloMapper.toDTO(c);
    }

    public CarrelloDTO removeItem(Long acquirenteId, Long prodottoId) {
        return updateQuantita(acquirenteId, prodottoId, 0);
    }

    // -------- bundle --------
    public CarrelloDTO addBundle(Long acquirenteId, Long bundleId, int quantita) {
        if (quantita <= 0) throw new BusinessException("Quantità deve essere > 0.");
        Carrello c = loadOrCreateGraph(acquirenteId);

        Bundle b = bundleRepo.findById(bundleId)
                .orElseThrow(() -> new NotFoundException("Bundle non trovato: " + bundleId));

        boolean nonApprovato = b.getItems().stream()
                .map(BundleItem::getProdotto)
                .anyMatch(p -> p.getStato() != StatoProdotto.APPROVATO);
        if (nonApprovato) throw new BusinessException("Il bundle contiene prodotti non approvati.");

        CarrelloBundleItem r = carrelloBundleItemRepo.findByCarrelloIdAndBundleId(c.getId(), b.getId()).orElse(null);
        if (r == null) {
            r = new CarrelloBundleItem(c, b, quantita);
            c.getBundleItems().add(r);
        } else {
            r.setQuantita(r.getQuantita() + quantita);
        }
        return CarrelloMapper.toDTO(c);
    }

    public CarrelloDTO updateQuantitaBundle(Long acquirenteId, Long bundleId, int quantita) {
        Carrello c = loadOrCreateGraph(acquirenteId);
        CarrelloBundleItem r = carrelloBundleItemRepo.findByCarrelloIdAndBundleId(c.getId(), bundleId)
                .orElseThrow(() -> new NotFoundException("Bundle non presente nel carrello."));
        if (quantita <= 0) {
            c.getBundleItems().remove(r);
            carrelloBundleItemRepo.delete(r);
        } else {
            r.setQuantita(quantita);
        }
        return CarrelloMapper.toDTO(c);
    }

    public CarrelloDTO removeBundle(Long acquirenteId, Long bundleId) {
        return updateQuantitaBundle(acquirenteId, bundleId, 0);
    }
}
