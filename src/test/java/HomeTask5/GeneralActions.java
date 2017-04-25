package HomeTask5;


import HomeTask5.model.ProductData;
import HomeTask5.utils.DataConverter;
import HomeTask5.utils.Properties;
import HomeTask5.utils.logging.CustomReporter;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Contains main script actions that may be used in scripts.
 */
public class GeneralActions {
    private WebDriver driver;
    private ProductData product;
    protected String productURL = Properties.getBaseUrl();

    public GeneralActions(WebDriver driver) {
        this.driver = driver;
    }

    public WebDriverWait explicitWait() {
        return (WebDriverWait) new WebDriverWait(this.driver, 30).ignoring(StaleElementReferenceException.class);
    }

    public WebElement waitElementVisibility(By locator) {
        return explicitWait().until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    public List<WebElement> waitElementsPresence(By locator) {
        return explicitWait().until(ExpectedConditions.presenceOfAllElementsLocatedBy(locator));
    }

    public WebElement waitElementPresence(By locator) {
        return explicitWait().until(ExpectedConditions.presenceOfElementLocated(locator));
    }

    public WebElement waitElementToBeClickable(By locator) {
        return explicitWait().until(ExpectedConditions.elementToBeClickable(locator));
    }

    public ExpectedCondition<Boolean> jQueryAJAXCallsAreCompleted() {
        return new ExpectedCondition<Boolean>() {
            @Override
            public Boolean apply(WebDriver driver) {
                return (Boolean) ((JavascriptExecutor) driver).executeScript("return (window.jQuery != null) && (jQuery.active === 0);");
            }
        };
    }

    public void waitForPageLoad() {
        explicitWait().until(new ExpectedCondition<Boolean>() {
            public Boolean apply(WebDriver driver) {
                return ((JavascriptExecutor) driver).executeScript(
                        "return document.readyState"
                ).equals("complete");
            }
        });
    }

    public void goToMainPage() {
        CustomReporter.logAction("Open main page");
        driver.navigate().to(Properties.getBaseUrl());
        waitForPageLoad();
    }

    public boolean isMobileSite() {
        WebElement mobileElement = driver.findElement(By.xpath("//*[@class='hidden-md-up text-xs-center mobile']"));
        return mobileElement.isDisplayed();
    }

    public void openRandomProduct() {
        CustomReporter.logAction("Open random product");
        // TODO implement logic to open random product before purchase
        waitElementPresence(By.xpath("//a[@class='all-product-link pull-xs-left pull-md-right h4']/i")).click();
        List<WebElement> articles = new ArrayList<>(explicitWait().until(ExpectedConditions.
                visibilityOfAllElementsLocatedBy(By.xpath("//div/h1/a"))));
        Random random = new Random();
        articles.get(random.nextInt(articles.size())).click();
        waitForPageLoad();
        explicitWait().until(jQueryAJAXCallsAreCompleted());
        productURL = driver.getCurrentUrl();
    }

    public void saveProductParameters() {
        CustomReporter.logAction("Get information about currently opened product");
        String productName = driver.findElement(By.xpath("//h1[@itemprop]")).getText().trim().toLowerCase();
        waitElementPresence(By.xpath("//a[@href='#product-details']")).click();
        waitForPageLoad();
        explicitWait().until(jQueryAJAXCallsAreCompleted());
        String qty = extractNumber(waitElementToBeClickable(By.xpath("//div[@class='product-quantities']/span")).getText().trim());
        String price = driver.findElement(By.xpath("//span[@itemprop='price']")).getText().trim();
        //System.out.println(productName + " " + qty + " " + price + " - " + DataConverter.parsePriceValue(price));
        product = new ProductData(productName, DataConverter.parseStockValue(qty), DataConverter.parsePriceValue(price));
    }

    public void addToCart() {
        CustomReporter.logAction("Add product to cart");
        waitElementToBeClickable(By.xpath("//button[@class='btn btn-primary add-to-cart']")).click();
        waitElementVisibility(By.xpath("//div[@id='blockcart-modal']//div[@class='modal-body']"));
    }

    public void goToCart() {
        CustomReporter.logAction("Go to cart");
        waitElementToBeClickable(By.xpath("//a[@class='btn btn-primary']")).click();
        waitElementVisibility(By.xpath("//section/div[@class='container']"));
    }

    public void validateProductInfo() {
        //System.out.println(driver.findElement(By.xpath("//span[@class='label js-subtotal']")).getText());
        String subTotalQty = extractNumber(driver.findElement(By.xpath("//span[@class='label js-subtotal']")).getText());
        Assert.assertTrue(subTotalQty.equals("1"), "Wrong number of items is added to cart");
        String productNameCompare = driver.findElement(By
                .xpath("//div[@class='product-line-info']/a")).getText().toLowerCase().trim();
        Assert.assertTrue(productNameCompare.equals(product.getName()), "Wrong product name detected in the cart");
        String comparingPrice = driver.findElement(By.tagName("strong")).getText().trim();
        //System.out.println(comparingPrice);
        Assert.assertTrue(DataConverter.parsePriceValue(comparingPrice) == product.getPrice(),
                "Product price in the cart doesn't correspond to the price on the product page");
    }

    public void proceedToOrderCreation() {
        CustomReporter.logAction("Go to order creation page");
        waitElementToBeClickable(By.xpath("//a[@class='btn btn-primary']")).click();
    }

    public void fillTheForm() {
        CustomReporter.logAction("Fill in information");
        String firstname = "Vitaliy";
        String lastname = "Zamlinnyi";
        String email = "test7671564@gmail.com";
        String address = "11 Wall St";
        String zip = "10005";
        String city = "New York";
        waitElementToBeClickable(By.name("firstname")).sendKeys(firstname);
        waitElementToBeClickable(By.name("lastname")).sendKeys(lastname);
        waitElementToBeClickable(By.name("email")).sendKeys(email);
        waitElementToBeClickable
                (By.xpath("//button[@name='continue' and @data-link-action='register-new-customer']")).click();
        waitForPageLoad();

        waitElementToBeClickable(By.name("address1")).sendKeys(address);
        waitElementToBeClickable(By.name("postcode")).sendKeys(zip);
        waitElementToBeClickable(By.name("city")).sendKeys(city);
        waitElementToBeClickable(By.name("confirm-addresses")).click();
        waitElementToBeClickable(By.name("confirmDeliveryOption")).click();

        List<WebElement> paymentRadioBtn = waitElementsPresence(By.xpath("//input[@name='payment-option']"));
        Random random = new Random();
        paymentRadioBtn.get(random.nextInt(2)).click();
        waitElementPresence(By.id("conditions_to_approve[terms-and-conditions]")).click();
        waitElementToBeClickable(By.xpath("//button[@class='btn btn-primary center-block']")).click();
        waitForPageLoad();
    }

    public void validateOrderSummary() {
        String message = waitElementVisibility(By.xpath("//h3[@class='h1 card-title']")).getText().toLowerCase().trim();
        String compareMessage = "ваш заказ подтверждён";
        //System.out.println(message + " " + compareMessage);
        Assert.assertTrue(message.contains(compareMessage), "Order is not confirmed");
        String name = waitElementVisibility(By
                .xpath("//div[@class='col-sm-4 col-xs-9 details']/span\n")).getText().toLowerCase();
        Assert.assertTrue(name.contains(product.getName()), "Product name doesn't match");
        String qty = waitElementVisibility(By.xpath("//div[@class='col-xs-2']")).getText().trim();
        Assert.assertTrue(qty.equals("1"), "Wrong number of items ordered");
        String finalPrice = waitElementVisibility(By
                .xpath("//div[@class='col-xs-5 text-sm-right text-xs-left']")).getText().trim();
        Assert.assertTrue(DataConverter.parsePriceValue(finalPrice) == product.getPrice(),
                "Final product price is wrong");
    }

    public void goToProductPage() {
        CustomReporter.logAction("Go to product page");
        driver.navigate().to(productURL);
        waitForPageLoad();
    }

    public void checkInStockValue() {
        CustomReporter.logAction("Check if the 'In stock' value has been decreased");
        waitElementPresence(By.xpath("//a[@href='#product-details']")).click();
        waitForPageLoad();
        explicitWait().until(jQueryAJAXCallsAreCompleted());
        String inStockValue = extractNumber(waitElementToBeClickable(By
                .xpath("//div[@class='product-quantities']/span")).getText().trim());
        Assert.assertEquals(Integer.parseInt(inStockValue), product.getQty() - 1, "In stock value has not been decreased");
    }

    public String extractNumber(String value) {
        String result = "";
        for (int i = 0; i < value.length(); i++) {
            if (value.charAt(i) == ' ') break;
            result += value.charAt(i);
        }
        return result;
    }


    /**
     * Extracts product information from opened product details page.
     *
     * @return
     */

}
