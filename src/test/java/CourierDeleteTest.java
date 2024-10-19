import dto.CourierDTO;
import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import services.CourierApi;

import static org.apache.http.HttpStatus.*;
import static org.hamcrest.Matchers.equalTo;

public class CourierDeleteTest {

    private final CourierApi courierApi = new CourierApi();
    private String courierId;  // Переменная для хранения ID курьера

    private String login;
    private String password;
    private String name;

    @Before
    public void setUp() {
        // Генерация данных для курьера перед каждым тестом
        login = CourierApi.generateRandomString(8);
        password = CourierApi.generateRandomString(8);
        name = CourierApi.generateRandomString(4);
        CourierDTO dto = new CourierDTO(login, password, name);

        // Создание курьера перед тестом
        courierApi.createCourierRequest(dto)
                .then()
                .statusCode(SC_CREATED)
                .body("ok", equalTo(true));

        // Авторизация и получение ID курьера для последующего удаления
        courierId = courierApi.login(dto).jsonPath().getString("id");
    }

    @After
    public void tearDown() {
        // Удаление курьера после каждого теста
        if (courierId != null) {
            Response response = courierApi.deleteCourier(courierId);
            response.then().statusCode(SC_OK).body("ok", equalTo(true));
        }
    }

    @Test
    @DisplayName("Успешное удаление курьера")
    @Description("Проверка успешного удаления курьера с корректным ID")
    public void canDeleteCourierSuccessfully() {
        // Удаляем курьера с сохраненным ID
        courierApi.deleteCourier(courierId).then()
                .statusCode(SC_OK)  // Используем SC_OK для статуса 200
                .body("ok", equalTo(true));

        // Обнуляем courierId, так как курьер уже удален
        courierId = null;
    }

    @Test
    @DisplayName("Ошибка при удалении курьера без ID")
    @Description("Проверка ошибки при попытке удаления курьера без ID")
    public void shouldReturnErrorWithoutId() {
        // Пытаемся удалить курьера без указания ID
        courierApi.deleteCourier("").then()
                .statusCode(SC_BAD_REQUEST)  // Используем SC_BAD_REQUEST для статуса 400
                .body("message", equalTo("Недостаточно данных для удаления курьера"));
    }

    @Test
    @DisplayName("Ошибка при удалении несуществующего курьера")
    @Description("Проверка ошибки при удалении курьера с несуществующим ID")
    public void shouldReturnErrorForNonExistentCourier() {
        // Пытаемся удалить курьера с несуществующим ID
        String nonExistentId = "9999";

        courierApi.deleteCourier(nonExistentId).then()
                .statusCode(SC_NOT_FOUND)  // Используем SC_NOT_FOUND для статуса 404
                .body("message", equalTo("Курьера с таким id нет."));
    }
}
