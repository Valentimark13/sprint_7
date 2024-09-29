package services;

import io.qameta.allure.Step;
import io.restassured.response.Response;

import java.util.Random;

import static io.restassured.RestAssured.given;
import static config.ApiConfig.BASE_URL;

public class CourierApi {

    // Класс для сериализации данных курьера
    public static class CourierData {
        private String login;
        private String password;
        private String firstName;

        public CourierData(String login, String password, String firstName) {
            this.login = login;
            this.password = password;
            this.firstName = firstName;
        }

        // Геттеры и сеттеры для сериализации
        public String getLogin() {
            return login;
        }

        public void setLogin(String login) {
            this.login = login;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public String getFirstName() {
            return firstName;
        }

        public void setFirstName(String firstName) {
            this.firstName = firstName;
        }
    }

    @Step("Создание курьера с логином: {0}, паролем: {1} и именем: {2}")
    public Response createCourierRequest(String login, String password, String name) {
        CourierData courier = new CourierData(login, password, name);

        return given()
                .header("Content-type", "application/json")
                .body(courier)
                .when()
                .post(String.format("%sapi/v1/courier/", BASE_URL));
    }

    @Step("Попытка создания курьера без логина")
    public Response createCourierWithoutLogin() {
        String json = "{\"password\": \"1234\", \"firstName\": \"saske\"}";
        return given()
                .header("Content-type", "application/json")
                .body(json)
                .when()
                .post(String.format("%sapi/v1/courier/", BASE_URL));
    }

    @Step("Удаление курьера с ID: {0}")
    public Response deleteCourier(String courierId) {
        return given()
                .header("Content-type", "application/json")
                .when()
                .delete(String.format("%sapi/v1/courier/%s", BASE_URL, courierId));
    }

    @Step("Попытка авторизации без логина")
    public Response loginWithoutLogin(String json) {
        return given()
                .header("Content-type", "application/json")
                .body(json)
                .when()
                .post(String.format("%sapi/v1/courier/login", BASE_URL));
    }

    @Step("Попытка авторизации без пароля")
    public Response loginWithoutPassword() {
        return given()
                .header("Content-type", "application/json")
                .body("")
                .when()
                .post(String.format("%sapi/v1/courier/login", BASE_URL));
    }

    @Step("Попытка авторизации с неверными логином и паролем")
    public Response loginWithInvalidCredentials(String json) {
        return given()
                .header("Content-type", "application/json")
                .body(json)
                .when()
                .post(String.format("%sapi/v1/courier/login", BASE_URL));
    }

    @Step("Авторизация курьера с логином: {0} и паролем: {1}")
    public Response login(String login, String password) {
        CourierData courier = new CourierData(login, password, null); // имя не требуется для логина

        return given()
                .header("Content-type", "application/json")
                .body(courier)
                .when()
                .post(String.format("%sapi/v1/courier/login", BASE_URL));
    }

    @Step("Попытка получения количества заказов без ID курьера")
    public Response orderCountWithoutId() {
        return given()
                .header("Content-type", "application/json")
                .when()
                .get(String.format("%sapi/v1/courier//ordersCount", BASE_URL));
    }

    @Step("Получение количества заказов для курьера с ID: {0}")
    public Response orderCount(int courierId) {
        return given()
                .header("Content-type", "application/json")
                .when()
                .get(String.format("%sapi/v1/courier/%d/ordersCount", BASE_URL, courierId));
    }

    @Step("Подтверждение заказа с ID: {0} для курьера с ID: {1}")
    public Response acceptOrder(int orderId, int courierId) {
        return given()
                .header("Content-type", "application/json")
                .when()
                .put(String.format("%sapi/v1/orders/accept/%d?courierId=%d", BASE_URL, orderId, courierId));
    }

    // Метод для генерации случайной строки
    public static String generateRandomString(int length) {
        String characters = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        Random random = new Random();
        StringBuilder sb = new StringBuilder(length);

        for (int i = 0; i < length; i++) {
            sb.append(characters.charAt(random.nextInt(characters.length())));
        }
        return sb.toString();
    }
}
