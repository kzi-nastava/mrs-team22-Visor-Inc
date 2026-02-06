package inc.visor.voom.app.admin.pricing.dto;

public class VehicleTypeDto {

    private long id;

    private String type;

    private Double price;

    public VehicleTypeDto() {
    }

    public VehicleTypeDto(long id, String type, Double price) {
        this.id = id;
        this.type = type;
        this.price = price;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
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

