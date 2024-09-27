import io.qameta.allure.Description;
import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.Before;
import org.junit.Test;
import services.Courier;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

public class CourierDeleteTest {

    private final String BASE_URL = "https://qa-scooter.praktikum-services.ru/api/v1/courier/";

    @Test
    @DisplayName("Успешное удаление курьера")
    @Description("Проверка успешного удаления курьера с корректным ID")
    public void canDeleteCourierSuccessfully() {
        String login = Courier.generateRandomString(8);
        String password = Courier.generateRandomString(8);
        String name = Courier.generateRandomString(4);
        createCourier(login, password, name);

        String courierId = loginCourierAndGetId(login, password);

        deleteCourier(courierId).then()
                .statusCode(200)
                .body("ok", equalTo(true));
    }

    @Step("Создание курьера с логином {0}, паролем {1} и именем {2}")
    private void createCourier(String login, String password, String name) {
        new Courier().createCourierRequest(login, password, name);
    }

    @Step("Авторизация курьера с логином {0} и паролем {1}")
    private String loginCourierAndGetId(String login, String password) {
        return new Courier().login(login, password).jsonPath().getString("id");
    }

    @Step("Удаление курьера с ID {0}")
    private Response deleteCourier(String courierId) {
        return given()
                .header("Content-type", "application/json")
                .when()
                .delete(BASE_URL + courierId);
    }

    @Test
    @DisplayName("Ошибка при удалении курьера без ID")
    @Description("Проверка ошибки при попытке удаления курьера без ID")
    public void shouldReturnErrorWithoutId() {
        given().header("Content-type", "application/json")
                .when()
                .delete(BASE_URL)
                .then()
                .statusCode(400)
                .body("message", equalTo("Недостаточно данных для удаления курьера"));
    }

    @Test
    @DisplayName("Ошибка при удалении несуществующего курьера")
    @Description("Проверка ошибки при удалении курьера с несуществующим ID")
    public void shouldReturnErrorForNonExistentCourier() {
        String nonExistentId = "9999";

        given().header("Content-type", "application/json")
                .when()
                .delete(BASE_URL + nonExistentId)
                .then()
                .statusCode(404)
                .body("message", equalTo("Курьера с таким id нет."));
    }
}
