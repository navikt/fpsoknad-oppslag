package no.nav.foreldrepenger.oppslag.http;

import java.time.LocalDate;
import java.util.List;

import javax.inject.Inject;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import no.nav.foreldrepenger.oppslag.arena.ArenaClient;
import no.nav.foreldrepenger.oppslag.domain.Ytelse;

@RestController
class ArenaController {

	private final ArenaClient arenaClient;

	@Inject
	public ArenaController(ArenaClient arenaClient) {
		this.arenaClient = arenaClient;
	}

	@RequestMapping(method = { RequestMethod.GET }, value = "/arena")
	public ResponseEntity<List<Ytelse>> incomeForAktor(@RequestParam("fnr") String fnr) {
		LocalDate now = LocalDate.now();
	   LocalDate oneYearAgo = LocalDate.now().minusMonths(12);
		try {
		   return ResponseEntity.ok(arenaClient.ytelser(fnr, oneYearAgo, now));
		} catch (Exception ex) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}

	}
}