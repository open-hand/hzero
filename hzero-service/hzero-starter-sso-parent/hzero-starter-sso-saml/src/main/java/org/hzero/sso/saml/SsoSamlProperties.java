package org.hzero.sso.saml;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = SsoSamlProperties.PREFIX)
public class SsoSamlProperties {

    public static final String PREFIX = "hzero.oauth.sso.saml";

    private String entityId = "hzero:org:sp";

    private String passphrase = "secret";

    private String privateKey = "MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQDQRsCf6qU0DYkoFIJUhNlMxJFKMPsEvK+u3rcaBIZcyWX4Cv5OU3xtcCAg6mqRwMRFYFDNdGgR0XLTaHAOcJpR7cXYnYc0Wa6Kh8KSstgZrCl+WdqCtuUS6bMrrAdSq6HpoAPwo1JyOqyC9ccRZ9ysjhYdWQS1ELSjUHjEuxxRoEgwKfrF7kxbo89dixQ7oF9E9CgeWtftJfMtDxGtMhmtaIurHjjcfOPWR8TND0b1Lp1pLkzPn6GdI2aRWqV3tNsljKoXP9omDInhP9xrzoLiXISbekfnLrfFQW+rmylCBiu6ZqKv0weg1V6b7B6rAyV3nIcXInLuKXSlQx0nnPOdAgMBAAECggEAOrAlKSqyYIeL5XpZ+zzwCly9X/2LThtpGcpyJ+esgMrTa+CVJjcKMcBNnVjQrL93zuDEBBDQHm05gO7F3JvIMFviyxYgehTnROvaXQH+OMW1b4AcPYcR55Foxl6UNaxdVHqdgZpT6hI0eDaPYI02tnzXKG/kDq1laTuMvErJQQp6Cd611yyAhBvpX1ibpAYvex10sfTkj0GRKmOrGqwVXibN29szaRei7Xeg/RStdVBgrYJoR5/4++dkGapa27oRdOh4VJUChRfXuJtH6pyxC7uay1fMRcmo2u6NcWAT6qMOvxLcuesnNFrbSlPoZaxWNiZRX/SVqeieyRAA0WS7IQKBgQDoywh4DkdL+SPrkA/sB0rOQF3kJjlzWibk9OM17In1P+obQk37kSRYKfBvsk48VWdG1fN33Up05Pxe+f36F//AZ8mp7uTmBtd6CAoR/005WxwkCSihF6LaDiB3VtxlpcfRA/TUZ10PMud43w0AeG30AG0KpCokfIiY87OpyTjJWQKBgQDlCgsgZ9rL3Wm7FbEDZ4f2uTB5rlT0Vz80paV0OOJdUQECrZW1PjemQpqIJocr8yoNupkrZKPSi4mbNoMFF1wXIydOjLq6iQ6KWIKRdsvmeXL++tWg6TiD8nDpBxuKzjRhwMcQN2lakb/SusoXnmG8qq12PCFUvpbhoZRqRPWv5QKBgQC8jUasxxPka0U21RawXC+w4t2pn3RFBC4goGEwGgibxkr+DTRQoHzJlB6Uud04bQwbicuLuIdIKvhmjSGzYaDa3LWwmDh6P+xjgQN3FEweOreOUITCBfz3lR2iy430HtS7bPLu31G2r8pgUnmbee/FBFtNlS41I1EYYbuRt9Pw8QKBgD6aPSpRWKtqTHD3X9e3X6FfQtGvhcb3Ze5E7HFU7wJklqsduRK9+8X05HocVcv8fd0cyKrkqiZtP2JuRueIWAJ2+FJvAsbjmVbVFHMgDmFjhrwM4YFG3cyq4pO+/pc0/3pMj9xt2N0Jg23c4koMX1iLKjhr/QxFv8XSPVfCm4jFAoGALfejdx4PpFgTWpbm5ZWRxukhZRhmfCIAWifYeJbsGTB5y7bheVxKmTpP9mKEqGL+gh3cLVPcZ557HWpc4d6NetdyrHffEhWULh4NWYDKC5BRCr9HjLKydBUQUMCFeJs3XZQTtN+CZORcuaI2ISH2QvfYki9ns4ujeH8OjzfHpvI=";

    private String certificate = "MIIDEzCCAfugAwIBAgIJAKoK/heBjcOYMA0GCSqGSIb3DQEBBQUAMCAxHjAcBgNVBAoMFU9yZ2FuaXphdGlvbiwgQ049T0lEQzAeFw0xNTExMTExMDEyMTVaFw0yNTExMTAxMDEyMTVaMCAxHjAcBgNVBAoMFU9yZ2FuaXphdGlvbiwgQ049T0lEQzCCASIwDQYJKoZIhvcNAQEBBQADggEPADCCAQoCggEBANBGwJ/qpTQNiSgUglSE2UzEkUow+wS8r67etxoEhlzJZfgK/k5TfG1wICDqapHAxEVgUM10aBHRctNocA5wmlHtxdidhzRZroqHwpKy2BmsKX5Z2oK25RLpsyusB1KroemgA/CjUnI6rIL1xxFn3KyOFh1ZBLUQtKNQeMS7HFGgSDAp+sXuTFujz12LFDugX0T0KB5a1+0l8y0PEa0yGa1oi6seONx849ZHxM0PRvUunWkuTM+foZ0jZpFapXe02yWMqhc/2iYMieE/3GvOguJchJt6R+cut8VBb6ubKUIGK7pmoq/TB6DVXpvsHqsDJXechxcicu4pdKVDHSec850CAwEAAaNQME4wHQYDVR0OBBYEFK7RqjoodSYVXGTVEdLf3kJflP/sMB8GA1UdIwQYMBaAFK7RqjoodSYVXGTVEdLf3kJflP/sMAwGA1UdEwQFMAMBAf8wDQYJKoZIhvcNAQEFBQADggEBADNZkxlFXh4F45muCbnQd+WmaXlGvb9tkUyAIxVL8AIu8J18F420vpnGpoUAE+Hy3evBmp2nkrFAgmr055fAjpHeZFgDZBAPCwYd3TNMDeSyMta3Ka+oS7GRFDePkMEm+kH4/rITNKUF1sOvWBTSowk9TudEDyFqgGntcdu/l/zRxvx33y3LMG5USD0x4X4IKjRrRN1BbcKgi8dq10C3jdqNancTuPoqT3WWzRvVtB/q34B7F74/6JzgEoOCEHufBMp4ZFu54P0yEGtWfTwTzuoZobrChVVBt4w/XZagrRtUCDNwRpHNbpjxYudbqLqpi1MQpV9oht/BpTHVJG2i0ro=";

    private String metadataDisplayProcessUrl = "/saml/metadata";

    public String getPassphrase() {
        return passphrase;
    }

    public void setPassphrase(String passphrase) {
        this.passphrase = passphrase;
    }

    public String getPrivateKey() {
        return privateKey;
    }

    public void setPrivateKey(String privateKey) {
        this.privateKey = privateKey;
    }

    public String getCertificate() {
        return certificate;
    }

    public void setCertificate(String certificate) {
        this.certificate = certificate;
    }

    public String getEntityId() {
        return entityId;
    }

    public void setEntityId(String entityId) {
        this.entityId = entityId;
    }

    public String getMetadataDisplayProcessUrl() {
        return metadataDisplayProcessUrl;
    }

    public void setMetadataDisplayProcessUrl(String metadataDisplayProcessUrl) {
        this.metadataDisplayProcessUrl = metadataDisplayProcessUrl;
    }

}
