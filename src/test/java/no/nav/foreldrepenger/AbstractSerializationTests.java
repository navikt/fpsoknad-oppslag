package no.nav.foreldrepenger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;

@AutoConfigureJsonTesters

public abstract class AbstractSerializationTests {

    private static final Logger LOG = LoggerFactory.getLogger(AbstractSerializationTests.class);

    private static final ObjectMapper mapper = mapper();

    private static ObjectMapper mapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.registerModule(new Jdk8Module());
        mapper.registerModule(new ParameterNamesModule(JsonCreator.Mode.PROPERTIES));
        return mapper;
    }

    private static void test(Object object, boolean print) {
        test(object, print, mapper);
    }

    void test(Object object) {
        test(object, true);

    }

    static void test(Object expected, boolean log, ObjectMapper mapper) {
        try {
            String serialized = serialize(expected, log, mapper);
            Object deserialized = mapper.readValue(serialized, expected.getClass());
            if (log) {
                LOG.info("{}", expected);
                LOG.info("{}", serialized);
                LOG.info("{}", deserialized);
            }
            assertEquals(expected, deserialized);
        } catch (IOException e) {
            LOG.error("{}", e);
            fail(expected.getClass().getSimpleName() + " failed");
        }
    }

    public static String serialize(Object obj, boolean print, ObjectMapper mapper) throws JsonProcessingException {
        String serialized = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(obj);
        return print ? printSerialized(serialized) : serialized;
    }

    static String printSerialized(String serialized) {
        return serialized;
    }

}
