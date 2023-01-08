package io.mvvm.halo.plugins.payment.alipay.utils;

import io.mvvm.halo.plugins.payment.sdk.utils.StreamUtil;
import org.springframework.util.Base64Utils;
import org.springframework.util.StringUtils;

import javax.crypto.Cipher;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

/**
 * RSA2Encryptor.
 *
 * @author: pan
 **/
public class RSA2Encryptor {

    public static final String SIGN_TYPE_RSA = "RSA";

    /**
     * sha256WithRsa 算法请求类型
     */
    public static final String SIGN_TYPE_RSA2 = "RSA2";

    public static final String SIGN_SHA256RSA_ALGORITHMS = "SHA256WithRSA";


    /**
     * RSA2最大加密明文大小(2048/8-11=244)
     */
    private static final int MAX_ENCRYPT_BLOCK_SIZE = 244;
    /**
     * RSA2最大解密密文大小(2048/8=256)
     */
    private static final int MAX_DECRYPT_BLOCK_SIZE = 256;

    public static PrivateKey getPrivateKeyFromPKCS8(String algorithm,
                                                    InputStream ins) throws Exception {
        if (ins == null || !StringUtils.hasLength(algorithm)) {
            return null;
        }

        KeyFactory keyFactory = KeyFactory.getInstance(algorithm);

        byte[] encodedKey = StreamUtil.readText(ins).getBytes();

        encodedKey = Base64Utils.decode(encodedKey);

        return keyFactory.generatePrivate(new PKCS8EncodedKeySpec(encodedKey));
    }

    public static PublicKey getPublicKeyFromX509(String algorithm,
                                                 InputStream ins) throws Exception {
        KeyFactory keyFactory = KeyFactory.getInstance(algorithm);

        StringWriter writer = new StringWriter();
        StreamUtil.io(new InputStreamReader(ins), writer);

        byte[] encodedKey = writer.toString().getBytes();

        encodedKey = Base64Utils.decode(encodedKey);

        return keyFactory.generatePublic(new X509EncodedKeySpec(encodedKey));
    }

    protected String getAsymmetricType() {
        return SIGN_TYPE_RSA2;
    }

    protected String getSignAlgorithm() {
        return SIGN_SHA256RSA_ALGORITHMS;
    }

    protected int getMaxDecryptBlockSize() {
        return MAX_DECRYPT_BLOCK_SIZE;
    }

    protected int getMaxEncryptBlockSize() {
        return MAX_ENCRYPT_BLOCK_SIZE;
    }

    protected String doDecrypt(String cipherTextBase64, String charset, String privateKey) throws Exception {
        int maxDecrypt = getMaxDecryptBlockSize();
        PrivateKey priKey = getPrivateKeyFromPKCS8(SIGN_TYPE_RSA,
                new ByteArrayInputStream(privateKey.getBytes()));
        Cipher cipher = Cipher.getInstance(SIGN_TYPE_RSA);
        cipher.init(Cipher.DECRYPT_MODE, priKey);
        byte[] encryptedData = !StringUtils.hasLength(charset)
                ? Base64Utils.decode(cipherTextBase64.getBytes())
                : Base64Utils.decode(cipherTextBase64.getBytes(charset));
        int inputLen = encryptedData.length;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int offSet = 0;
        byte[] cache;
        int i = 0;
        // 对数据分段解密
        while (inputLen - offSet > 0) {
            if (inputLen - offSet > maxDecrypt) {
                cache = cipher.doFinal(encryptedData, offSet, maxDecrypt);
            } else {
                cache = cipher.doFinal(encryptedData, offSet, inputLen - offSet);
            }
            out.write(cache, 0, cache.length);
            i++;
            offSet = i * maxDecrypt;
        }
        byte[] decryptedData = out.toByteArray();
        out.close();

        return !StringUtils.hasLength(charset) ? new String(decryptedData)
                : new String(decryptedData, charset);

    }

    protected String doEncrypt(String plainText, String charset, String publicKey) throws Exception {
        int maxEncrypt = getMaxEncryptBlockSize();
        PublicKey pubKey = getPublicKeyFromX509(SIGN_TYPE_RSA,
                new ByteArrayInputStream(publicKey.getBytes()));
        Cipher cipher = Cipher.getInstance(SIGN_TYPE_RSA);
        cipher.init(Cipher.ENCRYPT_MODE, pubKey);
        byte[] data = !StringUtils.hasLength(charset) ? plainText.getBytes()
                : plainText.getBytes(charset);
        int inputLen = data.length;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int offSet = 0;
        byte[] cache;
        int i = 0;
        // 对数据分段加密
        while (inputLen - offSet > 0) {
            if (inputLen - offSet > maxEncrypt) {
                cache = cipher.doFinal(data, offSet, maxEncrypt);
            } else {
                cache = cipher.doFinal(data, offSet, inputLen - offSet);
            }
            out.write(cache, 0, cache.length);
            i++;
            offSet = i * maxEncrypt;
        }
        byte[] encryptedData = Base64Utils.encode(out.toByteArray());
        out.close();

        return !StringUtils.hasLength(charset) ? new String(encryptedData)
                : new String(encryptedData, charset);
    }

    public String doSign(String content, String charset, String privateKey) throws Exception {
        PrivateKey priKey = getPrivateKeyFromPKCS8(SIGN_TYPE_RSA,
                new ByteArrayInputStream(privateKey.getBytes()));

        Signature signature = Signature.getInstance(getSignAlgorithm());

        signature.initSign(priKey);

        if (!StringUtils.hasLength(charset)) {
            signature.update(content.getBytes());
        } else {
            signature.update(content.getBytes(charset));
        }

        byte[] signed = signature.sign();

        return new String(Base64Utils.encode(signed));
    }

    protected boolean doVerify(String content, String charset, String publicKey, String sign) throws Exception {
        PublicKey pubKey = getPublicKeyFromX509("RSA",
                new ByteArrayInputStream(publicKey.getBytes()));

        Signature signature = Signature.getInstance(getSignAlgorithm());

        signature.initVerify(pubKey);

        if (!StringUtils.hasLength(charset)) {
            signature.update(content.getBytes());
        } else {
            signature.update(content.getBytes(charset));
        }

        return signature.verify(Base64Utils.decode(sign.getBytes()));
    }
}
