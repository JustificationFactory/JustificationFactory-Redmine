package fr.axonic.jf.redmine.masterfile;

import com.fasterxml.jackson.databind.AnnotationIntrospector;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.introspect.JacksonAnnotationIntrospector;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.jaxb.JaxbAnnotationIntrospector;

import javax.ws.rs.ext.ContextResolver;

public class RedmineMapperProvider implements ContextResolver<ObjectMapper> {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    static {
        AnnotationIntrospector aiJaxb = new JaxbAnnotationIntrospector(TypeFactory.defaultInstance());
        AnnotationIntrospector aiJackson = new JacksonAnnotationIntrospector();

        MAPPER.setAnnotationIntrospector(AnnotationIntrospector.pair(aiJaxb, aiJackson));

        MAPPER.registerModule(new JavaTimeModule());
    }

    @Override
    public ObjectMapper getContext(Class<?> aClass) {
        return MAPPER;
    }
}
