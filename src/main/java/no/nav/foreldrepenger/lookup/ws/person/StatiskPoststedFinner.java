package no.nav.foreldrepenger.lookup.ws.person;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.io.IOUtils;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import com.google.common.base.Charsets;
import com.google.common.base.Splitter;

import no.nav.foreldrepenger.lookup.util.Pair;

public class StatiskPoststedFinner implements PoststedFinner {

    private final Map<String, String> map;

    public StatiskPoststedFinner() {
        this(new ClassPathResource("postnr.txt"));
    }

    public StatiskPoststedFinner(Resource resource) {
        this.map = lines(resource)
                .stream()
                .map(this::poststedAndPostnr)
                .collect(Collectors.toMap(pair -> pair.getFirst(), pair -> pair.getSecond()));
    }

    private List<String> lines(Resource resource) {
        try {
            return IOUtils.readLines(resource.getInputStream(), Charsets.UTF_8);
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        }
    }

    private Pair<String, String> poststedAndPostnr(String line) {
        List<String> elements = Splitter.on('\t').splitToList(line);
        return Pair.of(elements.get(0), elements.get(1));
    }

    @Override
    public String poststed(String postnummer) {
        return map.get(postnummer);
    }
}
