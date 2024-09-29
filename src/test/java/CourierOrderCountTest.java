import io.qameta.allure.Description;
import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import services.CourierApi;
import services.OrderApi;

import static org.apache.http.HttpStatus.*;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

public class CourierOrderCountTest {

    private final CourierApi courierApi = new CourierApi();
    private final OrderApi orderApi = new OrderApi();
    private String courierId;  // Сохраняем ID курьера для последующего удаления

    private String login;
    private String password;
    private String firstName;

    @Before
    public void setUp() {
        // Генерация данных для курьера перед каждым тестом
        login = CourierApi.generateRandomString(8);
        password = CourierApi.generateRandomString(8);
        firstName = CourierApi.generateRandomString(4);

        // Создание курьера
        courierApi.createCourierRequest(login, password, firstName)
                .then()
                .statusCode(SC_CREATED)
                .body("ok", equalTo(true));

        // Авторизация и получение ID курьера для последующего удаления
        courierId = courierApi.login(login, password).jsonPath().getString("id");
    }

    @After
    public void tearDown() {
        // Удаление созданного курьера после каждого теста
        if (courierId != null) {
            Response response = courierApi.deleteCourier(courierId);
            response.then().statusCode(SC_OK).body("ok", equalTo(true));
        }
    }

    @Test
    @DisplayName("Получение количества заказов для курьера с существующим id")
    @Description("Проверка успешного получения количества заказов для курьера с валидным ID")
    public void canGetOrderCountForExistingCourier() {
        // Создаем заказ и связываем его с курьером
        int orderId = orderApi.createOrder().jsonPath().getInt("track");
        orderApi.acceptOrder(orderId, Integer.parseInt(courierId));

        // Получаем количество заказов для курьера
        Response response = courierApi.orderCount(Integer.parseInt(courierId));

        response.then()
                .statusCode(SC_OK)
                .body("id", equalTo(courierId))
                .body("ordersCount", notNullValue());  // Проверка на наличие ordersCount
    }

    @Test
    @DisplayName("Получение ошибки 400 при запросе без ID курьера")
    @Description("Проверка ошибки 400 при попытке получения количества заказов без указания ID курьера")
    public void shouldReturnErrorWithoutCourierId() {
        // Пытаемся получить количество заказов без ID
        Response response = courierApi.orderCountWithoutId();

        response.then()
                .statusCode(SC_BAD_REQUEST)  // Используем SC_BAD_REQUEST для статуса 400
                .body("message", equalTo("Недостаточно данных для поиска"));
    }

    @Test
    @DisplayName("Получение ошибки 404 при запросе с несуществующим ID курьера")
    @Description("Проверка ошибки 404 при запросе количества заказов для несуществующего ID курьера")
    public void shouldReturnNotFoundForNonExistentCourier() {
        int nonExistentId = 99999;  // Используем несуществующий ID курьера

        // Пытаемся получить количество заказов для несуществующего курьера
        Response response = courierApi.orderCount(nonExistentId);

        response.then()
                .statusCode(SC_NOT_FOUND)  // Используем SC_NOT_FOUND для статуса 404
                .body("message", equalTo("Курьер не найден"));
    }
}
