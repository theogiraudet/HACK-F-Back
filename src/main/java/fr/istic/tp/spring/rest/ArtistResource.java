package fr.istic.tp.spring.rest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import fr.istic.Database;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import scala.util.Failure;

import javax.annotation.PostConstruct;

@RestController
@Controller
public class ArtistResource {

    @Autowired
    private ObjectMapper mapper;

    @PostConstruct
    private void initialize() {
        Database.init();
    }

    @GetMapping(value = "/artists", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> getArtists(@RequestParam String filter) throws JsonProcessingException {
        final var result = Database.read(filter);
        if (result.isFailure()) {
            final var msg = ((Failure<String>) result).exception().getMessage();
            return ResponseEntity.badRequest().body(msg);
        } else {
            final var msg = result.get();
            final Object json = mapper.readValue(msg, Object.class);
            String indented = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(json);
            return ResponseEntity.ok(indented);
        }
    }

}
