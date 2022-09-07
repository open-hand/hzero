package org.hzero.sso.saml.util;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.interfaces.RSAPrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.ArrayList;
import java.util.Base64;

import org.apache.commons.io.IOUtils;

public class KeyStoreLocator {

    private static CertificateFactory certificateFactory;

    static {
        try {
            certificateFactory = CertificateFactory.getInstance("X.509");
        } catch (CertificateException e) {
            throw new RuntimeException(e);
        }
    }

    public static KeyStore createKeyStore(String pemPassPhrase) {
        try {
            KeyStore keyStore = KeyStore.getInstance("JKS");
            keyStore.load(null, pemPassPhrase.toCharArray());
            return keyStore;
        } catch (Exception e) {
            // too many exceptions we can't handle, so brute force catch
            throw new RuntimeException(e);
        }
    }

    // privateKey must be in the DER unencrypted PKCS#8 format. See README.md
    public static void addPrivateKey(KeyStore keyStore, String alias, String privateKey, String certificate,
            String password) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException, KeyStoreException,
            CertificateException {
        String wrappedCert = wrapCert(certificate);
        byte[] decodedKey = Base64.getDecoder().decode(privateKey.getBytes(StandardCharsets.UTF_8));

        char[] passwordChars = password.toCharArray();
        Certificate cert = certificateFactory.generateCertificate(new ByteArrayInputStream(wrappedCert.getBytes(StandardCharsets.UTF_8)));
        ArrayList<Certificate> certs = new ArrayList<>();
        certs.add(cert);

        byte[] privKeyBytes = IOUtils.toByteArray(new ByteArrayInputStream(decodedKey));

        KeySpec ks = new PKCS8EncodedKeySpec(privKeyBytes);
        RSAPrivateKey privKey = (RSAPrivateKey) KeyFactory.getInstance("RSA").generatePrivate(ks);
        keyStore.setKeyEntry(alias, privKey, passwordChars, certs.toArray(new Certificate[certs.size()]));
    }

    private static String wrapCert(String certificate) {
        return "-----BEGIN CERTIFICATE-----\n" + certificate + "\n-----END CERTIFICATE-----";
    }

}
