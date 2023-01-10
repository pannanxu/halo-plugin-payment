package io.mvvm.halo.plugins.payment.alipay.signature;

import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.springframework.util.StringUtils;

import java.io.ByteArrayInputStream;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.Security;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Collection;

/**
 * AlipaySignatureUtils.
 *
 * @author: pan
 **/
@Slf4j
public class AlipaySignatureUtils {

    private static BouncyCastleProvider provider;

    static {
        provider = new BouncyCastleProvider();
        Security.addProvider(provider);
    }

    private static String fillMD5(String md5) {
        return md5.length() == 32 ? md5 : fillMD5("0" + md5);
    }

    /**
     * 应用公钥证书SN（app_cert_sn）
     */
    public static String getAppCertSN(X509Certificate cf) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update((cf.getIssuerX500Principal().getName() + cf.getSerialNumber()).getBytes());
            String certSN = new BigInteger(1, md.digest()).toString(16);
            //BigInteger会把0省略掉，需补全至32位
            certSN = fillMD5(certSN);
            return certSN;
        } catch (NoSuchAlgorithmException e) {
            log.error("支付宝|获取应用公钥证书SN异常|{}", e.getMessage(), e);
            throw new RuntimeException("获取支付宝应用公钥证书SN异常");
        }
    }

    /**
     * 获取支付宝根证书SN（alipay_root_cert_sn）
     */
    public static String getAlipayRootCertSN(String rootCertContent) {
        String rootCertSN = null;
        try {
            X509Certificate[] x509Certificates = readPemCertChain(rootCertContent);
            MessageDigest md = MessageDigest.getInstance("MD5");
            for (X509Certificate c : x509Certificates) {
                if (c.getSigAlgOID().startsWith("1.2.840.113549.1.1")) {
                    md.update((c.getIssuerX500Principal().getName() + c.getSerialNumber()).getBytes());
                    String certSN = new BigInteger(1, md.digest()).toString(16);
                    //BigInteger会把0省略掉，需补全至32位
                    certSN = fillMD5(certSN);
                    if (!StringUtils.hasLength(rootCertSN)) {
                        rootCertSN = certSN;
                    } else {
                        rootCertSN = rootCertSN + "_" + certSN;
                    }
                }

            }
        } catch (Exception e) {
            log.error("支付宝|获取支付宝根证书SN异常|{}", e.getMessage(), e);
            throw new RuntimeException("获取支付宝根证书SN异常");
        }
        return rootCertSN;
    }

    private static X509Certificate[] readPemCertChain(String cert) throws CertificateException {
        ByteArrayInputStream inputStream = new ByteArrayInputStream(cert.getBytes());
        CertificateFactory factory = CertificateFactory.getInstance("X.509", provider);
        Collection<? extends Certificate> certificates = factory.generateCertificates(inputStream);
        X509Certificate[] x509Certificates = new X509Certificate[certificates.size()];
        return certificates.toArray(x509Certificates);
    }

}
