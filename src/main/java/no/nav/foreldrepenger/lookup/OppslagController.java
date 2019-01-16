package no.nav.foreldrepenger.lookup;

import static org.slf4j.LoggerFactory.getLogger;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import no.nav.foreldrepenger.lookup.util.TokenUtil;
import no.nav.foreldrepenger.lookup.ws.Søkerinfo;
import no.nav.foreldrepenger.lookup.ws.aktor.AktorId;
import no.nav.foreldrepenger.lookup.ws.aktor.AktorIdClient;
import no.nav.foreldrepenger.lookup.ws.arbeidsforhold.Arbeidsforhold;
import no.nav.foreldrepenger.lookup.ws.arbeidsforhold.ArbeidsforholdClient;
import no.nav.foreldrepenger.lookup.ws.person.Fødselsnummer;
import no.nav.foreldrepenger.lookup.ws.person.ID;
import no.nav.foreldrepenger.lookup.ws.person.Person;
import no.nav.foreldrepenger.lookup.ws.person.PersonClient;
import no.nav.security.oidc.api.Unprotected;

@RestController
@no.nav.security.oidc.api.ProtectedWithClaims(issuer = "selvbetjening", claimMap = { "acr=Level4" })
@RequestMapping(OppslagController.OPPSLAG)
public class OppslagController {

    public static final String OPPSLAG = "/oppslag";

    private static final Logger LOG = getLogger(OppslagController.class);

    private final AktorIdClient aktorClient;

    private final PersonClient personClient;

    private final ArbeidsforholdClient arbeidsforholdClient;

    private final TokenUtil tokenHandler;

    @Inject
    public OppslagController(AktorIdClient aktorClient, PersonClient personClient,
            ArbeidsforholdClient arbeidsforholdClient,
            TokenUtil tokenHandler) {
        this.aktorClient = aktorClient;
        this.personClient = personClient;
        this.arbeidsforholdClient = arbeidsforholdClient;
        this.tokenHandler = tokenHandler;
    }

    @Unprotected
    @GetMapping(value = "/ping", produces = APPLICATION_JSON_VALUE)
    public String ping(
            @RequestParam(name = "register", defaultValue = "all", required = false) PingableRegisters register) {
        LOG.info("Vil pinge register {}", register);
        switch (register) {
        case aareg:
            arbeidsforholdClient.ping();
            break;
        case aktør:
            aktorClient.ping();
            break;
        case tps:
            personClient.ping();
            break;
        case all:
            aktorClient.ping();
            personClient.ping();
            arbeidsforholdClient.ping();
            break;
        }
        return registerNavn(register) + " er i toppform";
    }

    @GetMapping
    public Søkerinfo essensiellSøkerinfo() {
        Fødselsnummer fnr = tokenHandler.autentisertBruker();
        AktorId aktorId = aktorClient.aktorIdForFnr(fnr);
        Person person = personClient.hentPersonInfo(new ID(aktorId, fnr));
        List<Arbeidsforhold> arbeidsforhold = arbeidsforholdClient.aktiveArbeidsforhold(fnr);
        return new Søkerinfo(person, arbeidsforhold);
    }

    @GetMapping(value = "/aktor")
    public AktorId getAktørId() {
        return getAktørIdForFNR(tokenHandler.autentisertBruker());
    }

    @GetMapping(value = "/aktorfnr")
    public AktorId getAktørIdForFNR(@RequestParam(name = "fnr") Fødselsnummer fnr) {
        return aktorClient.aktorIdForFnr(fnr);
    }

    @GetMapping(value = "/fnr")
    public Fødselsnummer getFNRforAktørIdR(@RequestParam(name = "aktorId") AktorId aktorId) {
        return aktorClient.fnrForAktørId(aktorId);
    }

    private static String registerNavn(PingableRegisters register) {
        return register.equals(PingableRegisters.all)
                ? Arrays.stream(PingableRegisters.values())
                        .map(PingableRegisters::name)
                        .filter(s -> s != "all")
                        .collect(Collectors.joining(","))
                : register.name();
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [aktorClient=" + aktorClient + ", personClient=" + personClient
                + ", aaregClient=" + arbeidsforholdClient + "]";
    }

}
