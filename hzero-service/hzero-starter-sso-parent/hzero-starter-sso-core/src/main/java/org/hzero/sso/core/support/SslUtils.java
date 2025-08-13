package org.hzero.sso.core.support;

import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import javax.net.ssl.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SslUtils {
    private static final Logger logger = LoggerFactory.getLogger(SslUtils.class);

    private static void trustAllHttpsCertificates() throws Exception {
        TrustManager[] trustAllCerts = new TrustManager[1];
        TrustManager tm = new miTM();
        trustAllCerts[0] = tm;
        SSLContext sc = SSLContext.getInstance("SSL");
        sc.init(null, trustAllCerts, null);
        HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
    }

    static class miTM implements TrustManager, X509TrustManager {

        @Override
        public X509Certificate[] getAcceptedIssuers() {
            return null;
        }

        public boolean isServerTrusted(X509Certificate[] certs) {
            return true;
        }

        public boolean isClientTrusted(X509Certificate[] certs) {
            return true;
        }

        @Override
        public void checkServerTrusted(X509Certificate[] certs, String authType) throws CertificateException {
        }

        @Override
        public void checkClientTrusted(X509Certificate[] certs, String authType) throws CertificateException {
        }
    }

    /**
     * 忽略HTTPS请求的SSL证书，必须在openConnection之前调用
     */
    public static void ignoreSsl() {
        HostnameVerifier hv = (urlHostName, session) -> {
            logger.info("Ignore Ssl: URL Host: " + urlHostName + " vs. " + session.getPeerHost());
            return true;
        };
        try {
            trustAllHttpsCertificates();
        } catch (Exception e) {
            logger.error("trust ssl error. ", e);
        }
        HttpsURLConnection.setDefaultHostnameVerifier(hv);
    }

}