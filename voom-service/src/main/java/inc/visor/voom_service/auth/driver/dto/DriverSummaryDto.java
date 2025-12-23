package inc.visor.voom_service.auth.driver.dto;

public class DriverSummaryDto {

    private Long id;
    private String firstName;
    private String lastName;

    public DriverSummaryDto() {
    }

    public DriverSummaryDto(Long id, String firstName, String lastName) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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
    
}
