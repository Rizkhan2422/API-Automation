package com.adl.et.telco.testautomation.configurations.xmlmappers;

import com.adl.et.telco.testautomation.configurations.User;
import com.adl.et.telco.testautomation.configurations.xmladapters.MapAdapter;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElementWrapper;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class is used to unmarshal xml data to java object
 */
@XmlRootElement
public class ConfigData {

    @XmlElement(name = "properties")
    @XmlJavaTypeAdapter(MapAdapter.class)
    private Map<String, String> properties = new HashMap<>();

    @XmlElement(name = "environments")
    @XmlJavaTypeAdapter(MapAdapter.class)
    private Map<String, String> environments = new HashMap<>();

    public String getProperty(String propertyName) {
        return properties.get(propertyName);
    }

    public Map<String, String> getEnvironment() {
        return environments;
    }

    @XmlElementWrapper(name = "users")
    @XmlElement(name = "user")
    private List<User> users;

    public List<User> getUsers() {
        return users;
    }
}
