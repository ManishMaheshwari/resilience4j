package com.manish.circuitbreaking;

import com.manish.util.Helper;
import com.manish.util.Person;
import io.github.resilience4j.reactor.circuitbreaker.operator.CircuitBreakerOperator;
import io.github.resilience4j.reactor.timelimiter.TimeLimiterOperator;
import io.github.resilience4j.timelimiter.TimeLimiter;
import io.github.resilience4j.timelimiter.TimeLimiterRegistry;
import io.netty.handler.timeout.TimeoutException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.http.MediaType;
import org.springframework.util.StopWatch;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import javax.annotation.PostConstruct;


@SpringBootApplication
public class R4J_05_TimeLimiter implements  CommandLineRunner{

    public static final Logger LOGGER = LoggerFactory.getLogger(R4J_05_TimeLimiter.class);

    @Autowired
    private TimeLimiterRegistry timeLimiterRegistry;

    private WebClient wc;
    private TimeLimiter timeLimiter;

    @PostConstruct
    public void init() {
        this.wc = WebClient.builder()
                .baseUrl("http://localhost:8080")
                .defaultHeader("Client", "WebClient")
                .build();
        this.timeLimiter = timeLimiterRegistry.timeLimiter("crankyServiceTimeLimit");
        LOGGER.info("timeLimiter crankyServiceTimeLimit Config: {}", timeLimiter.getTimeLimiterConfig().toString());
    }

    public static void main(String[] args) {
        new SpringApplicationBuilder(R4J_05_TimeLimiter.class)
                .web(WebApplicationType.NONE)
                .run(args);
    }

    @Override
    public void run(String... args) throws Exception {

        for (int i = 0; i < 100; i++) {
            StopWatch sw = new StopWatch();
            wc
                    .get()
                    .uri(uriBuilder ->
                            uriBuilder.pathSegment("person", "delay", "500")
                                    .build()
                    )
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .bodyToMono(Person.class)
                    .transform(TimeLimiterOperator.of(timeLimiter))
                    .doOnSubscribe(subscription -> sw.start())
                    .doOnNext(data -> {
                        sw.stop();
                    })
                    .subscribe(data -> LOGGER.info("Data recd: {}, \nDelay: {} ms", data, sw.getTotalTimeMillis()),
                            err -> LOGGER.error("Error recd: {}, \nDelay: {} ms", err.getMessage(), sw.getTotalTimeMillis()))
            ;
            Helper.holdMillis(500);
        }
        
        Helper.hold(1);
    }

}