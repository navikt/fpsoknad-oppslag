package no.nav.foreldrepenger.oppslag.http;

import no.nav.foreldrepenger.oppslag.aktor.AktorIdClient;
import no.nav.foreldrepenger.oppslag.domain.*;
import no.nav.foreldrepenger.oppslag.http.util.FnrExtractor;
import no.nav.foreldrepenger.oppslag.orchestrate.CoordinatedLookup;
import no.nav.foreldrepenger.oppslag.person.PersonClient;
import no.nav.security.oidc.context.OIDCRequestContextHolder;
import no.nav.security.spring.oidc.validation.api.ProtectedWithClaims;
import no.nav.security.spring.oidc.validation.api.Unprotected;
import org.slf4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.inject.Inject;

import static org.slf4j.LoggerFactory.getLogger;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@ProtectedWithClaims(issuer = "selvbetjening", claimMap = {"acr=Level4"})
@RequestMapping("/oppslag")
public class OppslagController {

    private static final Logger LOG = getLogger(OppslagController.class);

    @Inject
    private PersonClient personClient;

    @Inject
    private AktorIdClient aktorClient;

    @Inject
    private CoordinatedLookup personInfo;

    @Inject
    private OIDCRequestContextHolder contextHolder;

    @Unprotected
    @GetMapping(value = "/ping", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<String> ping(@RequestParam("navn") String navn) {
        LOG.info("I was pinged");
        return ResponseEntity.status(HttpStatus.OK).body("Hello " + navn);
    }

    @GetMapping(value = "/")
    public ResponseEntity<SøkerInformasjon> gimmeAllYouGot() {
        String fnrFromClaims = FnrExtractor.extract(contextHolder);
        if (fnrFromClaims == null || fnrFromClaims.trim().length() == 0) {
            return ResponseEntity.badRequest().build();
        }

        Fodselsnummer fnr = new Fodselsnummer(fnrFromClaims);
        AktorId aktorid = aktorClient.aktorIdForFnr(fnr);
        Person person = personClient.hentPersonInfo(new ID(aktorid, fnr));
        AggregatedLookupResults results = personInfo.gimmeAllYouGot(new ID(aktorid, fnr));
        return new ResponseEntity<>(
            new SøkerInformasjon(
                person,
                results.getInntekt(),
                results.getYtelser(),
                results.getArbeidsforhold(),
                results.getMedlPerioder()),
            OK);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [personClient=" + personClient + ", aktorClient=" + aktorClient
            + ", personInfo=" + personInfo
            + "]";
    }

}
