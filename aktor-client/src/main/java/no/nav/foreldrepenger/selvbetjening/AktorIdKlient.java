package no.nav.foreldrepenger.selvbetjening;

import java.util.Optional;

import javax.inject.Inject;
import javax.xml.ws.soap.SOAPFaultException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import no.nav.foreldrepenger.selvbetjening.aktorklient.domain.AktorId;
import no.nav.foreldrepenger.selvbetjening.aktorklient.domain.Fodselsnummer;
import no.nav.tjeneste.virksomhet.aktoer.v2.binding.AktoerV2;
import no.nav.tjeneste.virksomhet.aktoer.v2.binding.HentAktoerIdForIdentPersonIkkeFunnet;
import no.nav.tjeneste.virksomhet.aktoer.v2.meldinger.HentAktoerIdForIdentRequest;

@Component
public class AktorIdKlient {
   private static final Logger LOG = LoggerFactory.getLogger(AktorIdKlient.class);

   private final AktoerV2 aktoerV2;

   @Inject
   public AktorIdKlient(AktoerV2 aktoerV2) {
      this.aktoerV2 = aktoerV2;
   }
   
   public String aktorIdForFnr(String fnr)  {
	   Optional<AktorId> aktorId = aktorIdForFnr(new Fodselsnummer(fnr));
	   return aktorId.isPresent() ? aktorId.get().value() : "Nope";
   }

   public Optional<AktorId> aktorIdForFnr(Fodselsnummer fnr)  {
      try {
         return Optional.ofNullable(aktoerV2.hentAktoerIdForIdent(request(fnr.digits())))
            .map(r -> r.getAktoerId())
            .map(AktorId::new);
      } catch (SOAPFaultException e) {
         LOG.warn("Henting av aktørid har feilet", e);
         return Optional.empty();
      } catch (HentAktoerIdForIdentPersonIkkeFunnet e) {
         return Optional.empty();
      }
   }

   private HentAktoerIdForIdentRequest request(String indent) {
      HentAktoerIdForIdentRequest hentAktoerIdForIdentRequest = new HentAktoerIdForIdentRequest();
      hentAktoerIdForIdentRequest.setIdent(indent);
      return hentAktoerIdForIdentRequest;
   }

}
