package no.nav.foreldrepenger.oppslag.util;

import static java.util.Collections.emptyList;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public class StreamUtil {
    private StreamUtil() {
    }

    public static <T> Stream<T> safeStream(List<T> list) {
        return Optional.ofNullable(list).orElse(emptyList()).stream();
    }
}
