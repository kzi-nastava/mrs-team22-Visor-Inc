package inc.visor.voom.app.driver.dto;

// inc.visor.voom.app.shared.dto.DriverSummaryDto
public class DriverSummaryDto {
    public int id;
    public String firstName;
    public String lastName;
    public String status;

    public int getId() {
        return id;
    }

    public void setId(int id) {
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
