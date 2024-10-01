import dto.CourierDTO;
import io.qameta.allure.Description;
import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import services.CourierApi;

import static org.apache.http.HttpStatus.*;
import static org.hamcrest.Matchers.equalTo;

public class CourierLoginTest {

    private final CourierApi courierApi = new CourierApi();
    private String courierId;

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

        // Создание курьера перед каждым тестом
        courierApi.createCourierRequest(dto)
                .then()
                .statusCode(SC_CREATED)
                .body("ok", equalTo(true));

        // Авторизация и получение ID курьера для последующего удаления
        courierId = courierApi.login(dto).jsonPath().getString("id");
    }

    @After
    public void tearDown() {
        // Удаление курьера после выполнения тестов
        if (courierId != null) {
            Response response = courierApi.deleteCourier(courierId);
            response.then().statusCode(SC_OK).body("ok", equalTo(true));
        }
    }

    @Test
    @DisplayName("Курьер может успешно авторизоваться")
    @Description("Проверка успешной авторизации курьера с валидными данными")
    public void courierCanLoginSuccessfully() {
        CourierDTO dto = new CourierDTO(login, password, name);
        Response response = courierApi.login(dto);
        response.then().statusCode(SC_OK);  // Используем SC_OK для статуса 200
    }

    @Test
    @DisplayName("Ошибка при отсутствии логина")
    @Description("Проверка ошибки авторизации при отсутствии логина")
    public void cannotLoginWithoutLogin() {
        // Пытаемся авторизоваться без логина
        CourierDTO dto = new CourierDTO(null, password, name);
        Response response = courierApi.login(dto);

        response.then()
                .statusCode(SC_BAD_REQUEST)  // Используем SC_BAD_REQUEST для статуса 400
                .body("message", equalTo("Недостаточно данных для входа"));
    }

    @Test
    @DisplayName("Ошибка при отсутствии пароля")
    @Description("Проверка ошибки авторизации при отсутствии пароля")
    public void cannotLoginWithoutPassword() {
        CourierDTO dto = new CourierDTO(login, null, name);
        Response response = courierApi.login(dto);
        response.then()
                .statusCode(SC_BAD_REQUEST)  // Используем SC_BAD_REQUEST для статуса 400
                .body("message", equalTo("Недостаточно данных для входа"));
    }

    @Test
    @DisplayName("Ошибка при неправильном логине или пароле")
    @Description("Проверка ошибки авторизации с неправильным логином или паролем")
    public void cannotLoginWithInvalidCredentials() {
        CourierDTO dto = new CourierDTO("wrong_login__09812093801293809", "wrong_password", name);
        Response response = courierApi.login(dto);

        response.then()
                .statusCode(SC_NOT_FOUND)  // Используем SC_NOT_FOUND для статуса 404
                .body("message", equalTo("Учетная запись не найдена"));
    }
}
