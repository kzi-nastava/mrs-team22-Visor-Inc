package inc.visor.voom_service.auth.model;

import jakarta.persistence.*;

import java.util.Objects;

@Entity
@Table(name = "user_status")
public class UserStatus {

    @Id
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.SEQUENCE)
    @Column(name = "user_status_id", nullable = false)
    private int id;

    @Column(name = "status_name", unique = true, nullable = false)
    private String statusName;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getStatusName() {
        return statusName;
    }

    public void setStatusName(String statusName) {
        this.statusName = statusName;
    }

    @Override
    public String toString() {
        return "UserStatus{" +
                "id=" + id +
                ", statusName='" + statusName + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        UserStatus that = (UserStatus) o;
        return id == that.id && Objects.equals(statusName, that.statusName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, statusName);
    }

}