package com.adl.et.telco.testautomation.configurations;

import com.adl.et.telco.testautomation.configurations.xmlmappers.ConfigServiceData;
import com.adl.et.telco.testautomation.global.GlobalVars;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.HashMap;
import java.util.List;

/**
 * This class is used to get the test data from xml file.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DataExtractor {
	
	public static final Logger logger = LoggerFactory.getLogger(DataExtractor.class);

    /**
     * This method is used to test method related test data.
     * @param methodName method Name
     * @param filePath filePath
     * @return @{@link }
     * @throws throw JAXBException
     */
    public static List<HashMap<String, String>> getTestData(String methodName, String filePath) throws JAXBException {
        File file = new File(filePath);
        JAXBContext jaxbContext = JAXBContext.newInstance(TestData.class);
        Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
        TestData testData = (TestData) jaxbUnmarshaller.unmarshal(file);
        return testData.getTestMethodData().get(methodName);
    }

    /**
     * This method is used to the service related configuration data.
     * @return @{@link ConfigServiceData}
     * @throws JAXBException @{@link JAXBException}
     */
    public static ConfigServiceData getConfigDataService() throws JAXBException {
        
        logger.debug("getConfigDataService started");
        
    	String basePathWithFileSeperators = GlobalVars.BASE_DIR + GlobalVars.FILE_SEPARATOR + "src" + GlobalVars.FILE_SEPARATOR +
        		"main"+ GlobalVars.FILE_SEPARATOR + "resources" + GlobalVars.FILE_SEPARATOR + "serviceConfigData.xml";
    	
    	logger.debug("basePathWithFileSeparators = {}", basePathWithFileSeperators);
    	
        File file = new File(basePathWithFileSeperators);

        logger.debug("file exists = {}",file.exists());
        
        JAXBContext jaxbContext = JAXBContext.newInstance(ConfigServiceData.class);
        
        Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
        
        logger.debug("getConfigDataService ended and returning started");
        
        return (ConfigServiceData) jaxbUnmarshaller.unmarshal(file);
    }

    /**
     * This method is used to test suite related common test data
     * @param filePath String values of filePath
     * @return @{@link CommonTestData}
     * @throws JAXBException @{@link JAXBException}
     */
    public static CommonTestData getTestcaseInitData(String filePath) throws JAXBException {
        File file = new File(filePath);
        JAXBContext jaxbContext = JAXBContext.newInstance(CommonTestData.class);
        Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
        return (CommonTestData) jaxbUnmarshaller.unmarshal(file);
    }
}
