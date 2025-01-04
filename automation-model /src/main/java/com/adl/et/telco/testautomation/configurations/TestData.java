package com.adl.et.telco.testautomation.configurations;


import com.adl.et.telco.testautomation.configurations.xmladapters.MapAdapterTestData;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import java.util.HashMap;
import java.util.List;

@XmlRootElement
public class TestData {

    public HashMap<String, List<HashMap<String,String>>> getTestMethodData() {
        return testMethodData;
    }

    @XmlElement(name = "suite")
    @XmlJavaTypeAdapter(MapAdapterTestData.class)
    private HashMap<String, List<HashMap<String,String>>> testMethodData;

}
