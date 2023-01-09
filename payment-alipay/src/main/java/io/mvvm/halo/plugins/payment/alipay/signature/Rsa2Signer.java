package io.mvvm.halo.plugins.payment.alipay.signature;

/**
 * Rsa2Signer.
 *
 * @author: pan
 **/
public class Rsa2Signer extends RsaSigner {
    /**
     * RSA2最大加密明文大小(2048/8-11=244)
     */
    private static final int MAX_ENCRYPT_BLOCK_SIZE = 244;
    /**
     * RSA2最大解密密文大小(2048/8=256)
     */
    private static final int MAX_DECRYPT_BLOCK_SIZE = 256;

    public Rsa2Signer(String privateKey, String publicKey) {
        super(privateKey, publicKey);
    }

    @Override
    protected String getAsymmetricType() {
        return AlipayConstants.SIGN_TYPE_RSA2;
    }

    @Override
    protected String getSignAlgorithm() {
        return AlipayConstants.SIGN_SHA256RSA_ALGORITHMS;
    }

    @Override
    protected int getMaxDecryptBlockSize() {
        return MAX_DECRYPT_BLOCK_SIZE;
    }

    @Override
    protected int getMaxEncryptBlockSize() {
        return MAX_ENCRYPT_BLOCK_SIZE;
    }

}
