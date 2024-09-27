import io.qameta.allure.Description;
import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import org.junit.Test;
import services.Courier;
import services.Order;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

public class CourierOrderCountTest {

    private final String BASE_URL = "https://qa-scooter.praktikum-services.ru/api/v1/courier";

    @Test
    @DisplayName("Получение количества заказов для курьера с существующим id")
    @Description("Проверка успешного получения количества заказов для курьера с валидным ID")
    public void canGetOrderCountForExistingCourier() {
        Courier courier = new Courier();
        Order order = new Order();

        String login = Courier.generateRandomString(8);
        String password = Courier.generateRandomString(8);
        String name = Courier.generateRandomString(4);

        createCourier(login, password, name);

        int courierId = loginCourierAndGetId(login, password);
        int orderId = createAndAcceptOrder(order, courierId);

        Response response = getOrderCount(courierId);

        response.then()
                .statusCode(200)
                .body("id", equalTo(String.valueOf(courierId)))
                .body("ordersCount", notNullValue());  // Проверка на наличие ordersCount
    }

    @Step("Создание курьера с логином {0}, паролем {1} и именем {2}")
    private void createCourier(String login, String password, String name) {
        new Courier().createCourierRequest(login, password, name);
    }

    @Step("Авторизация курьера и получение его ID")
    private int loginCourierAndGetId(String login, String password) {
        return new Courier().login(login, password).jsonPath().getInt("id");
    }

    @Step("Создание заказа и его привязка к курьеру с ID {1}")
    private int createAndAcceptOrder(Order order, int courierId) {
        int orderId = order.createOrder().jsonPath().getInt("track");
        order.acceptOrder(orderId, courierId);
        return orderId;
    }

    @Step("Получение количества заказов для курьера с ID {0}")
    private Response getOrderCount(int courierId) {
        return new Courier().orderCount(courierId);
    }

    @Test
    @DisplayName("Получение ошибки 400 при запросе без ID курьера")
    @Description("Проверка ошибки 400 при попытке получения количества заказов без указания ID курьера")
    public void shouldReturnErrorWithoutCourierId() {
        Response response = given()
                .header("Content-type", "application/json")
                .when()
                .get(BASE_URL + "//ordersCount");

        response.then()
                .statusCode(400)
                .body("message", equalTo("Недостаточно данных для поиска"));
    }

    @Test
    @DisplayName("Получение ошибки 404 при запросе с несуществующим ID курьера")
    @Description("Проверка ошибки 404 при запросе количества заказов для несуществующего ID курьера")
    public void shouldReturnNotFoundForNonExistentCourier() {
        int nonExistentId = 99999;  // Используем несуществующий ID курьера

        Response response = given()
                .header("Content-type", "application/json")
                .when()
                .get(BASE_URL + "/" + nonExistentId + "/ordersCount");

        response.then()
                .statusCode(404)
                .body("message", equalTo("Курьер не найден"));
    }
}
