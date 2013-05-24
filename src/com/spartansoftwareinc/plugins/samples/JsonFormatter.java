package com.spartansoftwareinc.plugins.samples;

import java.io.IOException;
import java.io.StringWriter;

import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.map.ObjectMapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JsonFormatter {
    private static Logger LOG = LoggerFactory.getLogger(JsonFormatter.class);
    private static ObjectMapper mapper = new ObjectMapper();
    private static JsonFactory jsonFactory = new JsonFactory();

    public static String toJson(Object pojo, boolean prettyPrint) {
        StringWriter writer = new StringWriter();
        try {
            JsonGenerator j = jsonFactory.createJsonGenerator(writer);
            if (prettyPrint) {
                j.useDefaultPrettyPrinter();
            }
            mapper.writeValue(j, pojo);
        } catch (IOException e) {
            LOG.error("Failed to serialize to JSON", e);
        }
        return writer.toString();
    }

    public static String quote(String s) {
        return "\"" + s + "\"";
    }

    public static String indent(String s) {
        return s.replaceAll("\n", "\n  ");
    }
}
