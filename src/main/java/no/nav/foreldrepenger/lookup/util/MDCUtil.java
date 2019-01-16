package no.nav.foreldrepenger.lookup.util;

import static no.nav.foreldrepenger.lookup.Constants.NAV_CALL_ID;

import org.slf4j.MDC;

public final class MDCUtil {
    private MDCUtil() {

    }

    public static String callId() {
        return MDC.get(NAV_CALL_ID);
    }
}
