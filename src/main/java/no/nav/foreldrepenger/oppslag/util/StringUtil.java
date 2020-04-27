package no.nav.foreldrepenger.oppslag.util;

import java.io.UnsupportedEncodingException;
import java.util.Base64;
import java.util.List;

import org.springframework.util.CollectionUtils;

import com.google.common.base.Strings;

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

    public static String mask(String value) {
        return (value != null) && (value.length() == 11) ? Strings.padEnd(value.substring(0, 6), 11, '*') : value;
    }

}
