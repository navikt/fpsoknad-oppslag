package no.nav.foreldrepenger.oppslag.ws;

import java.util.List;

import no.nav.foreldrepenger.oppslag.ws.arbeidsforhold.Arbeidsforhold;
import no.nav.foreldrepenger.oppslag.ws.person.Person;

public class Søkerinfo {

    private final Person person;
    private final List<Arbeidsforhold> arbeidsforhold;

    public Søkerinfo(Person person, List<Arbeidsforhold> arbeidsforhold) {
        this.person = person;
        this.arbeidsforhold = arbeidsforhold;
    }

    public Person getPerson() {
        return person;
    }

    public List<Arbeidsforhold> getArbeidsforhold() {
        return arbeidsforhold;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [person=" + person + ", arbeidsforhold=" + arbeidsforhold + "]";
    }
}
