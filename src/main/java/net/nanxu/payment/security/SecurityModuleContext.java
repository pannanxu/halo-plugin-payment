package net.nanxu.payment.security;

import lombok.Data;
import lombok.RequiredArgsConstructor;

/**
 * SecurityModuleContext.
 *
 * @author: P
 **/
@Data
@RequiredArgsConstructor
public class SecurityModuleContext {

    private final Object original;
}
