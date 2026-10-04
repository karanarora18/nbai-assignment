package com.nbai.assignment.mobile;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import com.nbai.assignment.mobile.pages.CartPage;
import com.nbai.assignment.mobile.pages.ProductDetailsPage;
import com.nbai.assignment.mobile.pages.ProductsPage;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import org.testng.annotations.Test;

/** Cart flow of the React Native My Demo App. Each test gets a fresh app session from BaseMobileTest. */
@Epic("Mobile app (My Demo App)")
@Feature("Cart")
public class CartTest extends BaseMobileTest {

    private static final String PRODUCT_NAME = "Sauce Labs Backpack";
    private static final String PRODUCT_PRICE = "$29.99";

    @Test
    public void shouldShowAddedProductInCart() {
        ProductsPage products = new ProductsPage(driver);
        assertTrue(products.isLoaded(), "Products screen should be displayed after launching the app");

        products.openProduct(PRODUCT_NAME);
        ProductDetailsPage details = new ProductDetailsPage(driver);
        assertTrue(details.isLoaded(), "Product Details screen should be displayed");

        details.increaseQuantity();
        details.clickAddToCart();
        details.openCart();

        CartPage cart = new CartPage(driver);
        assertTrue(cart.isLoaded(), "Cart screen should be displayed");
        assertEquals(cart.getProductName(), PRODUCT_NAME, "product name in cart");
        assertEquals(cart.getProductPrice(), PRODUCT_PRICE, "product price in cart");
        assertEquals(cart.getQuantity(), "2", "quantity in cart");
        assertEquals(cart.getTotalItems(), "2 items", "total items");
        assertEquals(cart.getTotalPrice(), "$59.98", "total price");
        assertTrue(cart.isCheckoutButtonDisplayed(), "Proceed To Checkout button should be displayed");
    }
}
