package Service;


import DTO.CarrelloDTO;
import DTO.CarrelloMapper;
import Model.*;
import example.ids2425.Repository.*;
import exceptions.BusinessException;
import exceptions.NotFoundException;
import Service;
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
