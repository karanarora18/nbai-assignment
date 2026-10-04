package com.nbai.assignment.mobile;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import com.nbai.assignment.mobile.pages.LoginPage;
import com.nbai.assignment.mobile.pages.ProductsPage;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import org.testng.annotations.Test;

/** Login flows of the React Native My Demo App. Each test gets a fresh app session from BaseMobileTest. */
@Epic("Mobile app (My Demo App)")
@Feature("Login")
public class LoginTest extends BaseMobileTest {

    // The app prints this demo account on its own login screen; it is public sample data, not a secret.
    private static final String VALID_USERNAME = "bob@example.com";
    private static final String VALID_PASSWORD = "10203040";

    private static final String INVALID_USERNAME = "invalid@example.com";
    private static final String INVALID_PASSWORD = "wrongpassword";
    private static final String INVALID_LOGIN_MESSAGE = "Provided credentials do not match any user in this service.";

    @Test
    public void validLogin() {
        LoginPage login = new LoginPage(driver);
        login.navigateToLogin();
        assertTrue(login.isLoaded(), "Login screen should be displayed after choosing Log In from the menu");

        login.enterUsername(VALID_USERNAME);
        login.enterPassword(VALID_PASSWORD);
        login.clickLogin();

        assertTrue(new ProductsPage(driver).isLoaded(), "Products screen should be displayed after a valid login");
    }

    @Test
    public void invalidLogin() {
        LoginPage login = new LoginPage(driver);
        login.navigateToLogin();
        assertTrue(login.isLoaded(), "Login screen should be displayed after choosing Log In from the menu");

        login.enterUsername(INVALID_USERNAME);
        login.enterPassword(INVALID_PASSWORD);
        login.clickLogin();

        assertTrue(login.isLoaded(), "Login screen should remain displayed after an invalid login");
        assertTrue(login.isGenericErrorDisplayed(), "A generic error should be displayed");
        assertEquals(login.getGenericErrorMessage(), INVALID_LOGIN_MESSAGE, "generic error text");
    }
}
