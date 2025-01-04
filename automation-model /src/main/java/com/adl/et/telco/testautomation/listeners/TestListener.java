package com.adl.et.telco.testautomation.listeners;

import io.qameta.allure.Allure;
import io.qameta.allure.AllureLifecycle;
import io.qameta.allure.Attachment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Objects;

public class TestListener implements ITestListener {
	
    public static final Logger logger = LoggerFactory.getLogger(TestListener.class);

    private static final String LOG_CONSTANT = "=================================== {} {} ===================================";

    public void onTestStart(ITestResult iTestResult) {
        // To be implemented
        logger.info(LOG_CONSTANT,"Start Test",iTestResult.getMethod().getMethodName());
    }

    public void onTestSuccess(ITestResult iTestResult) {
        logger.info(LOG_CONSTANT,"Test Success", iTestResult.getMethod().getMethodName());
        setAllureReportFields(Allure.getLifecycle(),iTestResult);
    }

    public void onTestFailure(ITestResult iTestResult) {
        logger.info(LOG_CONSTANT,"Test Failed",iTestResult.getMethod().getMethodName());
        setAllureReportFields(Allure.getLifecycle(),iTestResult);
    }

    public void onTestSkipped(ITestResult iTestResult) {
        setAllureReportFields(Allure.getLifecycle(),iTestResult);
    }

    public void onTestFailedButWithinSuccessPercentage(ITestResult iTestResult) {
        // To be implemented
    }

    public void onStart(ITestContext iTestContext) {
    	logger.info("===============================================================================================");
    }

    public void onFinish(ITestContext iTestContext) {
    	logger.info("===============================================================================================");
    }

    @Attachment(value = "{0}",type = "text/plain")
    private String saveTextInReport(String msg){
        return msg;
    }

    private void setAllureReportFields(AllureLifecycle lifecycle, ITestResult iTestResult){
    	
    	logger.debug("setAllureReportFields started");
    	
    	if(iTestResult.getParameters().length > 0) {
    		
    		logger.debug("processing report");
    		
    		HashMap<String,String> params = (HashMap<String, String>) iTestResult.getParameters()[iTestResult.getParameters().length-1];
            if(Objects.nonNull(params.get("epic"))) Allure.epic(params.get("epic"));
            if(Objects.nonNull(params.get("story"))) Allure.story(params.get("story"));
            if(Objects.nonNull(iTestResult.getThrowable())) saveTextInReport(iTestResult.getThrowable().getMessage());
            
            lifecycle.updateTestCase(testResult ->{
                testResult.setParameters(new LinkedList<>());
                if(Objects.nonNull(params.get("testName"))) testResult.setName(params.get("testName"));
                if(Objects.nonNull(params.get("description"))) testResult.setDescription(params.get("description"));
            });
    	}
    	logger.debug("setAllureReportFields ended");
    }
}
