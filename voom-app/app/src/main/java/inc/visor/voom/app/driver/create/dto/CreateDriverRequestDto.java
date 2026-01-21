package inc.visor.voom.app.driver.create.dto;

public class CreateDriverRequestDto {

    private String email;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String address;
    private CreateVehicleRequestDto vehicle;

    public CreateDriverRequestDto(
            String email,
            String firstName,
            String lastName,
            String phoneNumber,
            String address,
            CreateVehicleRequestDto vehicle
    ) {
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phoneNumber = phoneNumber;
        this.address = address;
        this.vehicle = vehicle;
    }
}
