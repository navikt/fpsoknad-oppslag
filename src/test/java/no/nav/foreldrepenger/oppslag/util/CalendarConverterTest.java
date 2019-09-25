package no.nav.foreldrepenger.oppslag.util;

import org.junit.jupiter.api.Test;

import no.nav.foreldrepenger.oppslag.util.DateUtil;

import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CalendarConverterTest {

	@Test
	public void toCalendar() throws Exception {
		LocalDate date = LocalDate.of(2017, 12, 13);
		XMLGregorianCalendar expected = DatatypeFactory.newInstance().newXMLGregorianCalendar("2017-12-13");
		XMLGregorianCalendar actual = DateUtil.toXMLGregorianCalendar(date);
		assertEquals(actual, expected);
	}

	@Test
	public void toDate() throws Exception {
		XMLGregorianCalendar xgc = DatatypeFactory.newInstance().newXMLGregorianCalendar("2017-12-13");
		LocalDate expected = LocalDate.of(2017, 12, 13);
		LocalDate actual = DateUtil.toLocalDate(xgc);
		assertEquals(actual, expected);
	}

}
