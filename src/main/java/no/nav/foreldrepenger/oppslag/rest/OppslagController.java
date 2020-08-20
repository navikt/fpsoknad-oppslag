package no.nav.foreldrepenger.oppslag.rest;

import javax.inject.Inject;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import no.nav.foreldrepenger.oppslag.http.ProtectedRestController;
import no.nav.foreldrepenger.oppslag.util.TokenUtil;
import no.nav.foreldrepenger.oppslag.ws.aktor.AktørId;
import no.nav.foreldrepenger.oppslag.ws.aktor.AktørTjeneste;
import no.nav.foreldrepenger.oppslag.ws.person.Fødselsnummer;
import no.nav.foreldrepenger.oppslag.ws.person.PersonTjeneste;

@ProtectedRestController(OppslagController.OPPSLAG)
public class OppslagController {

    public static final String OPPSLAG = "/oppslag";

    private final AktørTjeneste aktør;

    private final PersonTjeneste person;

    private final TokenUtil tokenUtil;

    @Inject
    public OppslagController(AktørTjeneste aktør, PersonTjeneste person, /* ArbeidsforholdTjeneste arbeid, */
            TokenUtil tokenHandler) {
        this.aktør = aktør;
        this.person = person;
        this.tokenUtil = tokenHandler;
    }

    @GetMapping("/aktor")
    public AktørId getAktørId() {
        return getAktørIdForFNR(new Fødselsnummer(tokenUtil.autentisertBruker()));
    }

    @GetMapping("/aktorfnr")
    public AktørId getAktørIdForFNR(@RequestParam(name = "fnr") Fødselsnummer fnr) {
        return aktør.aktorIdForFnr(fnr);
    }

    @GetMapping("/fnr")
    public Fødselsnummer getFNRforAktørIdR(@RequestParam(name = "aktorId") AktørId aktorId) {
        return aktør.fnrForAktørId(aktorId);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [aktør=" + aktør + ", person=" + person /* + ", arbeid=" + arbeid */
                + "]";
    }

}
