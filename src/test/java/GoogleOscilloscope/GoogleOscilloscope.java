package GoogleOscilloscope;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.*;
import org.openqa.selenium.Point;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Iterator;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.List;

import static org.junit.Assert.fail;

public class GoogleOscilloscope {
    private WebDriver driver;
    private String baseUrl;
    private WebElement explicitWaitElement, testElement;
    private StringBuffer verificationErrors = new StringBuffer();
    private String searchValue;
    private String googleSearchBtnG,
            googleSearchBtnK,
            searchInput,
            keyboard,
            shadingElement,
            searchResults,
            searchString,
            searchElement,
            nextButton,
            pageNumber;
    private int timeForWait;
    private boolean stopTest;

    private final String TESTSTART = "Начало теста   ";
    private final String TESTFINISH = "Конец теста    ";
    private final String ELEMENTNOTFOUND = "Искомый элемент не найден: ";
    private final String FOUND = "Совпадение найдено на странице: ";
    private final String NOTFOUND = "Совпадение не найдено";
    private final String BANNED = "Banned by Google :)";
    private final String IMAGESTORED = "Изображение успешно сохранено";
    private final String FILENAME = "screenshot.png";
    private final String PATHTOSTORE = "OUTPUT\\GoogleOscilloscope\\" + FILENAME;

    @Before
    public void setUp() throws Exception {
        driver = new FirefoxDriver();
        // не явное ожидание, по умолчанию
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        // явное ожидание
        timeForWait = 15;
        // остановка теста
        stopTest = false;
        // описание єлементов страниц
        searchValue = "осциллограф";
        baseUrl = "https://www.google.com.ua/?gws_rd=ssl";
        googleSearchBtnG = "btnG";
        googleSearchBtnK = "btnK";
        searchInput = "lst-ib";
        keyboard = "gs_ok0";
        shadingElement = "flyr";
        searchResults = "//div[@class =\"srg\"]/div[last()]";
        searchString = "srg";
        searchElement = "zapisnyh\\.narod\\.ru[\\s\\S]*$";
        nextButton = "pnnext";
        pageNumber = ".//*[@id='nav']/tbody/tr/td[@class='cur']";
    }

    @Test
    public void googleOscilloscopeTest() throws Exception {

        // получаем название теста и выводим информацию о старте
        System.out.println(TESTSTART + new Object() {
        }.getClass().getEnclosingMethod().getName());

        driver.get(baseUrl);

        // проверяем загрузилась ли страница
        testElement = explicitWait(driver, By.name(googleSearchBtnK), timeForWait);
        // если элемент не найден - корректно завершаем тест
        Assert.assertNotNull(testElement);

        // вводим слово для поиска
        driver.findElement(By.id(searchInput)).clear();
        driver.findElement(By.id(searchInput)).sendKeys(searchValue);

        // Проверка двух кнопок поиска гугла и нажатие на нужную
        if (isElementPresent(By.name(googleSearchBtnG))) {
            driver.findElement(By.name(googleSearchBtnG)).click();
        } else {
            driver.findElement(By.name(googleSearchBtnK)).click();
        }

        do {
            // ждем пока уйдет затеняющий эфект со страницы
            new WebDriverWait(driver, 5).until(ExpectedConditions.invisibilityOfElementLocated(By.id(shadingElement)));

            // проверяем гуглобан
            // под вопросом, пока не получилось проверить
            try {
                testElement = null;
                testElement = (new WebDriverWait(driver, 5))
                        .until(ExpectedConditions.visibilityOfElementLocated(By.id(keyboard)));
            } catch (Exception e) {
                System.out.println(BANNED);
            }
            // если элемент не найден - корректно завершаем тест
            Assert.assertNotNull(testElement);

            // проверяем подгрузились ли результаты поиска (по последнему элементу в поисковой выдаче)
            testElement = explicitWait(driver, By.xpath(searchResults), timeForWait);
            // если элемент не найден - корректно завершаем тест
            Assert.assertNotNull(testElement);

            //
            // Основное задание
            //

            Pattern site = Pattern.compile(searchElement);
            Matcher site2 = site.matcher(driver.findElement(By.className(searchString)).getText());
            if (site2.find()) {
                // если найдено совпадение, останавливаем тест
                stopTest = true;
                // выводим номер страницы
                System.out.println(FOUND + driver.findElement(By.xpath(pageNumber)).getText());
                //Получение скрина всей страницы
                File screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
                // подготовка скрина для выделения результата
                BufferedImage fullImg = ImageIO.read(screenshot);
                // выделяем результат
                fullImg = drawRectangle(fullImg);
                // подготовка к сохранению
                ImageIO.write(fullImg, "png", screenshot);
                // сохраняем в файл
                FileUtils.copyFile(screenshot, new File(PATHTOSTORE));
                System.out.println(IMAGESTORED);
            } else {
                try {
                    testElement = null;
                    testElement = (new WebDriverWait(driver, timeForWait))
                            .until(ExpectedConditions.elementToBeClickable(By.id(nextButton)));
                    // нажимаем на кнопку Дальше
                    driver.findElement(By.id(nextButton)).click();
                } catch (Exception e) {
                    // если элемент не найден сообщаем об этом
                    stopTest = true;
                    System.out.println(NOTFOUND);
                }
            }
        } while (!stopTest);
        // получаем название теста и выводим информацию о его окончании
        System.out.println(TESTFINISH + new Object() {
        }.getClass().getEnclosingMethod().getName());
    }

