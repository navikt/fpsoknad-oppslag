package no.nav.foreldrepenger.oppslag.lookup.ws.medl;

import static org.springframework.http.ResponseEntity.badRequest;
import static org.springframework.http.ResponseEntity.ok;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import no.nav.foreldrepenger.oppslag.lookup.FnrExtractor;
import no.nav.foreldrepenger.oppslag.lookup.ws.person.Fodselsnummer;
import no.nav.security.oidc.context.OIDCRequestContextHolder;
import no.nav.security.spring.oidc.validation.api.ProtectedWithClaims;

@RestController
@ProtectedWithClaims(issuer = "selvbetjening", claimMap = { "acr=Level4" })
class MedlController {

    private final MedlClient medlClient;

    private final OIDCRequestContextHolder contextHolder;

    public MedlController(MedlClient medlClient, OIDCRequestContextHolder contextHolder) {
        this.medlClient = medlClient;
        this.contextHolder = contextHolder;
    }

    @GetMapping(value = "/medl")
    public ResponseEntity<List<MedlPeriode>> membership() {
        String fnrFromClaims = FnrExtractor.extract(contextHolder);
        if (fnrFromClaims == null || fnrFromClaims.trim().length() == 0) {
            return badRequest().build();
        }

        Fodselsnummer fnr = new Fodselsnummer(fnrFromClaims);
        return ok(medlClient.medlInfo(fnr));
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [medlClient=" + medlClient + ", contextHolder=" + contextHolder + "]";
    }
}