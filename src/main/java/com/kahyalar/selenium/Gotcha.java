package com.kahyalar.selenium;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.io.IOException;

/**
 * Created by kahyalar on 20/03/2017.
 */
public class Gotcha {
    static private WebDriver driver;
    static private WebElement element;
    static boolean isItMac;


    public Gotcha(WebDriver driver){
        Gotcha.driver = driver;
    }
    public static void crackCaptcha(WebElement captcha) throws IOException {
        Gotcha.createScreenshotManagerInstance().getScreenshotOfCaptcha(element, isItMac);

    }

    private static ScreenshotManager createScreenshotManagerInstance(){
        return new ScreenshotManager(driver);
    }
}
