package com.adl.et.telco.testautomation.base;

import com.adl.et.telco.testautomation.configurations.CommonTestData;
import com.adl.et.telco.testautomation.configurations.DataExtractor;
import com.adl.et.telco.testautomation.configurations.xmlmappers.ConfigServiceData;
import com.adl.et.telco.testautomation.utils.GeneralHelpers;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.RestAssured;
import io.restassured.config.ObjectMapperConfig;
import io.restassured.config.RestAssuredConfig;
import io.restassured.path.json.config.JsonPathConfig;
import jakarta.xml.bind.JAXBException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.ITestContext;
import org.testng.ITestNGMethod;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.DataProvider;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;

import static io.restassured.config.JsonConfig.jsonConfig;
public class ConfigService {

    protected Properties properties;
    protected static final ConfigServiceData configData;

    static {
        try {
            configData = DataExtractor.getConfigDataService();
        } catch (JAXBException e) {
            throw new RuntimeException(e);
        }
    }

    protected Map<String, Object> preferences;
    private Logger logger = LoggerFactory.getLogger(ConfigService.class);


    @BeforeSuite(alwaysRun = true)
    public void beforeSuiteMethod(ITestContext iTestContext) {
        try {
            System.setProperty("org.uncommons.reportng.escape-output", "false");
            RestAssured.config = RestAssuredConfig.config()
                    .objectMapperConfig(new ObjectMapperConfig().jackson2ObjectMapperFactory(
                    (type, s) -> new ObjectMapper().setDefaultPropertyInclusion(JsonInclude.Include.NON_NULL)
                            .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                            .findAndRegisterModules()))
                    .jsonConfig(jsonConfig().numberReturnType(JsonPathConfig.NumberReturnType.BIG_DECIMAL));
            setAllureEnvironment();
        } catch (Exception e) {
            logger.error("Setup test configuration fail --> = {}" ,e.getMessage());
        }
    }


   public void setAllureEnvironment() {
        // to be implemented this method
   }

    @DataProvider(name = "getDefaultUser")
    public Object[][] getDefaultUser(ITestNGMethod iTestNGMethod) throws JAXBException {
    	
    	logger.debug("getDefaultUser started");

    	String fileName = Objects.nonNull(configData.getProperty("testFile"))?configData.getProperty("testFileName"):iTestNGMethod.getRealClass().getSimpleName();
        String filePath = configData.getProperty("testDataFilePath")+fileName+".xml";
        
        logger.debug("full file path = {} ",filePath);
        
        String methodName = iTestNGMethod.getMethodName();
        List<HashMap<String,String>> testData = new ArrayList<>();
        int testDataSize = 1;
        if(GeneralHelpers.isFileExists(filePath)) {
        	List<HashMap<String,String>> tempData = DataExtractor.getTestData(methodName, filePath);
            if(tempData != null) {
            	tempData.forEach(a -> a.putAll(configData.getProperties()));
            	testData = tempData;
            }
            else {
            	testData.add((HashMap<String, String>) configData.getProperties());
            }
        	testDataSize = (testData.isEmpty())?1:testData.size();
        }
        else{
        	testData.add((HashMap<String, String>) configData.getProperties());
        }
        Object[][] dataRow = new Object[testDataSize][1];
        for(int i=0; i< dataRow.length; i++){

            dataRow[i][0] =  (testData.isEmpty())?null:testData.get(i%testDataSize);
        }
        return dataRow;
    }

    public CommonTestData getInitData(String fileName) {
        CommonTestData tempData;
        try {
            String filePath = configData.getProperty("testDataFilePath")+fileName+".xml";
            tempData = DataExtractor.getTestcaseInitData(filePath);
        }catch (JAXBException ex){
            tempData = new CommonTestData();
        }
        return tempData;
    }
}