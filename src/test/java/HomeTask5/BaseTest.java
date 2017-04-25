package HomeTask5;

import HomeTask5.utils.DriverFactory;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.annotations.*;

/**
 * Base script functionality, can be used for all Selenium scripts.
 */
public abstract class BaseTest {
    protected WebDriver driver;
    protected GeneralActions actions;
    protected boolean isMobileTesting;

    /**
     * Prepares {@link WebDriver} instance with timeout and browser window configurations.
     * <p>
     * Driver type is based on passed parameters to the automation project,
     * creates {@link ChromeDriver} instance by default.
     */

    @BeforeClass
    @Parameters({"selenium.browserPJS", "selenium.grid"})
    public void setUp(@Optional("chrome") String browser, @Optional("") String gridUrl) {
        // TODO create WebDriver instance according to passed parameters
        //System.out.println(browser + " " + gridUrl);
        if (gridUrl.equals("")) {
            driver = DriverFactory.initDriver(browser);
            //System.out.println("Regular testing");
        } else {
            driver = DriverFactory.initDriver(browser, gridUrl);
            //System.out.println("Headless testing");
        }
        if (!isMobileTesting(browser))
            driver.manage().window().maximize();
        isMobileTesting = isMobileTesting(browser);
        actions = new GeneralActions(driver);
    }

    /**
     * Closes driver instance after test class execution.
     */
    @AfterClass
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    /**
     *
     * @return Whether required browser displays content in mobile mode.
     */
    private boolean isMobileTesting(String browser) {
        switch (browser) {
            case "android":
                return true;
            case "firefox":
            case "ie":
            case "internet explorer":
            case "chrome":
            case "phantomjs":
            default:
                return false;
        }
    }
}
