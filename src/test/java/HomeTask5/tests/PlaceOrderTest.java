package HomeTask5.tests;

import HomeTask5.BaseTest;
import HomeTask5.utils.DriverFactory;
import org.testng.Assert;
import org.testng.annotations.Test;

public class PlaceOrderTest extends BaseTest {

    @Test
    public void checkSiteVersion() {
        // TODO open main page and validate website version
        actions.goToMainPage();
        Assert.assertEquals(isMobileTesting, actions.isMobileSite(), "Inappropriate site version is open");
    }

    @Test(dependsOnMethods = "checkSiteVersion")
    public void createNewOrder() {
        // TODO implement order creation test
        // open random product
        actions.openRandomProduct();

        // save product parameters
        actions.saveProductParameters();

        // add product to Cart and validate product information in the Cart
        actions.addToCart();
        actions.goToCart();
        actions.validateProductInfo();

        // proceed to order creation, fill required information
        actions.proceedToOrderCreation();
        actions.fillTheForm();

        // place new order and validate order summary
        actions.validateOrderSummary();

        // check updated In Stock value
        actions.goToProductPage();
        actions.checkInStockValue();
    }

}
