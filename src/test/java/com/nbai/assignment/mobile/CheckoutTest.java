package com.nbai.assignment.mobile;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import com.nbai.assignment.mobile.pages.CartPage;
import com.nbai.assignment.mobile.pages.CheckoutAddressPage;
import com.nbai.assignment.mobile.pages.CheckoutCompletePage;
import com.nbai.assignment.mobile.pages.CheckoutPaymentPage;
import com.nbai.assignment.mobile.pages.CheckoutReviewPage;
import com.nbai.assignment.mobile.pages.LoginPage;
import com.nbai.assignment.mobile.pages.ProductDetailsPage;
import com.nbai.assignment.mobile.pages.ProductsPage;
import io.appium.java_client.AppiumBy;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import java.math.BigDecimal;
import java.time.Duration;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.Test;

/** End-to-end checkout of the React Native My Demo App. Each test gets a fresh app session from BaseMobileTest. */
@Epic("Mobile app (My Demo App)")
@Feature("Checkout")
@Severity(SeverityLevel.CRITICAL)
public class CheckoutTest extends BaseMobileTest {

    private static final String PRODUCT_NAME = "Sauce Labs Backpack";

    // The app prints this demo account on its own login screen; it is public sample data, not a secret.
    private static final String VALID_USERNAME = "bob@example.com";
    private static final String VALID_PASSWORD = "10203040";

    // Test data entered explicitly so the test never depends on the app's default values. Card is a standard test number.
    private static final String FULL_NAME = "Rebecca Winter";
    private static final String ADDRESS_LINE_1 = "Mandorley 112";
    private static final String ADDRESS_LINE_2 = "Entrance 1";
    private static final String CITY = "Truro";
    private static final String STATE_REGION = "Cornwall";
    private static final String ZIP_CODE = "89750";
    private static final String COUNTRY = "United Kingdom";
    private static final String CARD_NUMBER = "4111111111111111";
    private static final String CARD_EXPIRY = "12/30";
    private static final String CARD_CVV = "123";

    @Test
    public void shouldCompleteCheckoutForTwoBackpacks() {
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
        assertEquals(cart.getQuantity(), "2", "quantity in cart");
        BigDecimal cartSubtotal = parseMoney(cart.getTotalPrice());
        cart.clickProceedToCheckout();

        // Checkout needs a logged-in user: the app shows Login first, otherwise goes straight to the address step.
        boolean loginShown = waitForLoginOrAddress();
        if (loginShown) {
            LoginPage login = new LoginPage(driver);
            login.enterUsername(VALID_USERNAME);
            login.enterPassword(VALID_PASSWORD);
            login.clickLogin();
        }

        CheckoutAddressPage address = new CheckoutAddressPage(driver);
        assertTrue(address.isLoaded(), "Checkout address screen should be displayed");
        address.enterFullName(FULL_NAME);
        address.enterAddressLine1(ADDRESS_LINE_1);
        address.enterAddressLine2(ADDRESS_LINE_2);
        address.enterCity(CITY);
        address.enterStateRegion(STATE_REGION);
        address.enterZipCode(ZIP_CODE);
        address.enterCountry(COUNTRY);
        assertEquals(address.getFullName(), FULL_NAME, "Address: Full Name entered");
        assertEquals(address.getAddressLine1(), ADDRESS_LINE_1, "Address: Address Line 1 entered");
        assertEquals(address.getAddressLine2(), ADDRESS_LINE_2, "Address: Address Line 2 entered");
        assertEquals(address.getCity(), CITY, "Address: City entered");
        assertEquals(address.getStateRegion(), STATE_REGION, "Address: State/Region entered");
        assertEquals(address.getZipCode(), ZIP_CODE, "Address: Zip Code entered");
        assertEquals(address.getCountry(), COUNTRY, "Address: Country entered");
        address.clickToPayment();

        CheckoutPaymentPage payment = new CheckoutPaymentPage(driver);
        assertTrue(payment.isLoaded(), "Checkout payment screen should be displayed");
        payment.enterFullName(FULL_NAME);
        payment.enterCardNumber(CARD_NUMBER);
        payment.enterExpirationDate(CARD_EXPIRY);
        payment.enterSecurityCode(CARD_CVV);
        assertEquals(payment.getFullName(), FULL_NAME, "Payment: Full Name entered");
        assertEquals(payment.getCardNumber().replaceAll("\\s", ""), CARD_NUMBER, "Payment: Card Number entered");
        assertEquals(payment.getExpirationDate(), CARD_EXPIRY, "Payment: Expiration Date entered");
        assertEquals(payment.getSecurityCodeLength(), CARD_CVV.length(), "Payment: Security Code entered");
        payment.ensureBillingSameAsShipping();
        assertTrue(payment.isLoaded(), "Payment screen should still be displayed after handling the billing checkbox");
        payment.clickReviewOrder();

        CheckoutReviewPage review = new CheckoutReviewPage(driver);
        assertTrue(review.isLoaded(), "Checkout review screen should be displayed");
        assertEquals(review.getProductName(), PRODUCT_NAME, "product name on review");
        assertTrue(review.areDeliveryAddressPaymentAndBillingDisplayed(),
                "Delivery address, payment info, billing address and delivery details should be displayed");
        assertEquals(review.getTotalItems(), "2 items", "total items on review");
        // Expected total = what the cart showed for 2 backpacks + the delivery charge, which must be positive.
        // Both values come from the app, so leftover cart state from earlier runs cannot break the test.
        BigDecimal reviewTotal = parseMoney(review.getTotalPrice());
        assertTrue(reviewTotal.compareTo(cartSubtotal) > 0,
                "Review total " + reviewTotal + " should be the cart total " + cartSubtotal + " plus a delivery charge");
        assertTrue(review.isPlaceOrderButtonDisplayed(), "Place Order button should be displayed");
        review.clickPlaceOrder();

        CheckoutCompletePage complete = new CheckoutCompletePage(driver);
        assertTrue(complete.isLoaded(), "Checkout complete screen should be displayed");
        assertTrue(complete.isCheckoutCompleteTextDisplayed(), "'Checkout Complete' should be displayed");
        assertTrue(complete.isThankYouTextDisplayed(), "'Thank you for your order' should be displayed");
        assertTrue(complete.isOrderOnItsWayTextDisplayed(), "'Your new swag is on its way' should be displayed");
        assertTrue(complete.isContinueShoppingButtonDisplayed(), "Continue Shopping button should be displayed");
    }

    /** Waits until either the login or the checkout address screen is shown; true if it is the login screen. */
    private boolean waitForLoginOrAddress() {
        return new WebDriverWait(driver, Duration.ofSeconds(15)).until(d -> {
            if (!d.findElements(AppiumBy.accessibilityId("login screen")).isEmpty()) {
                return true;
            }
            return d.findElements(AppiumBy.accessibilityId("checkout address screen")).isEmpty() ? null : false;
        });
    }

    private static BigDecimal parseMoney(String text) {
        return new BigDecimal(text.replaceAll("[^0-9.]", ""));
    }
}
