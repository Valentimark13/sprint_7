package dto;

public class CourierDTO {

    public final String login;
    public final String password;
    public final String firstName;

    public CourierDTO(String login, String password, String firstName) {
        this.login = login;
        this.password = password;
        this.firstName = firstName;
    }
}
