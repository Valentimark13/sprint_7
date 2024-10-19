package services;

import com.google.gson.Gson;
import dto.OrderDTO;
import io.qameta.allure.Step;
import io.restassured.response.Response;

import static io.restassured.RestAssured.given;
import static config.ApiConfig.BASE_URL;  // Предполагается, что BASE_URL вынесен в отдельный конфигурационный класс

public class OrderApi {
    // Метод для создания заказа с цветом
    @Step("Создание заказа с цветом {0}")
    public Response createOrder(OrderDTO dto) {
        Gson gson = new Gson();
        String data = gson.toJson(dto);
        return given()
                .header("Content-type", "application/json")
                .body(data)
                .when()
                .post(String.format("%sapi/v1/orders", BASE_URL));
    }

    // Метод для отмены (удаления) заказа по track
    @Step("Отмена заказа с трек-номером {0}")
    public Response cancelOrder(String track) {
        return given()
                .header("Content-type", "application/json")
                .when()
                .put(String.format("%sapi/v1/orders/cancel/%s", BASE_URL, track));
    }

    // Метод для принятия заказа курьером (принимает трек заказа и ID курьера)
    @Step("Принятие заказа {0} курьером {1}")
    public Response acceptOrder(int orderId, int courierId) {
        return given()
                .header("Content-type", "application/json")
                .when()
                .put(String.format("%sapi/v1/orders/accept/%d?courierId=%d", BASE_URL, orderId, courierId));
    }

    @Step("Получение количества заказов для курьера с ID: {0}")
    public Response orderCount(int courierId) {
        return given()
                .header("Content-type", "application/json")
                .when()
                .get(String.format("%sapi/v1/courier/%d/ordersCount", BASE_URL, courierId));
    }

    @Step("Попытка получения количества заказов без ID курьера")
    public Response orderCountWithoutId() {
        return given()
                .header("Content-type", "application/json")
                .when()
                .get(String.format("%sapi/v1/courier//ordersCount", BASE_URL));
    }
}
