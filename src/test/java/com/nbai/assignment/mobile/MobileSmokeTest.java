package com.nbai.assignment.mobile;

import static org.testng.Assert.assertTrue;

import com.nbai.assignment.mobile.pages.ProductsPage;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import org.testng.annotations.Test;

/** Proves the local Appium setup works end to end: install the app, launch it, see the Products screen. */
@Epic("Mobile app (My Demo App)")
@Feature("Smoke")
public class MobileSmokeTest extends BaseMobileTest {

    @Test
    public void shouldLaunchAppAndShowProductsScreen() {
        ProductsPage products = new ProductsPage(driver);

        assertTrue(products.isLoaded(), "Products screen should be displayed after launching the app");
    }
}
