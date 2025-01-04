package com.adl.et.telco.testautomation.configurations.xmlmappers;

import com.adl.et.telco.testautomation.configurations.xmladapters.MapAdapter;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import java.util.HashMap;
import java.util.Map;

/**
 * This class is used to unmarshal xml data to java object
 */
@XmlRootElement
public class ConfigServiceData {
    
    @XmlElement(name = "properties")
    @XmlJavaTypeAdapter(MapAdapter.class)
    private Map<String, String> properties = new HashMap<>();

    @XmlElement(name = "dbConfig")
    @XmlJavaTypeAdapter(MapAdapter.class)
    private Map<String, String> dbConfig = new HashMap<>();

    public String getProperty(String propertyName) {
        return properties.get(propertyName);
    }
    
    public Map<String, String> getProperties(){
    	return properties;
    }

    public String getDbProperty(String propertyName) {
        return dbConfig.get(propertyName);
    }

    public Map<String, String> getDbConfig(){
        return dbConfig;
    }

}
