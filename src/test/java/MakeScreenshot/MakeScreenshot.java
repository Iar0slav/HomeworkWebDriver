package MakeScreenshot;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.*;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.fail;

public class MakeScreenshot {
    private WebDriver driver;
    private String baseUrl;
    private WebElement element;
    private StringBuffer verificationErrors = new StringBuffer();
    private String searchElementByXpath;
    private String searchElementByXpathOk;
    private String fileName;
    private String pathToStore;
    private String imageStored;
    private int timeForWait;

    private final String TESTSTART          =   "Начало теста   ";
    private final String TESTFINISH         =   "Конец теста    ";
    private final String ELEMENTNOTFOUND    =   "Искомый элемент не найден: ";

    @Before
    public void setUp() throws Exception {
        driver = new FirefoxDriver();
        // не явное ожидание, по умолчанию
        driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
        // явное ожидание
        timeForWait = 15;
        baseUrl = "http://en.wikipedia.org/";
        searchElementByXpath = ".//*[@id='mp-itn-img']/div/a/im";
        searchElementByXpathOk = ".//*[@id='mp-itn-img']/div/a/img";
        fileName = "screenshot.png";
        pathToStore = "OUTPUT\\MakeScreenshot\\" + fileName;
        imageStored = "Изображение успешно сохранено";
    }

    @Test
    public void screenshotInTheNewsPicture() throws Exception {

        // получаем название теста и выводим информацию о старте
        System.out.println(TESTSTART + new Object(){}.getClass().getEnclosingMethod().getName());

        driver.get(baseUrl);

        // явным ожиданием проверяем наличие элемента с которым будем работать
        // проверка идет каждые 500мс но не больше установленного времени
        try {
            element = (new WebDriverWait(driver, timeForWait))
                    .until(ExpectedConditions.presenceOfElementLocated(By.xpath(searchElementByXpath)));
        } catch (Exception e){
            // если элемент не найден сообщаем об этом
            System.out.println(ELEMENTNOTFOUND + searchElementByXpath);
        }

        // если элемент не найден коректно завершаем тест
        Assert.assertNotNull(element);

        //Если элемент найден выполняем задание по тесту
        //Получение скрина всей страницы
        File screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
        BufferedImage fullImg = ImageIO.read(screenshot);

        //Получение расположения элемента на странице
        Point point = element.getLocation();

        //Получение высоты и ширины элемента
        int eleWidth = element.getSize().getWidth();
        int eleHeight = element.getSize().getHeight();

        //Обрезка скрина всей страницы до размера элемента
        BufferedImage eleScreenshot = fullImg.getSubimage(point.getX(), point.getY(), eleWidth, eleHeight);
        ImageIO.write(eleScreenshot, "png", screenshot);

        //Сохранение скрина элемента
        FileUtils.copyFile(screenshot, new File(pathToStore));
        System.out.println(imageStored);

        // получаем название теста и выводим информацию о его окончании
        System.out.println(TESTFINISH + new Object(){}.getClass().getEnclosingMethod().getName());
    }

    @Test
    public void screenshotInTheNewsPictureOk() throws Exception {

        // получаем название теста и выводим информацию о старте
        System.out.println(TESTSTART + new Object(){
        }.getClass().getEnclosingMethod().getName());

        driver.get(baseUrl);

        // явным ожиданием проверяем наличие элемента с которым будем работать
        // проверка идет каждые 500мс но не больше установленного времени
        try {
            element = (new WebDriverWait(driver, timeForWait))
                    .until(ExpectedConditions.presenceOfElementLocated(By.xpath(searchElementByXpathOk)));
        } catch (Exception e){
            // если элемент не найден сообщаем об этом
            System.out.println(ELEMENTNOTFOUND + searchElementByXpathOk);
        }

        // если элемент не найден коректно завершаем тест
        Assert.assertNotNull(element);

        //Если элемент найден выполняем задание по тесту
        //Получение скрина всей страницы
        File screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
        BufferedImage fullImg = ImageIO.read(screenshot);

        //Получение расположения элемента на странице
        Point point = element.getLocation();

        //Получение высоты и ширины элемента
        int eleWidth = element.getSize().getWidth();
        int eleHeight = element.getSize().getHeight();

        //Обрезка скрина всей страницы до размера элемента
        BufferedImage eleScreenshot = fullImg.getSubimage(point.getX(), point.getY(), eleWidth, eleHeight);
        ImageIO.write(eleScreenshot, "png", screenshot);

        //Сохранение скрина элемента
        FileUtils.copyFile(screenshot, new File(pathToStore));
        System.out.println(imageStored);

        // получаем название теста и выводим информацию о его окончании
        System.out.println(TESTFINISH + new Object(){}.getClass().getEnclosingMethod().getName());
    }

    @After
    public void tearDown() throws Exception {
        driver.quit();
        String verificationErrorString = verificationErrors.toString();
        if (!"".equals(verificationErrorString)) {
            fail(verificationErrorString);
        }
    }
}