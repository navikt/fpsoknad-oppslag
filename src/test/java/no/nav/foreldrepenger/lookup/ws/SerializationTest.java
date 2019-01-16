package no.nav.foreldrepenger.lookup.ws;

import static java.util.Collections.emptyList;
import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.neovisionaries.i18n.CountryCode;

import no.nav.foreldrepenger.lookup.ws.aktor.AktorId;
import no.nav.foreldrepenger.lookup.ws.arbeidsforhold.Arbeidsforhold;
import no.nav.foreldrepenger.lookup.ws.person.Bankkonto;
import no.nav.foreldrepenger.lookup.ws.person.Fødselsnummer;
import no.nav.foreldrepenger.lookup.ws.person.ID;
import no.nav.foreldrepenger.lookup.ws.person.Kjønn;
import no.nav.foreldrepenger.lookup.ws.person.Navn;
import no.nav.foreldrepenger.lookup.ws.person.Person;

@SpringJUnitConfig
@AutoConfigureJsonTesters
public class SerializationTest {

    @Autowired
    private ObjectMapper mapper;

    @Test
    public void testArbeidsforholdSerialization() throws IOException {
        test(arbeidsforhold());
    }

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

    private static ID id() {
        return new ID(aktoer(), fnr());
    }

    private static Navn name() {
        return new Navn("Jan-Olav", "Kjørås", "Eide");
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

    private static AktorId aktoer() {
        return new AktorId("11111111111111111");
    }

    private static Bankkonto bankkonto() {
        return new Bankkonto("1234567890", "Pæng r'us");
    }

    private static Arbeidsforhold arbeidsforhold() {
        LocalDate now = LocalDate.now();
        return new Arbeidsforhold("arbgiver", "typen", 100d,
                now.minusMonths(2), Optional.of(now));
    }

    private String write(Object obj) throws JsonProcessingException {
        return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(obj);
    }
}
