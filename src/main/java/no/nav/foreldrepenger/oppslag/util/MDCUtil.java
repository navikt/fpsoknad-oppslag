package no.nav.foreldrepenger.oppslag.util;

import static no.nav.foreldrepenger.oppslag.config.Constants.NAV_CALL_ID;

import java.util.Optional;

import org.slf4j.MDC;

public final class MDCUtil {
    private MDCUtil() {

    }

    public static String callId() {
        return MDC.get(NAV_CALL_ID);
    }

    public static void tilMDC(String key, Object value) {
        if (value != null) {
            tilMDC(key, value.toString());
        }
    }

    public static void tilMDC(String key, String value) {
        tilMDC(key, value, null);
    }

    public static void tilMDC(String key, String value, String defaultValue) {
        MDC.put(key, Optional.ofNullable(value).orElse(defaultValue));
    }
}
