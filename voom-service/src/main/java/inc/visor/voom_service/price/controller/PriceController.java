package inc.visor.voom_service.price.controller;

import inc.visor.voom_service.exception.NotFoundException;
import inc.visor.voom_service.price.dto.CreatePriceDto;
import inc.visor.voom_service.price.dto.PriceDto;
import inc.visor.voom_service.price.model.Price;
import inc.visor.voom_service.price.service.PriceService;
import inc.visor.voom_service.vehicle.model.VehicleType;
import inc.visor.voom_service.vehicle.service.VehicleTypeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/prices")
public class PriceController {

    private final VehicleTypeService vehicleTypeService;
    private final PriceService priceService;

    public PriceController(VehicleTypeService vehicleTypeService, PriceService priceService) {
        this.vehicleTypeService = vehicleTypeService;
        this.priceService = priceService;
    }

    @GetMapping
    public ResponseEntity<List<PriceDto>> getPrices() {
        return ResponseEntity.ok(this.priceService.getPrices().stream().map(PriceDto::new).toList());
    }

    @GetMapping("{id}")
    public ResponseEntity<PriceDto> getPrice(@PathVariable Long id) {
        Price price = priceService.getPrice(id).orElseThrow(NotFoundException::new);
        return ResponseEntity.ok(new PriceDto(price));
    }

    @PostMapping
    public ResponseEntity<PriceDto> createPrice(@RequestBody CreatePriceDto dto) {
        VehicleType vehicleType = this.vehicleTypeService.getVehicleType(dto.getVehicleTypeId()).orElseThrow(NotFoundException::new);
        Price price = new Price(dto.getPricePerKm(), vehicleType);
        return ResponseEntity.ok(new PriceDto(price));
    }

    @PutMapping("{id}")
    public ResponseEntity<PriceDto> updatePrice(@PathVariable Long id, @RequestBody PriceDto dto) {
        Price price = this.priceService.getPrice(id).orElseThrow(NotFoundException::new);
        price.setPricePerKm(dto.getPricePerKm());
        price = this.priceService.update(price);
        return ResponseEntity.ok(new PriceDto(price));
    }

}
