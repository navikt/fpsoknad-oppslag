package no.nav.foreldrepenger.oppslag.ws;

import static java.util.Collections.emptyList;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.time.LocalDate;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.neovisionaries.i18n.CountryCode;

import no.nav.foreldrepenger.oppslag.ws.aktor.AktørId;
import no.nav.foreldrepenger.oppslag.ws.person.Bankkonto;
import no.nav.foreldrepenger.oppslag.ws.person.Fødselsnummer;
import no.nav.foreldrepenger.oppslag.ws.person.Kjønn;
import no.nav.foreldrepenger.oppslag.ws.person.Navn;
import no.nav.foreldrepenger.oppslag.ws.person.Person;

@SpringJUnitConfig
@AutoConfigureJsonTesters
public class SerializationTest {
    @Autowired
    private ObjectMapper mapper;

    @Test
    public void testBankkontoSerialization() throws IOException {
        test(bankkonto());
    }

    @Test
    public void testKjonnSerialization() throws IOException {
        test(Kjønn.K);
    }

    @Test
    public void testNameSerialization() throws IOException {
        test(name());
    }

    // TODO removed for now @Test
    public void testPersonSerialization() throws IOException {
        test(person());
    }

    @Test
    public void testFnrSerialization() throws IOException {
        test(fnr());
    }

    @Test
    public void testAktorIdSerialization() throws IOException {
        test(aktoer());
    }

    @Test
    public void testIDPairSerialization() throws IOException {
        test(id());
    }

    private void test(Object object) throws IOException {
        String serialized = write(object);
        Object deserialized = mapper.readValue(serialized, object.getClass());
        assertEquals(object, deserialized);
    }

    private static Fødselsnummer id() {
        return fnr();
    }

    private static Navn name() {
        return new Navn("Jan-Olav", "Kjørås", "Eide", Kjønn.M);
    }

    private static Person person() {
        return new Person(id(), CountryCode.NO, Kjønn.M, name(), "nynorsk",
                bankkonto(), birthDate(), emptyList());
    }

    private static LocalDate birthDate() {
        return LocalDate.now().minusMonths(2);
    }

    private static Fødselsnummer fnr() {
        return new Fødselsnummer("03016536325");
    }

    private static AktørId aktoer() {
        return new AktørId("11111111111111111");
    }

    private static Bankkonto bankkonto() {
        return new Bankkonto("1234567890", "Pæng r'us");
    }

    private String write(Object obj) throws JsonProcessingException {
        return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(obj);
    }
}
