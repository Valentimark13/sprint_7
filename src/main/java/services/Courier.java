package services;

import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;

import java.util.Random;

import static io.restassured.RestAssured.given;

public class Courier {

    private final String BASE_URL = "https://qa-scooter.praktikum-services.ru/";

    // Метод для создания курьера с случайными логином и паролем
    public Response createCourierRequest(String login, String password, String name) {
        // Создаем JSON для создания курьера
        String json = String.format("{\"login\": \"%s\", \"password\": \"%s\", \"firstName\": \"%s\"}",
                login, password, name);

        // Выполняем запрос на создание курьера
        Response response = given()
                .header("Content-type", "application/json")
                .body(json)
                .when()
                .post(BASE_URL + "api/v1/courier/");

        return response;
    }

    public Response login(String login, String password) {
        String json = String.format("{\"login\": \"%s\", \"password\": \"%s\"}",
                login, password);

        // Выполняем запрос на создание курьера
        return given()
                .header("Content-type", "application/json")
                .body(json)
                .when()
                .post(BASE_URL + "api/v1/courier/login");
    }

    public static String generateRandomString(int length) {
        String characters = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        Random random = new Random();
        StringBuilder sb = new StringBuilder(length);

        for (int i = 0; i < length; i++) {
            sb.append(characters.charAt(random.nextInt(characters.length())));
        }
        return sb.toString();
    }

    public Response orderCount(int courierId) {
        return given()
                .header("Content-type", "application/json")
                .when()
                .get(BASE_URL + "api/v1/courier/" + courierId + "/ordersCount");
    }
}
