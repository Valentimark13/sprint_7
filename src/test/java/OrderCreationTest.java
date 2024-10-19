import dto.OrderDTO;
import io.qameta.allure.Description;
import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import services.OrderApi;

import java.util.Arrays;
import java.util.Collection;

import static org.apache.http.HttpStatus.*;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

@RunWith(Parameterized.class)
public class OrderCreationTest {

    @Parameterized.Parameter
    public String color;

    private String track;  // Переменная для хранения номера заказа

    @Parameterized.Parameters(name = "{index}: Test with color={0}")
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{
                {"BLACK"}, {"GREY"}, {"BLACK, GREY"}, {""}
        });
    }

    @Test
    @DisplayName("Создание заказа с разными цветами")
    @Description("Проверка успешного создания заказа с указанием цвета")
    public void canCreateOrderWithColor() {
        Response response = createOrder(color);

        response.then()
                .statusCode(SC_CREATED)
                .body("track", notNullValue());  // Проверяем, что track возвращается и не null

        // Сохраняем track для удаления заказа после теста
        track = response.jsonPath().getString("track");
    }

    @Step("Создание заказа с цветом {0}")
    private Response createOrder(String color) {
        if (color.isEmpty()) {
            return new OrderApi().createOrder(new OrderDTO("Naruto", "Uchiha", "Konoha, 142 apt.", 4, "+7 800 355 35 35", 5, "2020-06-06",
                    "Saske, come back to Konoha", null));
        } else {
            return new OrderApi().createOrder(new OrderDTO("Naruto", "Uchiha", "Konoha, 142 apt.", 4, "+7 800 355 35 35", 5, "2020-06-06",
                    "Saske, come back to Konoha", new String[]{color}));
        }
    }

    @After
    @Step("Удаление созданного заказа после теста")
    public void tearDown() {
        if (track != null) {
            new OrderApi().cancelOrder(track)  // Метод для удаления заказа по track
                    .then()
                    .statusCode(SC_OK)  // Проверяем, что заказ успешно удален
                    .body("ok", equalTo(true));
        }
    }
}
