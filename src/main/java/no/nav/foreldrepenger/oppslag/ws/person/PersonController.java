package no.nav.foreldrepenger.oppslag.ws.person;

import java.util.Optional;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import no.nav.foreldrepenger.oppslag.util.TokenUtil;
import no.nav.foreldrepenger.oppslag.ws.aktor.AktørTjeneste;
import no.nav.security.token.support.core.api.ProtectedWithClaims;

@RestController
@ProtectedWithClaims(issuer = "selvbetjening", claimMap = { "acr=Level4" })
@RequestMapping(PersonController.PERSON)
public class PersonController {

    private static final Logger LOG = LoggerFactory.getLogger(PersonController.class);

    public static final String PERSON = "/person";
    private final AktørTjeneste aktorClient;
    private final PersonTjeneste personClient;
    private final TokenUtil tokenHandler;

    @Inject
    public PersonController(AktørTjeneste aktorClient, PersonTjeneste personClient,
            TokenUtil tokenHandler) {
        this.aktorClient = aktorClient;
        this.personClient = personClient;
        this.tokenHandler = tokenHandler;
    }

    @GetMapping
    public Person person() {
        LOG.trace("Slår opp person");
        var fnr = new Fødselsnummer(tokenHandler.autentisertBruker());
        return personClient.hentPersonInfo(new ID(aktorClient.aktorIdForFnr(fnr), fnr));
    }

    @GetMapping("/navn")
    public Navn person(Fødselsnummer fnr) {
        LOG.info("Slår opp navn");
        return personClient.navn(fnr);
    }

    @GetMapping("/kontonr")
    public Bankkonto kontonr() {
        LOG.info("Slår opp kontonummer");
        var knr = Optional.ofNullable(person())
                .map(Person::getBankkonto)
                .orElse(null);
        LOG.info("Slo opp kontonummer OK");
        return knr;
    }

    @GetMapping("/maalform")
    public String målform() {
        LOG.info("Slår opp målform");
        return Optional.ofNullable(person())
                .map(Person::getMålform)
                .orElse(null);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [aktorClient=" + aktorClient + ", personClient=" + personClient + "]";
    }
}
