package inc.visor.voom_service.driver.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class CreateDriverRequestDto {

    @NotBlank(message="Email is required")
    @Email(message="Email is not valid.")
    private String email;

    @NotBlank(message="First name is required")
    private String firstName;

    @NotBlank(message="Last name is required")
    private String lastName;

    @NotBlank(message="Phone number is required")
    private String phoneNumber;

    @NotBlank(message="Address is required")
    private String address;

    @NotNull(message="Vehicle information is required")
    @Valid
    private VehicleRequestDto vehicle;

    public CreateDriverRequestDto() {}

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public VehicleRequestDto getVehicle() {
        return vehicle;
    }

    public void setVehicle(VehicleRequestDto vehicle) {
        this.vehicle = vehicle;
    }
}
