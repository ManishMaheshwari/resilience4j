package com.manish.httpserver.spring;

import com.manish.util.Person;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.Random;

@RestController
public class CrankyPersonController {
    private static final Logger LOGGER = LoggerFactory.getLogger(CrankyPersonController.class);

    /**
     * All-well case.
     *
     * @return
     */
    @GetMapping(value = "/person", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Mono<Person>> findPerson() {
        LOGGER.info("Handling mono - findPerson");
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(Mono.just(Person.getSomePerson())
                        .doOnNext(person -> LOGGER.info("Server sent: {}", person)));

    }

    /**
     * Response errors out with a probability of "errorRate"
     *
     * @param errorRate must be between 0 to 100
     * @return
     */
    @GetMapping(value = "/person/error/{errorRate}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Mono<Person>> findPersonRandomError(@PathVariable int errorRate) {
        boolean errorOut = new Random().nextInt(100) < errorRate ? true : false;
        LOGGER.info("Handling mono - findPersonRandomError with error value being {}", errorOut);
        if (errorOut) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .header("X-Reason", "error-out")
                    .body(Mono.just(Person.getNoOne()));
        } else {

            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(Mono.just(Person.getSomePerson())
                            .doOnNext(person -> LOGGER.info("Server sent: {}", person)));

        }
    }

    /**
     * Response is delayed randomly between 0 and "delay" milliseconds.
     *
     * @param delay
     * @return
     */
    @GetMapping(value = "/person/delay/{delay}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Mono<Person>> findPersonRandomSlow(@PathVariable int delay) {
        int addDelay = new Random().nextInt(delay);
        LOGGER.info("Handling mono - findPersonRandomSlow with delay {} ms", addDelay);
        return ResponseEntity
                .status(HttpStatus.OK)
                .header("X-Delay", addDelay + "")
                .body(Mono.just(Person.getSomePerson())
                        .delayElement(Duration.ofMillis(addDelay))
                        .doOnNext(person -> LOGGER.info("Server sent: {}", person)));
    }
}
