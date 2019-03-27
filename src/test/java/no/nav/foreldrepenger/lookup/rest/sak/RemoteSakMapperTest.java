package no.nav.foreldrepenger.lookup.rest.sak;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

public class RemoteSakMapperTest {

    @Test
    public void mapValues() {
        RemoteSak remoteSak = new RemoteSak(1L, "temaet", "appen", "aktøren",
                "org123", "fagsakNr", "oppretteren", "2018-09-02T10:15:42.659+02:00");
        Sak expected = new Sak("1", "temaet", "appen", "fagsakNr", null, LocalDate.of(2018, 9, 2), "");
        Sak actual = RemoteSakMapper.map(remoteSak);
        assertEquals(expected, actual);
    }

    @Test
    public void alternativeDateFormat() {
        RemoteSak remoteSak = new RemoteSak(1L, "temaet", "appen", "aktøren",
                "org123", "fagsakNr", "oppretteren", "2018-08-27T09:16:01.2+02:00");
        Sak expected = new Sak("1", "temaet", "appen", "fagsakNr", null, LocalDate.of(2018, 8, 27), "");
        Sak actual = RemoteSakMapper.map(remoteSak);
        assertEquals(expected, actual);
    }

    @Test
    public void alternativeDateFormat1() {
        RemoteSak remoteSak = new RemoteSak(1L, "temaet", "appen", "aktøren",
                "org123", "fagsakNr", "oppretteren", "2015-11-12T08:19:16+01:00");
        Sak expected = new Sak("1", "temaet", "appen", "fagsakNr", null, LocalDate.of(2015, 11, 12), "");
        Sak actual = RemoteSakMapper.map(remoteSak);
        assertEquals(expected, actual);
    }

}
