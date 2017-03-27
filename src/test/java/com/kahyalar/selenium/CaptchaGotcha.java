package com.kahyalar.selenium;
import com.google.cloud.vision.spi.v1.ImageAnnotatorClient;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;

/**
 * Created by kahyalar on 16/03/2017.
 */
public class CaptchaGotcha {
    WebDriver driver;

    @Before
    public void startUp(){
        driver = new ChromeDriver();
        driver.get("http://predator.bahcesehir.edu.tr");
    }

    @Test
    public void crackCaptcha() throws IOException {
        WebElement captcha = driver.findElement(GotchaConstants.OISCaptcha);
        Kahium.createScreenshotManager(driver).getScreenshotOfElement(null, null, captcha, true);
        Detect.detectText(GotchaConstants.DEFAULT_FILE_PATH + GotchaConstants.DEAFULT_FILE_NAME,
                new PrintStream(new File(GotchaConstants.DEFAULT_FILE_PATH + "cracked.txt")));
    }

    @After
    public void tearDown(){
        driver.quit();
    }

}
