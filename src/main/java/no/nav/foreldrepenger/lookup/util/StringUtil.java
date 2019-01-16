package no.nav.foreldrepenger.lookup.util;

import java.io.UnsupportedEncodingException;
import java.util.Base64;
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

    public static String encode(String string) {
        try {
            return Base64.getEncoder().encodeToString(string.getBytes("utf-8"));
        } catch (UnsupportedEncodingException e) {
            throw new IllegalArgumentException(e);
        }
    }

}
