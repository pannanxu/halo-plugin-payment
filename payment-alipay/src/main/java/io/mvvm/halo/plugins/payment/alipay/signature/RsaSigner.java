package io.mvvm.halo.plugins.payment.alipay.signature;

import io.mvvm.halo.plugins.payment.sdk.signature.Signer;

/**
 * RsaSigner.
 *
 * @author: pan
 **/
public class RsaSigner extends RSAEncryptor implements Signer {

    private final String privateKey;
    private final String publicKey;

    public RsaSigner(String privateKey, String publicKey) {
        this.privateKey = privateKey;
        this.publicKey = publicKey;
    }

    @Override
    public String sign(String content) {
        try {
            return doSign(content, "utf-8", privateKey);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean verify(String content, String sign) {
        try {
            return doVerify(content, "utf-8", publicKey, sign);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
