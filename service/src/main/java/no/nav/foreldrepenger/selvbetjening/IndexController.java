package no.nav.foreldrepenger.selvbetjening;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class IndexController {

  @RequestMapping(method = {RequestMethod.GET}, value = "/")
  public ResponseEntity<String> isAlive() {
    return new ResponseEntity<String>("Howdy!", HttpStatus.OK);
  }

}
