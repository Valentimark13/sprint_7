import io.qameta.allure.Description;
import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import org.junit.Test;
import services.Courier;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

public class CourierTest {

    private final String BASE_URL = "https://qa-scooter.praktikum-services.ru/api/v1/courier/";

    @Test
    @DisplayName("Курьер успешно создается")
    @Description("Проверка успешного создания курьера с валидными данными")
    public void courierCanBeCreated() {
        String randomLogin = Courier.generateRandomString(8);
        String randomPassword = Courier.generateRandomString(8);
        String randomFirstName = Courier.generateRandomString(4);

        createCourier(randomLogin, randomPassword, randomFirstName)
                .then()
                .statusCode(201)
                .body("ok", equalTo(true));
    }

    @Step("Создание курьера с логином {0}, паролем {1} и именем {2}")
    private Response createCourier(String login, String password, String firstName) {
        return (new Courier()).createCourierRequest(login, password, firstName);
    }

    @Test
    @DisplayName("Ошибка при создании курьера с существующим логином")
    @Description("Проверка ошибки при попытке создать курьера с уже существующим логином")
    public void cannotCreateCourierWithExistingLogin() {
        String login = Courier.generateRandomString(8);
        String password = Courier.generateRandomString(8);
        String name = Courier.generateRandomString(4);

        // Создаем первого курьера
        createCourier(login, password, name);

        // Попытка создать курьера с тем же логином
        createCourier(login, password, name)
                .then()
                .statusCode(409)
                .body("message", equalTo("Этот логин уже используется. Попробуйте другой."));
    }

    @Test
    @DisplayName("Ошибка при создании курьера без логина")
    @Description("Проверка ошибки при попытке создать курьера без указания логина")
    public void cannotCreateCourierWithoutLogin() {
        String json = "{\"password\": \"1234\", \"firstName\": \"saske\"}";

        createCourierWithoutLogin(json)
                .then()
                .statusCode(400)
                .body("message", equalTo("Недостаточно данных для создания учетной записи"));
    }

    @Step("Попытка создания курьера без логина")
    private Response createCourierWithoutLogin(String json) {
        return given()
                .header("Content-type", "application/json")
                .body(json)
                .when()
                .post(BASE_URL);
    }
}
