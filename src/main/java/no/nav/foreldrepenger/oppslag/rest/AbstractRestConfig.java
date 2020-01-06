package no.nav.foreldrepenger.oppslag.rest;

import java.net.URI;

import org.springframework.boot.context.properties.ConstructorBinding;
import org.springframework.http.HttpHeaders;
import org.springframework.web.util.UriComponentsBuilder;

@ConstructorBinding
public abstract class AbstractRestConfig implements PingEndpointAware {

    protected abstract URI pingURI();

    private boolean enabled = true;

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    protected URI uri(String base, String path) {
        return uri(URI.create(base), path);

    }

    protected URI uri(URI base, String path) {
        return uri(base, path, null);
    }

    protected URI uri(URI base, String path, HttpHeaders queryParams) {
        return builder(base, path, queryParams)
                .build()
                .toUri();
    }

    protected HttpHeaders queryParams(String key, String value) {
        HttpHeaders queryParams = new HttpHeaders();
        queryParams.add(key, value);
        return queryParams;
    }

    private static UriComponentsBuilder builder(URI base, String path, HttpHeaders queryParams) {
        return UriComponentsBuilder
                .fromUri(base)
                .pathSegment(path)
                .queryParams(queryParams);
    }
}
