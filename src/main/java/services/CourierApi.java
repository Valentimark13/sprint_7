package services;

import com.google.gson.Gson;
import dto.CourierDTO;
import io.qameta.allure.Step;
import io.restassured.response.Response;

import java.util.Random;

import static io.restassured.RestAssured.given;
import static config.ApiConfig.BASE_URL;

public class CourierApi {
    @Step("Создание курьера с логином: {0}, паролем: {1} и именем: {2}")
    public Response createCourierRequest(CourierDTO courier) {
        Gson gson = new Gson();
        String data = gson.toJson(courier);

        return given()
                .header("Content-type", "application/json")
                .body(data)
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

    @Step("Авторизация курьера с логином: {0} и паролем: {1}")
    public Response login(CourierDTO courier) {
        Gson gson = new Gson();
        String data = gson.toJson(courier);
        return given()
                .header("Content-type", "application/json")
                .body(data)
                .when()
                .post(String.format("%sapi/v1/courier/login", BASE_URL));
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
