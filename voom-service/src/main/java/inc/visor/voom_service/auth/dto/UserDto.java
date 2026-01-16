package inc.visor.voom_service.auth.dto;

import inc.visor.voom_service.auth.user.model.User;

public class UserDto {
    private long id;
    private String firstName;
    private String lastName;
    private String email;

    private UserDto() {
    }

    public UserDto(User user) {
        this.id = user.getId();
        this.firstName = user.getPerson().getFirstName();
        this.lastName = user.getPerson().getLastName();
        this.email = user.getEmail();
    }


    public long getId() {
        return id;
    }

    public void setId(long id) {
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
