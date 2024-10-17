package net.nanxu.payment.generator;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import reactor.core.publisher.Mono;

/**
 * OrderNoGenerator.
 *
 * @author: P
 **/
public interface OrderNoGenerator {

    OrderNoGenerator simple = new SimpleOrderNoGenerator();
    OrderNoGenerator uuid = new UUIDOrderNoGenerator();

    Mono<String> generate();

    class SimpleOrderNoGenerator implements OrderNoGenerator {

        @Override
        public Mono<String> generate() {
            String prefix = "1";
            String date = new SimpleDateFormat("yyyyMMddHHmmss").format(Instant.now());
            int random = ThreadLocalRandom.current().nextInt(10000);
            return Mono.just(prefix + date + random);
        }
    }

    class UUIDOrderNoGenerator implements OrderNoGenerator {
        @Override
        public Mono<String> generate() {
            return Mono.just(UUID.randomUUID().toString());
        }
    }
}
