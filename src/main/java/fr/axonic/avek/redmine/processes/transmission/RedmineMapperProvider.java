package fr.axonic.avek.redmine.processes.transmission;

import com.fasterxml.jackson.databind.AnnotationIntrospector;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.introspect.JacksonAnnotationIntrospector;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.fasterxml.jackson.module.jaxb.JaxbAnnotationIntrospector;

public class RedmineMapperProvider {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    static {
        AnnotationIntrospector aiJaxb = new JaxbAnnotationIntrospector(TypeFactory.defaultInstance());
        AnnotationIntrospector aiJackson = new JacksonAnnotationIntrospector();

        MAPPER.setAnnotationIntrospector(AnnotationIntrospector.pair(aiJaxb, aiJackson));
    }

    public static ObjectMapper getMapper() {
        return MAPPER;
    }
}
