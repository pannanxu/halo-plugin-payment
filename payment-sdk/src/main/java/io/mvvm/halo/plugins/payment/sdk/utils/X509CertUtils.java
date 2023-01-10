package io.mvvm.halo.plugins.payment.sdk.utils;

import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

/**
 * X509CertUtils.
 *
 * @author: pan
 **/
@Slf4j
public class X509CertUtils {

    /**
     * @param certPath 证书路径
     */
    public static X509Certificate getCertFromPath(String certPath) {
        try (InputStream in = new FileInputStream(certPath)) {
            CertificateFactory cf = CertificateFactory.getInstance("X.509", "BC");
            return (X509Certificate) cf.generateCertificate(in);
        } catch (Exception e) {
            log.error("X509CertUtils|证书路径加载失败|{}", e.getMessage(), e);
            throw new RuntimeException("证书路径加载失败");
        }
    }

    public static X509Certificate getCertFromContent(String certContent) {
        try (InputStream in = new ByteArrayInputStream(certContent.getBytes())) {
            CertificateFactory cf = CertificateFactory.getInstance("X.509", "BC");
            return (X509Certificate) cf.generateCertificate(in);
        } catch (Exception e) {
            log.error("X509CertUtils|证书正文加载失败|{}", e.getMessage(), e);
            throw new RuntimeException("证书正文加载失败");
        }
    }

}
