package no.nav.foreldrepenger.oppslag.rest;

import static org.slf4j.LoggerFactory.getLogger;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import no.nav.foreldrepenger.oppslag.util.PingableRegisters;
import no.nav.foreldrepenger.oppslag.util.TokenUtil;
import no.nav.foreldrepenger.oppslag.ws.Søkerinfo;
import no.nav.foreldrepenger.oppslag.ws.aktor.AktørId;
import no.nav.foreldrepenger.oppslag.ws.aktor.AktørTjeneste;
import no.nav.foreldrepenger.oppslag.ws.arbeidsforhold.Arbeidsforhold;
import no.nav.foreldrepenger.oppslag.ws.arbeidsforhold.ArbeidsforholdTjeneste;
import no.nav.foreldrepenger.oppslag.ws.person.Fødselsnummer;
import no.nav.foreldrepenger.oppslag.ws.person.ID;
import no.nav.foreldrepenger.oppslag.ws.person.Person;
import no.nav.foreldrepenger.oppslag.ws.person.PersonTjeneste;
import no.nav.security.token.support.core.api.ProtectedWithClaims;
import no.nav.security.token.support.core.api.Unprotected;

@RestController
@ProtectedWithClaims(issuer = "selvbetjening", claimMap = { "acr=Level4" })
@RequestMapping(OppslagController.OPPSLAG)
public class OppslagController {

    public static final String OPPSLAG = "/oppslag";

    private static final Logger LOG = getLogger(OppslagController.class);

    private final AktørTjeneste aktør;

    private final PersonTjeneste person;

    private final ArbeidsforholdTjeneste arbeid;

    private final TokenUtil tokenUtil;

    @Inject
    public OppslagController(AktørTjeneste aktør, PersonTjeneste person, ArbeidsforholdTjeneste arbeid,
            TokenUtil tokenHandler) {
        this.aktør = aktør;
        this.person = person;
        this.arbeid = arbeid;
        this.tokenUtil = tokenHandler;
    }

    @Unprotected
    @GetMapping("/ping")
    public String ping(
            @RequestParam(name = "register", defaultValue = "all", required = false) PingableRegisters register) {
        LOG.info("Vil pinge register {}", register);
        switch (register) {
        case aareg:
            arbeid.ping();
            break;
        case aktør:
            aktør.ping();
            break;
        case tps:
            person.ping();
            break;
        case all:
            aktør.ping();
            person.ping();
            arbeid.ping();
            break;
        }
        return registerNavn(register) + " er i toppform";
    }

    @GetMapping
    public Søkerinfo essensiellSøkerinfo() {
        Fødselsnummer fnr = new Fødselsnummer(tokenUtil.autentisertBruker());
        AktørId aktorId = aktør.aktorIdForFnr(fnr);
        Person p = person.hentPersonInfo(new ID(aktorId, fnr));
        List<Arbeidsforhold> arbeidsforhold = arbeid.aktiveArbeidsforhold(fnr);
        return new Søkerinfo(p, arbeidsforhold);
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

    private static String registerNavn(PingableRegisters register) {
        return register.equals(PingableRegisters.all)
                ? Arrays.stream(PingableRegisters.values())
                        .map(PingableRegisters::name)
                        .filter(s -> !s.equals("all"))
                        .collect(Collectors.joining(","))
                : register.name();
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [aktør=" + aktør + ", person=" + person + ", arbeid=" + arbeid + "]";
    }

}
