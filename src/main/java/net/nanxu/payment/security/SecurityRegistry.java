package net.nanxu.payment.security;

import java.util.ArrayList;
import java.util.List;
import net.nanxu.payment.security.impl.Decryptor;
import net.nanxu.payment.security.impl.Encryptor;
import net.nanxu.payment.security.impl.FrequencyLimiter;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

/**
 * SecurityRegistry.
 *
 * @author: P
 **/
@Component
public class SecurityRegistry {

    private final List<SecurityModule> modules = new ArrayList<>();

    public SecurityRegistry() {
        modules.add(new Decryptor());
        modules.add(new FrequencyLimiter());
        modules.add(new Encryptor());
    }

    public <T extends SecurityModule> Flux<SecurityModule> getModules(Class<T> clazz) {
        return Flux.fromIterable(modules).filter(m -> clazz.isAssignableFrom(m.getClass()));
    }

}
