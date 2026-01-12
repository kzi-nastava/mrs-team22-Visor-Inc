package inc.visor.voom_service.driver.dto;

import inc.visor.voom_service.driver.model.enums.DriverActivityStatus;

public class DriverSummaryDto {

    private Long id;
    private String firstName;
    private String lastName;
    private String pfpUrl;
    private DriverActivityStatus status;
    private String email;

    public DriverSummaryDto() {
    }

    public DriverSummaryDto(Long id, String firstName, String lastName) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public DriverSummaryDto(Long id, String firstName, String lastName, String email) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
    }

    public DriverSummaryDto(Long id, String firstName, String lastName, String pfpUrl, DriverActivityStatus status) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.pfpUrl = pfpUrl;
        this.status = status;
    }

    public String getPfpUrl() {
        return pfpUrl;
    }

    public DriverActivityStatus getStatus() {
        return status;
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

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setPfpUrl(String pfpUrl) {
        this.pfpUrl = pfpUrl;
    }

    public void setStatus(DriverActivityStatus status) {
        this.status = status;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public static DriverSummaryDto from(inc.visor.voom_service.driver.model.Driver driver) {
        return new DriverSummaryDto(
                driver.getId(),
                driver.getPerson().getFirstName(),
                driver.getPerson().getLastName(),
                driver.getUser().getEmail()
        );
    }
}
