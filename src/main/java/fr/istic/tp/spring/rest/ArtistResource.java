package fr.istic.tp.spring.rest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import fr.istic.Artist;
import fr.istic.Database;
import fr.istic.Query;
import fr.istic.Utils;
import fr.istic.mongo.MongoDatabase;
import fr.istic.parameters.QueryParameters;
import fr.istic.parsers.FilterParser;
import fr.istic.parsers.LimitParser;
import fr.istic.parsers.SortingParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import scala.collection.JavaConverters;
import scala.util.Failure;
import scala.util.Try;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.logging.Logger;

@RestController
@Controller
@CrossOrigin
public class ArtistResource {

    private final Logger logger = Logger.getLogger("Artist Ressource REST");

    @Autowired
    private ObjectMapper mapper;

    private Database database;

    @Value("${database.url}")
    private String url;

    @PostConstruct
    private void initialize() {
        logger.info("Initialise database API...");
        database = new MongoDatabase(url, "hack", "artists");
    }

    @GetMapping(value = "/artists", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> getArtists(@RequestParam(required = false, defaultValue = "") String filter, @RequestParam(required = false, defaultValue = "") String order, @RequestParam(required = false, defaultValue = "-1") int limit) throws JsonProcessingException {
        logger.info("Receive request on /artists");
        final var parse = FilterParser.analyserFilter(filter);
        final var parseOrder = SortingParser.analyserSorting(order);
        final var parseLimit = LimitParser.parseLimit(limit);

        final List<Try<? extends QueryParameters.QueryParameter>> list = List.of(parse, parseOrder, parseLimit);
        final var newList= Utils.tryToList(JavaConverters.collectionAsScalaIterable(list).toSeq());

        final var query = database.createReadQuery();

        final var result = newList.map(element -> element.foldLeft(query, Query::apply)).flatMap(Query::send);

        if (result.isFailure()) {
            final var msg = ((Failure<String>) result).exception().getMessage();
            return ResponseEntity.badRequest().body("{ \"error\": \"" + msg + "\"}");
        } else {
            final var msg = result.get();
            final Object json = mapper.readValue(msg, Object.class);
            String indented = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(json);
            return ResponseEntity.ok(indented);
        }
    }

    @PostMapping(value = "/artists", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> addArtist(@RequestBody Artist body) {
        var result = database.write(body);
        if (result.isSuccess())
            return ResponseEntity.ok().build();
        else
            return ResponseEntity.badRequest().body(((Failure<?>) result).exception().getMessage());
    }

}
