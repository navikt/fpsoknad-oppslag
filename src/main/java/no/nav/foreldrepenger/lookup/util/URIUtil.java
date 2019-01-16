package no.nav.foreldrepenger.lookup.util;

import java.net.URI;

import org.springframework.http.HttpHeaders;
import org.springframework.web.util.UriComponentsBuilder;

public final class URIUtil {

    private URIUtil() {

    }

    public static URI uri(URI base, String path) {
        return uri(base, path, null);
    }

    public static URI uri(URI base, HttpHeaders queryParams) {
        return uri(base, null, queryParams);
    }

    public static URI uri(URI base, String path, HttpHeaders queryParams) {
        return builder(base, path, queryParams)
                .build()
                .toUri();
    }

    public static UriComponentsBuilder builder(URI base, String path, HttpHeaders queryParams) {
        return UriComponentsBuilder
                .fromUri(base)
                .pathSegment(path)
                .queryParams(queryParams);
    }
}
