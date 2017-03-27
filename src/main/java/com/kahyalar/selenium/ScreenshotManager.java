package com.kahyalar.selenium;
import org.openqa.selenium.*;
import org.openqa.selenium.Point;


import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import static com.kahyalar.selenium.GotchaConstants.DEAFULT_FILE_NAME;
import static com.kahyalar.selenium.GotchaConstants.DEFAULT_FILE_PATH;
import static javax.imageio.ImageIO.read;
import static javax.imageio.ImageIO.write;
import static org.apache.commons.io.FileUtils.*;

/**
 * Created by kahyalar on 16/03/2017.
 */
public class ScreenshotManager {
    private WebDriver driver;
    private WebElement element;
    private File screenshot;
    private File screenshotLocation = new File(DEFAULT_FILE_PATH);
    private BufferedImage fullSizeImage;

    public ScreenshotManager(WebDriver driver){
        this.driver = driver;
    }

    private void getScreenshot() throws IOException{
        screenshot = ((TakesScreenshot)driver).getScreenshotAs(OutputType.FILE);
        fullSizeImage = read(screenshot);
        write(fullSizeImage, "png", screenshotLocation);
    }

    private void processElement(WebElement element, boolean MacOS) throws IOException{
        getScreenshot();
        Point point;
        int eleWidth, eleHeight;
        this.element = element;
        if(!MacOS){
            point = new Point(element.getLocation().getX(), element.getLocation().getY());
            eleWidth = element.getSize().getWidth();
            eleHeight = element.getSize().getHeight();
        }
        else {
            point = new Point((element.getLocation().getX()) * 2, (element.getLocation().getY()) * 2);
            eleWidth = (element.getSize().getWidth()) * 2;
            eleHeight = (element.getSize().getHeight()) * 2;
        }
        BufferedImage elementScreenshot= fullSizeImage.getSubimage(point.getX(), point.getY(), eleWidth, eleHeight);
        write(elementScreenshot, "png", screenshot);
    }

    public void getScreenshotOfCaptcha(WebElement element, boolean isItMacOS) throws IOException {
        processElement(element, isItMacOS);
        copyFile(screenshot, screenshotLocation);
    }
}
