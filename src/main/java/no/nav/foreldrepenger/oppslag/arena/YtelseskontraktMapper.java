package no.nav.foreldrepenger.oppslag.arena;

import no.nav.foreldrepenger.oppslag.domain.Ytelse;
import no.nav.foreldrepenger.oppslag.time.CalendarConverter;
import no.nav.tjeneste.virksomhet.ytelseskontrakt.v3.informasjon.ytelseskontrakt.Ytelseskontrakt;

public class YtelseskontraktMapper {

   public static Ytelse map(Ytelseskontrakt ytelse) {
      return new Ytelse(ytelse.getYtelsestype(),
         ytelse.getStatus(),
         CalendarConverter.toDate(ytelse.getFomGyldighetsperiode()),
         CalendarConverter.toDate(ytelse.getTomGyldighetsperiode()));
   }

}