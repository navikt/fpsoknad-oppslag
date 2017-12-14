package no.nav.foreldrepenger.selvbetjening.controllers;

import static java.util.stream.Collectors.joining;

import javax.inject.Inject;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import no.nav.foreldrepenger.selvbetjening.AktorIdKlient;

@RestController
@RequestMapping("/startup")
public class StartupController {

   private final AktorIdKlient aktorClient;

   @Inject
   public  StartupController(AktorIdKlient aktorClient) {
      this.aktorClient = aktorClient;
   }

   @RequestMapping(method = {RequestMethod.GET}, value = "/")
   public ResponseEntity<String> startup(@RequestParam("fnr") String fnr) {
      return new ResponseEntity<String>(aktorClient.aktorIdForFnr(fnr), HttpStatus.OK);
   }


   @Override
   public String toString() {
      return getClass().getSimpleName()  +
         " [AktorIdKlient=" + aktorClient + "]";
   }

}
