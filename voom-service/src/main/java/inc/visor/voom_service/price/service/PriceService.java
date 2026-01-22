package inc.visor.voom_service.price.service;

import inc.visor.voom_service.price.model.Price;
import inc.visor.voom_service.price.repository.PriceRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PriceService {

    private final PriceRepository priceRepository;

    public PriceService(PriceRepository priceRepository) {
        this.priceRepository = priceRepository;
    }

    public List<Price> getPrices() {
        return priceRepository.findAll();
    }

    public Optional<Price> getPrice(Long id) {
        return priceRepository.findById(id);
    }

    public Price create(Price price) {
        return priceRepository.save(price);
    }

    public Price update(Price price) {
        return priceRepository.save(price);
    }

    public void delete(long priceId) {
        priceRepository.deleteById(priceId);
    }

}
