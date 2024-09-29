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

        // Создание курьера перед каждым тестом
        courierApi.createCourierRequest(login, password, name)
                .then()
                .statusCode(SC_CREATED)
                .body("ok", equalTo(true));

        // Авторизация и получение ID курьера для последующего удаления
        courierId = courierApi.login(login, password).jsonPath().getString("id");
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
        // Пытаемся авторизоваться
        Response response = courierApi.login(login, password);
        response.then().statusCode(SC_OK);  // Используем SC_OK для статуса 200
    }

    @Test
    @DisplayName("Ошибка при отсутствии логина")
    @Description("Проверка ошибки авторизации при отсутствии логина")
    public void cannotLoginWithoutLogin() {
        // Пытаемся авторизоваться без логина
        String json = "{\"password\": \"1234\"}";
        Response response = courierApi.loginWithoutLogin(json);

        response.then()
                .statusCode(SC_BAD_REQUEST)  // Используем SC_BAD_REQUEST для статуса 400
                .body("message", equalTo("Недостаточно данных для входа"));
    }

    @Test
    @DisplayName("Ошибка при отсутствии пароля")
    @Description("Проверка ошибки авторизации при отсутствии пароля")
    public void cannotLoginWithoutPassword() {
        // Пытаемся авторизоваться без пароля
        Response response = courierApi.loginWithoutPassword();

        response.then()
                .statusCode(SC_BAD_REQUEST)  // Используем SC_BAD_REQUEST для статуса 400
                .body("message", equalTo("Недостаточно данных для входа"));
    }

    @Test
    @DisplayName("Ошибка при неправильном логине или пароле")
    @Description("Проверка ошибки авторизации с неправильным логином или паролем")
    public void cannotLoginWithInvalidCredentials() {
        // Пытаемся авторизоваться с некорректными данными
        String json = "{\"login\": \"wrong_login__09812093801293809-A_)@0diq9ias09di\", \"password\": \"wrong_password\"}";
        Response response = courierApi.loginWithInvalidCredentials(json);

        response.then()
                .statusCode(SC_NOT_FOUND)  // Используем SC_NOT_FOUND для статуса 404
                .body("message", equalTo("Учетная запись не найдена"));
    }
}
