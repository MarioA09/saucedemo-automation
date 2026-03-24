package pruebas;

import org.junit.jupiter.api.*;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

public class PruebaInicialTest {

    WebDriver driver;
    WebDriverWait wait;

    @BeforeEach
    public void setUp() {
        WebDriverManager.chromedriver().setup();
        driver = new ChromeDriver();
        driver.manage().window().maximize();
        wait = new WebDriverWait(driver, Duration.ofSeconds(5));
        driver.get("https://www.saucedemo.com/");
    }

    // 🔹 Método reutilizable
    public void login(String usuario, String contraseña) {
        driver.findElement(By.id("user-name")).sendKeys(usuario);
        driver.findElement(By.id("password")).sendKeys(contraseña);
        driver.findElement(By.id("login-button")).click();

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("inventory_list")));
    }

    // 🔹 CP01 - Login válido
    @Test
    public void CP01_LoginValido_E2E() {
        login("standard_user", "secret_sauce");

        assertTrue(driver.getCurrentUrl().contains("inventory"),
                "No ingresó al inventario");
    }

    // 🔹 CP02 - Login inválido
    @Test
    public void CP02_LoginInvalido_E2E() {
        driver.findElement(By.id("user-name")).sendKeys("usuario_malo");
        driver.findElement(By.id("password")).sendKeys("123");
        driver.findElement(By.id("login-button")).click();

        WebElement error = wait.until(
                ExpectedConditions.visibilityOfElementLocated(By.cssSelector("h3[data-test='error']"))
        );

        assertTrue(error.getText().contains("Username and password do not match"),
                "No se mostró el error");
    }

    // 🔹 CP03 - Compra completa (EL MÁS IMPORTANTE)
    @Test
    public void CP03_CompraCompleta_E2E() {
        login("standard_user", "secret_sauce");

        driver.findElement(By.id("add-to-cart-sauce-labs-backpack")).click();
        driver.findElement(By.className("shopping_cart_link")).click();

        assertTrue(driver.getPageSource().contains("Sauce Labs Backpack"));

        driver.findElement(By.id("checkout")).click();

        driver.findElement(By.id("first-name")).sendKeys("Mario");
        driver.findElement(By.id("last-name")).sendKeys("Cordero");
        driver.findElement(By.id("postal-code")).sendKeys("12345");

        driver.findElement(By.id("continue")).click();
        driver.findElement(By.id("finish")).click();

        WebElement mensaje = wait.until(
                ExpectedConditions.visibilityOfElementLocated(By.className("complete-header"))
        );

        assertTrue(mensaje.getText().contains("Thank you"),
                "Compra no completada");
    }

    // 🔹 CP04 - Agregar múltiples productos
    @Test
    public void CP04_AgregarProductos_E2E() {
        login("standard_user", "secret_sauce");

        driver.findElement(By.id("add-to-cart-sauce-labs-backpack")).click();
        driver.findElement(By.id("add-to-cart-sauce-labs-bike-light")).click();

        driver.findElement(By.className("shopping_cart_link")).click();

        assertTrue(driver.getPageSource().contains("Sauce Labs Backpack"));
        assertTrue(driver.getPageSource().contains("Sauce Labs Bike Light"));
    }

    // 🔹 CP05 - Eliminar producto
    @Test
    public void CP05_EliminarProducto_E2E() {
        login("standard_user", "secret_sauce");

        driver.findElement(By.id("add-to-cart-sauce-labs-backpack")).click();
        driver.findElement(By.className("shopping_cart_link")).click();

        driver.findElement(By.id("remove-sauce-labs-backpack")).click();

        assertFalse(driver.getPageSource().contains("Sauce Labs Backpack"),
                "No se eliminó el producto");
    }

    // 🔹 CP06 - Navegación About
    @Test
    public void CP06_NavegacionAbout_E2E() {
        login("standard_user", "secret_sauce");

        driver.findElement(By.id("react-burger-menu-btn")).click();

        WebElement about = wait.until(
                ExpectedConditions.elementToBeClickable(By.id("about_sidebar_link"))
        );

        about.click();

        assertTrue(driver.getCurrentUrl().contains("saucelabs.com"),
                "No redirigió correctamente");
    }

    @AfterEach
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}