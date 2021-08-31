import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.WebDriverRunner;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Cookie;

import static com.codeborne.selenide.Selenide.*;
import static io.restassured.RestAssured.given;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.hamcrest.CoreMatchers.is;

public class DemoWebShopTests {

    String email = "joriyav670@mnqlm.com";
    String password = "12345671";
    int count = 5;
    SelenideElement quantityWishlist = $("span.wishlist-qty");


    @Test
    void addToWishlist() {
        open("http://demowebshop.tricentis.com/login");
        $("#Email").setValue(email);
        $("#Password").setValue(password);
        $("[for='RememberMe']").click();
        $("[value='Log in']").click();
        Cookie cookieToken = WebDriverRunner.getWebDriver().manage().getCookieNamed("NOPCOMMERCE.AUTH");
        String quantityWishlistString = quantityWishlist.text().replaceAll("[()]", "");
        int quantityWishlistInt = Integer.parseInt (quantityWishlistString.trim ());
        closeWebDriver();

        String response =
                given()
                        .contentType("application/x-www-form-urlencoded; charset=UTF-8")
                        .body("addtocart_14.EnteredQuantity=" + count)
                        .cookie(cookieToken.toString())
                        .when()
                        .post("http://demowebshop.tricentis.com/addproducttocart/details/14/2")
                        .then()
                        .statusCode(200)
                        .body("success", is(true))
                        .body("updatetopwishlistsectionhtml", is("(" + (quantityWishlistInt + count) + ")"))
                        .contentType(ContentType.JSON)
                        .extract().body().jsonPath()
                        .getString("updatetopwishlistsectionhtml");

        open("http://demowebshop.tricentis.com/favicon.ico");
        WebDriverRunner.getWebDriver().manage().addCookie(cookieToken);
        open("http://demowebshop.tricentis.com");
        String quantityWishlistStringNew = quantityWishlist.text();
        assertThat(quantityWishlistStringNew).isEqualTo(response);

    }
}
