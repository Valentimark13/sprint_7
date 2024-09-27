package services;

import io.restassured.response.Response;

import static io.restassured.RestAssured.given;

public class Order {
    private final String BASE_URL = "https://qa-scooter.praktikum-services.ru/";

    public Response createOrder(String color) {
        String json = String.format("{\"firstName\": \"Naruto\", \"lastName\": \"Uchiha\", \"address\": \"Konoha, 142 apt.\", " +
                "\"metroStation\": 4, \"phone\": \"+7 800 355 35 35\", \"rentTime\": 5, \"deliveryDate\": \"2020-06-06\", " +
                "\"comment\": \"Saske, come back to Konoha\", \"color\": [\"%s\"]}", color);

        return given()
                .header("Content-type", "application/json")
                .body(json)
                .when()
                .post(BASE_URL+ "api/v1/orders");
    }

    public Response createOrder() {
        String json = "{\"firstName\": \"Naruto\", \"lastName\": \"Uchiha\", \"address\": \"Konoha, 142 apt.\", " +
                "\"metroStation\": 4, \"phone\": \"+7 800 355 35 35\", \"rentTime\": 5, \"deliveryDate\": \"2020-06-06\", " +
                "\"comment\": \"Saske, come back to Konoha\"}";

        return given()
                .header("Content-type", "application/json")
                .body(json)
                .when()
                .post(BASE_URL+ "api/v1/orders");
    }

    public Response acceptOrder(int orderId, int courierId) {
        return given()
                .header("Content-type", "application/json")
                .when()
                .put(BASE_URL + "api/v1/orders/accept/" + orderId + "?courierId=" + courierId);
    }
}
