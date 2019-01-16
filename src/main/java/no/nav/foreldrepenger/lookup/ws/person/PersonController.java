package no.nav.foreldrepenger.lookup.ws.person;

import javax.inject.Inject;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import no.nav.foreldrepenger.lookup.util.TokenUtil;
import no.nav.foreldrepenger.lookup.ws.aktor.AktorIdClient;
import no.nav.security.oidc.api.ProtectedWithClaims;

@RestController
@ProtectedWithClaims(issuer = "selvbetjening", claimMap = { "acr=Level4" })
@RequestMapping(PersonController.PERSON)
public class PersonController {

    public static final String PERSON = "/person";
    private final AktorIdClient aktorClient;
    private final PersonClient personClient;
    private final TokenUtil tokenHandler;

    @Inject
    public PersonController(AktorIdClient aktorClient, PersonClient personClient,
            TokenUtil tokenHandler) {
        this.aktorClient = aktorClient;
        this.personClient = personClient;
        this.tokenHandler = tokenHandler;
    }

    @GetMapping
    public Person person() {
        Fødselsnummer fnr = tokenHandler.autentisertBruker();
        return personClient.hentPersonInfo(new ID(aktorClient.aktorIdForFnr(fnr), fnr));
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [aktorClient=" + aktorClient + ", personClient=" + personClient + "]";
    }
}
