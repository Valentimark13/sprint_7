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

public class CourierTest {

    private final CourierApi courierApi = new CourierApi();
    private String courierId;  // Переменная для хранения ID курьера
    private CourierDTO dto;

    @Before
    public void setUp() {
        // Генерируем данные для курьера перед каждым тестом
        String randomLogin = CourierApi.generateRandomString(8);
        String randomPassword = CourierApi.generateRandomString(8);
        String randomFirstName = CourierApi.generateRandomString(4);
        this.dto = new CourierDTO(randomLogin, randomPassword, randomFirstName);
    }

    @After
    public void tearDown() {
        // Удаляем созданного курьера после каждого теста, если он был создан
        if (courierId != null) {
            Response response = courierApi.deleteCourier(courierId);
            response.then().statusCode(SC_OK).body("ok", equalTo(true));
        }
    }

    @Test
    @DisplayName("Курьер успешно создается")
    @Description("Проверка успешного создания курьера с валидными данными")
    public void courierCanBeCreated() {
        // Создаем курьера
        Response response = courierApi.createCourierRequest(dto);
        response.then().statusCode(SC_CREATED).body("ok", equalTo(true));

        // Получаем ID курьера для последующего удаления
        courierId = courierApi.login(dto).jsonPath().getString("id");
    }

    @Test
    @DisplayName("Ошибка при создании курьера с существующим логином")
    @Description("Проверка ошибки при попытке создать курьера с уже существующим логином")
    public void cannotCreateCourierWithExistingLogin() {
        // Создаем первого курьера
        Response response = courierApi.createCourierRequest(dto);
        response.then().statusCode(SC_CREATED).body("ok", equalTo(true));

        // Сохраняем ID курьера для последующего удаления
        courierId = courierApi.login(dto).jsonPath().getString("id");

        // Попытка создать курьера с тем же логином
        courierApi.createCourierRequest(dto)
                .then()
                .statusCode(SC_CONFLICT)  // Используем SC_CONFLICT для 409
                .body("message", equalTo("Этот логин уже используется. Попробуйте другой."));
    }

    @Test
    @DisplayName("Ошибка при создании курьера без логина")
    @Description("Проверка ошибки при попытке создать курьера без указания логина")
    public void cannotCreateCourierWithoutLogin() {
        CourierDTO dto = new CourierDTO(null, CourierApi.generateRandomString(8), CourierApi.generateRandomString(4));
        courierApi.createCourierRequest(dto)
                .then()
                .statusCode(SC_BAD_REQUEST)  // Используем SC_BAD_REQUEST для 400
                .body("message", equalTo("Недостаточно данных для создания учетной записи"));
    }
}
