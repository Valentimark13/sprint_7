import io.qameta.allure.Description;
import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import org.junit.Test;
import services.Courier;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

public class CourierLoginTest {

    private final String BASE_URL = "https://qa-scooter.praktikum-services.ru/api/v1/courier/login";

    @Test
    @DisplayName("Курьер может успешно авторизоваться")
    @Description("Проверка успешной авторизации курьера с валидными данными")
    public void courierCanLoginSuccessfully() {
        String login = Courier.generateRandomString(8);
        String password = Courier.generateRandomString(8);
        String name = Courier.generateRandomString(4);
        createCourier(login, password, name);

        Response response = loginCourier(login, password);
        response.then()
                .statusCode(200);
    }

    @Step("Создание курьера с логином {0}, паролем {1} и именем {2}")
    private void createCourier(String login, String password, String name) {
        (new Courier()).createCourierRequest(login, password, name);
    }

    @Step("Авторизация курьера с логином {0} и паролем {1}")
    private Response loginCourier(String login, String password) {
        return (new Courier()).login(login, password);
    }

    @Test
    @DisplayName("Ошибка при отсутствии логина")
    @Description("Проверка ошибки авторизации при отсутствии логина")
    public void cannotLoginWithoutLogin() {
        String json = "{\"password\": \"1234\"}";

        Response response = loginWithoutLogin(json);

        response.then()
                .statusCode(400)
                .body("message", equalTo("Недостаточно данных для входа"));
    }

    @Step("Попытка авторизации без логина")
    private Response loginWithoutLogin(String json) {
        return given()
                .header("Content-type", "application/json")
                .body(json)
                .when()
                .post(BASE_URL);
    }

    @Test
    @DisplayName("Ошибка при отсутствии пароля")
    @Description("Проверка ошибки авторизации при отсутствии пароля")
    public void cannotLoginWithoutPassword() {
        Response response = loginWithoutPassword();

        response.then()
                .statusCode(400)
                .body("message", equalTo("Недостаточно данных для входа"));
    }

    @Step("Попытка авторизации без пароля")
    private Response loginWithoutPassword() {
        return given()
                .header("Content-type", "application/json")
                .body("")
                .when()
                .post(BASE_URL);
    }

    @Test
    @DisplayName("Ошибка при неправильном логине или пароле")
    @Description("Проверка ошибки авторизации с неправильным логином или паролем")
    public void cannotLoginWithInvalidCredentials() {
        String json = "{\"login\": \"wrong_login__09812093801293809-A_)@0diq9ias09di\", \"password\": \"wrong_password\"}";

        Response response = loginWithInvalidCredentials(json);

        response.then()
                .statusCode(404)
                .body("message", equalTo("Учетная запись не найдена"));
    }

    @Step("Попытка авторизации с неверными логином и паролем")
    private Response loginWithInvalidCredentials(String json) {
        return given()
                .header("Content-type", "application/json")
                .body(json)
                .when()
                .post(BASE_URL);
    }
}
