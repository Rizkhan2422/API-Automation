package com.adl.et.telco.testautomation.utils;

import io.appium.java_client.AppiumDriver;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.commons.configuration2.PropertiesConfiguration;
import org.apache.commons.configuration2.builder.fluent.Configurations;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class FileHelper {
	
	public static final Logger logger = LoggerFactory.getLogger(FileHelper.class);
	
    public static final String COMMA_SEPARATOR = ",";
    public static final String PATH_DELIMITER = "/";

    public static List<String> stringToList(String text, String separator){
        return Arrays.asList(text.split(separator));
    }

    public static void captureScreenShots(AppiumDriver<WebElement> driver) throws IOException {
        String folderName;
        DateFormat df;
        folderName="screenshot";
        File f=((TakesScreenshot)driver).getScreenshotAs(OutputType.FILE);
        //Date format for screenshot file name
        df=new SimpleDateFormat("dd-MMM-yyyy__hh_mm_ssaa");
        //create dir with given folder name
        new File(folderName).mkdir();
        //Setting file name
        String fileName=df.format(new Date())+".png";
        //copy screenshot file into screenshot folder.
        FileUtils.copyFile(f, new File(folderName + PATH_DELIMITER + fileName));
    }

    public static PropertiesConfiguration readPropertyFile(String filePath) throws org.apache.commons.configuration2.ex.ConfigurationException {
        Configurations configurations = new Configurations();
        return configurations.properties(getFileFromPath(filePath));
    }

    public static File getFileFromPath(String path){
        return new File(path);
    }

    public static String getStringFromFile(String filePath) {
    	
    	logger.debug("filePath = {} ",filePath);
    	
        String content = "";
        try
        {
            content = new String ( Files.readAllBytes( Paths.get(filePath) ) );
            logger.debug("content = {} ",content);
        }
        catch (IOException e)
        {
           logger.error("Error while reading file ",e);
        }
        return content;
    }
}
