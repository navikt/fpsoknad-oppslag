package no.nav.foreldrepenger.lookup.util;

import java.util.List;

import org.springframework.util.CollectionUtils;

public final class StringUtil {

    private StringUtil() {

    }

    public static String endelse(List<?> liste) {
        if (CollectionUtils.isEmpty(liste)) {
            return ("er");
        }
        return liste.size() == 1 ? "" : "er";
    }

}
