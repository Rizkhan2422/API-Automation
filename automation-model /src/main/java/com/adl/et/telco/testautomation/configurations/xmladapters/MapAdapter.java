package com.adl.et.telco.testautomation.configurations.xmladapters;

import jakarta.xml.bind.annotation.XmlAnyElement;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlValue;
import jakarta.xml.bind.annotation.adapters.XmlAdapter;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This is a customize adapter class
 */
public class MapAdapter extends XmlAdapter<MapAdapter.AdaptedMap, Map<String, String>> {
    private DocumentBuilder documentBuilder;

    public static class AdaptedMap {
        @XmlAnyElement
        public List<Element> elements = new ArrayList<>();
    }

    public static class Entry {
        @XmlAttribute
        public String key;

        @XmlValue
        public String value;
    }

    @Override
    public Map<String, String> unmarshal(AdaptedMap adaptedMap) throws Exception {
        Map<String, String> map = new HashMap<>();
        for (Element element : adaptedMap.elements) {
            map.put(element.getAttribute("name"), element.getTextContent());
        }
        return map;
    }

    @Override
    public AdaptedMap marshal(Map<String, String> map) throws Exception {
        Document document = documentBuilder.newDocument();
        AdaptedMap adaptedMap = new AdaptedMap();
        for (Map.Entry<String, String> entry : map.entrySet()) {
            Element element = document.createElement(entry.getKey());
            element.setTextContent(entry.getValue());
            adaptedMap.elements.add(element);
        }
        return adaptedMap;
    }
}