package no.nav.foreldrepenger.oppslag.util;

import static java.time.LocalDate.of;
import static no.nav.foreldrepenger.oppslag.util.DateUtil.dateWithinPeriod;
import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

public class DateUtilTest {

    @Test
    public void testDateWithinPeriod() {
        LocalDate start = of(2018, 1, 1);
        LocalDate end = of(2018, 1, 31);

        assertThat(dateWithinPeriod(of(2017, 12, 31), start, end)).isFalse();
        assertThat(dateWithinPeriod(start, start, end)).isTrue();
        assertThat(dateWithinPeriod(of(2018, 1, 15), start, end)).isTrue();
        assertThat(dateWithinPeriod(end, start, end)).isTrue();
        assertThat(dateWithinPeriod(of(2018, 2, 1), start, end)).isFalse();
    }
}
