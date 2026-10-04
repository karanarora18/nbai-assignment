package com.nbai.assignment.mobile;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import com.nbai.assignment.mobile.pages.ProductDetailsPage;
import com.nbai.assignment.mobile.pages.ProductsPage;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import org.testng.annotations.Test;

/** Product Details flow of the React Native My Demo App. Each test gets a fresh app session from BaseMobileTest. */
@Epic("Mobile app (My Demo App)")
@Feature("Product details")
public class ProductTest extends BaseMobileTest {

    private static final String PRODUCT_NAME = "Sauce Labs Backpack";
    private static final String PRODUCT_PRICE = "$29.99";

    @Test
    public void shouldShowProductDetailsAndAddToCart() {
        ProductsPage products = new ProductsPage(driver);
        assertTrue(products.isLoaded(), "Products screen should be displayed after launching the app");

        products.openProduct(PRODUCT_NAME);

        ProductDetailsPage details = new ProductDetailsPage(driver);
        assertTrue(details.isLoaded(), "Product Details screen should be displayed");
        assertTrue(details.isProductNameDisplayed(PRODUCT_NAME), "Product name should be " + PRODUCT_NAME);
        assertEquals(details.getProductPrice(), PRODUCT_PRICE, "product price");
        assertEquals(details.getQuantity(), "1", "default quantity");

        details.increaseQuantity();
        assertEquals(details.getQuantity(), "2", "quantity after increasing once");

        details.clickAddToCart();
        assertTrue(details.waitForCartBadge("2"), "Cart badge should show 2 after adding two units");
    }
}
