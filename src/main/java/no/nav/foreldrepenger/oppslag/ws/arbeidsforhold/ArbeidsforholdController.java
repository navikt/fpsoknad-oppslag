package no.nav.foreldrepenger.oppslag.ws.arbeidsforhold;

import static no.nav.foreldrepenger.oppslag.config.Constants.ISSUER;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import no.nav.foreldrepenger.oppslag.util.TokenUtil;
import no.nav.foreldrepenger.oppslag.ws.person.Fødselsnummer;
import no.nav.security.token.support.core.api.ProtectedWithClaims;
import no.nav.security.token.support.core.api.Unprotected;

@RequestMapping(path = ArbeidsforholdController.ARBEIDSFORHOLD, produces = APPLICATION_JSON_VALUE)
@RestController
@ProtectedWithClaims(issuer = ISSUER, claimMap = { "acr=Level4" })
public class ArbeidsforholdController {
    public static final String ARBEIDSFORHOLD = "/arbeidsforhold";
    private final ArbeidsforholdTjeneste arbeidsforholdClient;
    private final TokenUtil tokenUtil;

    public ArbeidsforholdController(ArbeidsforholdTjeneste arbeidsforholdClient, TokenUtil tokenUtil) {
        this.arbeidsforholdClient = arbeidsforholdClient;
        this.tokenUtil = tokenUtil;
    }

    @GetMapping
    public List<Arbeidsforhold> workHistory() {
        return arbeidsforholdClient.aktiveArbeidsforhold(new Fødselsnummer(tokenUtil.autentisertBruker()));
    }

    @GetMapping("/navn")
    @Unprotected
    public String arbeidsgiverNavn(@RequestParam(name = "orgnr") String orgnr) {
        return arbeidsforholdClient.arbeidsgiverNavn(orgnr);
    }

    @GetMapping("/ping")
    @Unprotected
    public String ping(@RequestParam(name = "navn", defaultValue = "jordboer") String navn) {
        return "Hallo " + navn + " fra ubeskyttet ressurs";
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [arbeidsforholdClient=" + arbeidsforholdClient + ", tokenUtil="
                + tokenUtil + "]";
    }
}
