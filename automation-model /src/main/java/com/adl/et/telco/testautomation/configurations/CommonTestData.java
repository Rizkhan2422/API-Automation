package com.adl.et.telco.testautomation.configurations;

import com.adl.et.telco.testautomation.configurations.xmladapters.MapAdapterTestData;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import java.util.HashMap;
import java.util.Map;

@XmlRootElement(name = "testData")
public class CommonTestData {

    public Map<String, Object> getMetaData() {
        return metaData;
    }

    public Object getMetaDataByName(String property){
        return metaData.getOrDefault(property,"Not Set in XML");
    }

    @XmlElement(name = "metaData")
    @XmlJavaTypeAdapter(MapAdapterTestData.class)
    private HashMap<String, Object> metaData;


}
