package org.hzero.starter.social.apple.api;

import java.io.UnsupportedEncodingException;
import java.security.PublicKey;
import java.util.Map;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.auth0.jwk.Jwk;
import io.jsonwebtoken.*;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.RestTemplate;

import org.hzero.starter.social.core.common.api.AbstractSocialApi;
import org.hzero.starter.social.core.exception.CommonSocialException;
import org.hzero.starter.social.core.exception.ProviderUserNotFoundException;
import org.hzero.starter.social.core.exception.SocialErrorCode;
import org.hzero.starter.social.core.provider.Provider;

/**
 * @Copyright (c) 2020. Hand Enterprise Solution Company. All right reserved.
 * @ProjectName: hzero-starter-social-parent
 * @PackageName: org.hzero.starter.social.apple.api
 * @Date: 2020/4/27
 * @author: ajisun
 * @Email: jiguang.sun@hand-china.com
 */
public class DefaultAppleApi extends AbstractSocialApi implements AppleApi {

    private static final Logger logger = LoggerFactory.getLogger(DefaultAppleApi.class);
    private static final String AUTH_TIME = "auth_time";

    private static final RestTemplate restTemplate = new RestTemplate();

    private String openIdUrl;
    private String identityToken;
    private String openId;
    private String email;


    public DefaultAppleApi(String accessToken, Provider provider) {
        super(accessToken);
        this.identityToken = accessToken;
        this.openIdUrl = provider.getOpenIdUrl();
    }

    @Override
    public AppleUser getUser() {

        if (!isAuthorized()) {
            throw new CommonSocialException(SocialErrorCode.SOCIAL_USER_NOT_AUTHORIZED);
        }

        AppleUser user = new AppleUser();


        if (!this.appleVerify(identityToken, openIdUrl)) {
            logger.info("not found provider user, result user={}", user);
            throw new ProviderUserNotFoundException(SocialErrorCode.PROVIDER_USER_NOT_FOUND);
        }
        user.setOpenId(openId);
        user.setEmail(email);
        user.setUserName(email);
        return user;

    }



    private PublicKey getPublicKey(String kid, String appKeysUrl) {
        String keys = restTemplate.getForObject(appKeysUrl, String.class);
        if (StringUtils.isEmpty(keys)) {
            return null;
        }

        JSONObject jsonObject = JSONObject.parseObject(keys);
        JSONArray array = jsonObject.getJSONArray("keys");

        JSONObject keyJson = new JSONObject();
        for (int i = 0; i < array.size(); i++) {
            JSONObject jsonObject1 = array.getJSONObject(i);
            if (kid.equals(jsonObject1.getString("kid"))) {
                keyJson = jsonObject1;
                break;
            }

        }
        Jwk jwa = Jwk.fromValues(keyJson);
        PublicKey publicKey = null;
        try {
            publicKey = jwa.getPublicKey();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return publicKey;

    }


    private boolean appleVerify(String identifyToken, String appKeysUrl) {

        try {
            String[] identityTokens = identifyToken.split("\\.");
            if (identityTokens.length<2){
                throw new CommonSocialException("hoth.social.apple.identifyTokenNotRight");
            }
            Map header = JSONObject.parseObject(new String(Base64.decodeBase64(identityTokens[0]), "UTF-8"));
            Map payload = JSONObject.parseObject(new String(Base64.decodeBase64(identityTokens[1]), "UTF-8"));

            String kid = String.valueOf(header.get("kid"));
            String aud = String.valueOf(payload.get("aud"));
            String sub = String.valueOf(payload.get("sub"));
            String iss = String.valueOf(payload.get("iss"));
            openId = sub;
            email = String.valueOf(payload.get("email"));

            PublicKey publicKey = getPublicKey(kid, appKeysUrl);
            JwtParser jwtParser = Jwts.parser().setSigningKey(publicKey);
            jwtParser.requireIssuer(iss);
            jwtParser.requireAudience(aud);
            jwtParser.requireSubject(sub);

            try {
                Jws<Claims> claim = jwtParser.parseClaimsJws(identifyToken);

                if (claim != null && claim.getBody().containsKey(AUTH_TIME)) {
                    return true;
                }
                return false;
            } catch (ExpiredJwtException e) {
                logger.error("apple identityToken expired ", e);
                throw new CommonSocialException("hoth.social.apple.identifyTokenExpired");
            } catch (Exception e) {
                logger.error("apple identityToken illegal ", e);
                return false;
            }

        } catch (UnsupportedEncodingException e) {
            logger.error("apple identityToken Base64 ", e);
            return false;
        }
    }


}
