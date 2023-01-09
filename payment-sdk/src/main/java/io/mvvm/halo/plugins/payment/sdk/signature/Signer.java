package io.mvvm.halo.plugins.payment.sdk.signature;

/**
 * Signer.
 *
 * @author: pan
 **/
public interface Signer {

    String sign(String content);

    boolean verify(String content, String sign);
}
