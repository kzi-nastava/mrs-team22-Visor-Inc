package inc.visor.voom_service.person.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public class UpdateUserProfileRequestDto {
    
    @NotBlank(message="First name is required.")
    private String firstName;

    @NotBlank(message="Last name is required.")
    private String lastName;

    @NotBlank(message = "Phone number is required")
    @Pattern(
        regexp = "^\\+?[0-9]{9,15}$",
        message = "Phone number format is not valid"
    )
    private String phoneNumber;

    @NotBlank(message="Address is required.")
    private String address;

    public UpdateUserProfileRequestDto() {};

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
}
