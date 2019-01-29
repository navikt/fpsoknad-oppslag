package no.nav.foreldrepenger.lookup.rest.sak;

import java.util.List;

import javax.inject.Inject;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import no.nav.foreldrepenger.lookup.util.TokenUtil;
import no.nav.foreldrepenger.lookup.ws.aktor.AktorIdClient;
import no.nav.foreldrepenger.lookup.ws.person.Fødselsnummer;
import no.nav.security.oidc.api.ProtectedWithClaims;

@RestController
@ProtectedWithClaims(issuer = "selvbetjening", claimMap = { "acr=Level4" })
public class SakController {

    public static final String SAK = "/sak";
    private final SakClient sakClient;
    private final AktorIdClient aktorClient;
    private final TokenUtil tokenHandler;

    @Inject
    public SakController(SakClient sakClient, AktorIdClient aktorClient, TokenUtil tokenHandler) {
        this.sakClient = sakClient;
        this.tokenHandler = tokenHandler;
        this.aktorClient = aktorClient;
    }

    @GetMapping(SAK)
    public List<Sak> saker() {
        return sakClient.sakerFor(aktorClient.aktorIdForFnr(new Fødselsnummer(tokenHandler.autentisertBruker())));
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [sakClient=" + sakClient + ", aktorClient=" + aktorClient
                + ", tokenHandler=" + tokenHandler + "]";
    }

}
