package services;

import io.qameta.allure.Step;
import io.restassured.response.Response;

import static io.restassured.RestAssured.given;
import static config.ApiConfig.BASE_URL;  // Предполагается, что BASE_URL вынесен в отдельный конфигурационный класс
import static org.apache.http.HttpStatus.*;

public class OrderApi {

    // Класс для данных заказа (может использоваться для сериализации)
    public static class OrderData {
        private String firstName;
        private String lastName;
        private String address;
        private int metroStation;
        private String phone;
        private int rentTime;
        private String deliveryDate;
        private String comment;
        private String[] color;

        public OrderData(String firstName, String lastName, String address, int metroStation, String phone, int rentTime,
                         String deliveryDate, String comment, String[] color) {
            this.firstName = firstName;
            this.lastName = lastName;
            this.address = address;
            this.metroStation = metroStation;
            this.phone = phone;
            this.rentTime = rentTime;
            this.deliveryDate = deliveryDate;
            this.comment = comment;
            this.color = color;
        }

        // Геттеры и сеттеры, если необходимо
    }

    // Метод для создания заказа с цветом
    @Step("Создание заказа с цветом {0}")
    public Response createOrder(String color) {
        OrderData order = new OrderData(
                "Naruto", "Uchiha", "Konoha, 142 apt.", 4, "+7 800 355 35 35", 5, "2020-06-06",
                "Saske, come back to Konoha", new String[]{color}
        );

        return given()
                .header("Content-type", "application/json")
                .body(order)
                .when()
                .post(String.format("%sapi/v1/orders", BASE_URL));
    }

    // Метод для создания заказа без указания цвета
    @Step("Создание заказа без указания цвета")
    public Response createOrder() {
        OrderData order = new OrderData(
                "Naruto", "Uchiha", "Konoha, 142 apt.", 4, "+7 800 355 35 35", 5, "2020-06-06",
                "Saske, come back to Konoha", null
        );

        return given()
                .header("Content-type", "application/json")
                .body(order)
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
}
