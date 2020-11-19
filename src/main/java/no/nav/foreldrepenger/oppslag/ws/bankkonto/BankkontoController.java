package no.nav.foreldrepenger.oppslag.ws.bankkonto;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import no.nav.foreldrepenger.oppslag.util.TokenUtil;
import no.nav.security.token.support.core.api.ProtectedWithClaims;

@RestController
@ProtectedWithClaims(issuer = "selvbetjening", claimMap = { "acr=Level4" })
@RequestMapping(BankkontoController.PERSON)
public class BankkontoController {

    private static final Logger LOG = LoggerFactory.getLogger(BankkontoController.class);

    public static final String PERSON = "/person";
    private final BankkontoTjeneste personClient;
    private final TokenUtil tokenHandler;

    @Inject
    public BankkontoController(BankkontoTjeneste personClient,
            TokenUtil tokenHandler) {
        this.personClient = personClient;
        this.tokenHandler = tokenHandler;
    }

    @GetMapping("/kontonr")
    public Bankkonto kontonr() {
        LOG.info("Slår opp kontonummer");
        var knr = personClient.bankkonto(new Fødselsnummer(tokenHandler.autentisertBruker()));
        LOG.info("Slo opp kontonummer OK");
        return knr;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [ personClient=" + personClient + "]";
    }
}