package inc.visor.voom_service.vehicle.dto;

public class CreateVehicleTypeDto {

    private String type;

    public CreateVehicleTypeDto() {}

    public CreateVehicleTypeDto(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
