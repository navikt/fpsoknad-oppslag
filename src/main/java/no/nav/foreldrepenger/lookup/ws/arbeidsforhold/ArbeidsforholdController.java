package no.nav.foreldrepenger.lookup.ws.arbeidsforhold;

import java.util.List;

import javax.inject.Inject;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import no.nav.foreldrepenger.lookup.util.TokenUtil;
import no.nav.foreldrepenger.lookup.ws.person.Fødselsnummer;

@RestController
@no.nav.security.oidc.api.ProtectedWithClaims(issuer = "selvbetjening", claimMap = { "acr=Level4" })
public class ArbeidsforholdController {

    public static final String ARBEIDSFORHOLD = "/arbeidsforhold";
    @Inject
    private ArbeidsforholdClient arbeidsforholdClient;

    @Inject
    private TokenUtil tokenHandler;

    @RequestMapping(method = { RequestMethod.GET }, value = ARBEIDSFORHOLD)
    public ResponseEntity<List<Arbeidsforhold>> workHistory() {
        Fødselsnummer fnr = tokenHandler.autentisertBruker();
        List<Arbeidsforhold> arbeidsforhold = arbeidsforholdClient.aktiveArbeidsforhold(fnr);
        return ResponseEntity.ok(arbeidsforhold);
    }
}
