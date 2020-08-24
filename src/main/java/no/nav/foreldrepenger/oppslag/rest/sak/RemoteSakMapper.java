package no.nav.foreldrepenger.oppslag.rest.sak;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// Format 2017-08-04T23:56:09+02:00 must be handled

public class RemoteSakMapper {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE;
    private static final Logger LOG = LoggerFactory.getLogger(RemoteSakMapper.class);

    private RemoteSakMapper() {

    }

    public static Sak map(RemoteSak remoteSak) {
        return new Sak(Long.toString(remoteSak.getId()),
                remoteSak.getTema(),
                remoteSak.getApplikasjon(),
                remoteSak.getFagsakNr(),
                "",
                parseSafely(remoteSak.getOpprettetTidspunkt()),
                remoteSak.getOpprettetAv());
    }

    private static LocalDate parseSafely(String opprettet) {
        if (opprettet == null) {
            LOG.warn("ingen opprettet dato");
            return null;
        }
        int index = opprettet.indexOf('T');
        if (index < 0) {
            LOG.warn("Nok et merkverdig format {}", opprettet);
            return null;
        }
        String dato = opprettet.substring(0, index);
        try {
            return Optional.of(dato)
                    .map(s -> LocalDate.parse(s, FORMATTER)).orElse(null);
        } catch (Exception e) {
            LOG.warn("Kunne ikke parse dato {}", dato);
            return null;
        }
    }

}