    @After
    public void tearDown() throws Exception {
        driver.quit();
        String verificationErrorString = verificationErrors.toString();
        if (!"".equals(verificationErrorString)) {
            fail(verificationErrorString);
        }
    }

    private boolean isElementPresent(By by) {
        try {
            driver.findElement(by);
            return true;
        } catch (NoSuchElementException e) {
            return false;
        }
    }

    private WebElement explicitWait(WebDriver driver, By by, int timeForWait) {
        // явным ожиданием проверяем наличие элемента с которым будем работать
        // проверка идет каждые 500мс но не больше установленного времени
        try {
            explicitWaitElement = null;
            explicitWaitElement = (new WebDriverWait(driver, timeForWait))
                    .until(ExpectedConditions.presenceOfElementLocated(by));
        } catch (Exception e) {
            // если элемент не найден сообщаем об этом
            System.out.println(ELEMENTNOTFOUND + by.toString());
        }
        return explicitWaitElement;
    }

    // отрисовка прямоугольника выделения
    private BufferedImage drawRectangle(BufferedImage bufferedImage){

        // получение нужного элемента в списке пезультатов
        WebElement elementForSelect = driver.findElement(By.xpath(".//*[@class='srg']/div" + findPositionOfElement() + "/div"));

        //Получение расположения элемента на странице
        Point point = elementForSelect.getLocation();
        //Получение высоты и ширины элемента
        int eleWidth = elementForSelect.getSize().getWidth();
        int eleHeight = elementForSelect.getSize().getHeight();
        // рисовалка
        Graphics2D g2d = bufferedImage.createGraphics();
        // подложка из исходного изображения
        g2d.drawImage(bufferedImage, 0, 0, null);
        // цвет кисти
        g2d.setColor(Color.red);
        // рисуем прямоугольник
        g2d.draw(new Rectangle2D.Double(point.getX()-5, point.getY()-5, eleWidth+10, eleHeight+10));
        // соеденяем подложку и рисунок
        g2d.dispose();

        return bufferedImage;
    }

    // нахождение порядкового номера нужного элемента в списке выдачи
    private String findPositionOfElement(){
        int i = 0;

        List<WebElement> foundSearchResults = driver.findElements(By.className("rc"));

        Pattern search = Pattern.compile(searchElement);

        Iterator <WebElement> iterator = foundSearchResults.iterator();
        int temp = 0;
        while (iterator.hasNext()){
            temp++;
            WebElement searchItem = iterator.next();
            Matcher result = search.matcher(searchItem.getText());
            if (result.find()) {
                i = temp;
            }
        }

        String result;

        if (i == 1){
            result = "";
        } else {
            result = "[" + Integer.toString(i) + "]";
        }

        return result;
    }

}
