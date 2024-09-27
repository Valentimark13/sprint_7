import io.qameta.allure.Description;
import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import org.junit.Test;
import services.Order;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.notNullValue;

public class OrderCreationTest {

    @Test
    @DisplayName("Создание заказа с цветом BLACK")
    @Description("Проверка успешного создания заказа с указанием цвета BLACK")
    public void canCreateOrderWithBlackColor() {
        Response response = createOrderWithColor("BLACK");

        response.then()
                .statusCode(201)
                .body("track", notNullValue());  // Проверяем, что track возвращается и не null
    }

    @Step("Создание заказа с цветом {0}")
    private Response createOrderWithColor(String color) {
        return (new Order()).createOrder(color);
    }

    @Test
    @DisplayName("Создание заказа с цветом GREY")
    @Description("Проверка успешного создания заказа с указанием цвета GREY")
    public void canCreateOrderWithGreyColor() {
        Response response = createOrderWithColor("GREY");

        response.then()
                .statusCode(201)
                .body("track", notNullValue());  // Проверяем, что track возвращается и не null
    }

    @Test
    @DisplayName("Создание заказа с двумя цветами: BLACK и GREY")
    @Description("Проверка успешного создания заказа с двумя цветами: BLACK и GREY")
    public void canCreateOrderWithBothColors() {
        Response response = createOrderWithColor("BLACK, GREY");

        response.then()
                .statusCode(201)
                .body("track", notNullValue());  // Проверяем, что track возвращается и не null
    }

    @Test
    @DisplayName("Создание заказа без указания цвета")
    @Description("Проверка успешного создания заказа без указания цвета")
    public void canCreateOrderWithoutColor() {
        Response response = createOrderWithoutColor();

        response.then()
                .statusCode(201)
                .body("track", notNullValue());  // Проверяем, что track возвращается и не null
    }

    @Step("Создание заказа без указания цвета")
    private Response createOrderWithoutColor() {
        return (new Order()).createOrder();
    }
}
