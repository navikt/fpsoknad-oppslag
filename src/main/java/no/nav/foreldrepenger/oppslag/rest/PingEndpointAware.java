package no.nav.foreldrepenger.oppslag.rest;

import java.net.URI;

import no.nav.foreldrepenger.oppslag.util.Pingable;

public interface PingEndpointAware extends Pingable {

    URI pingEndpoint();

    String name();

}
