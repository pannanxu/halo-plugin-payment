package net.nanxu.payment.generator;

import java.util.HashMap;
import java.util.Map;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

/**
 * OrderNoProvider.
 *
 * @author: P
 **/
@Component
public class OrderNoProvider {

    private final Map<String, OrderNoGenerator> generators = new HashMap<>();
    public static final String USED_GENERATOR = "simple";

    public OrderNoProvider() {
        generators.put(USED_GENERATOR, OrderNoGenerator.simple);
    }

    public Mono<String> generate() {
        return generators.get(USED_GENERATOR).generate();
    }
}
