package inc.visor.voom.app.shared.dto.vehicle;

public class CreateVehicleTypeDto {
    private String type;
    private Double price;

    public CreateVehicleTypeDto() {
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }
}
