package inc.visor.voom_service.auth.user.model;

import jakarta.persistence.*;

import java.util.Objects;

@Entity
@Table(name = "user_type")
public class UserType {

    @Id
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.SEQUENCE)
    @Column(name = "user_type_id", nullable = false)
    private int id;

    @Column(name = "type_name", unique = true, nullable = false)
    private String typeName;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    @Override
    public String toString() {
        return "UserType{" +
                "id=" + id +
                ", typeName='" + typeName + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        UserType userType = (UserType) o;
        return id == userType.id && Objects.equals(typeName, userType.typeName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, typeName);
    }

}
