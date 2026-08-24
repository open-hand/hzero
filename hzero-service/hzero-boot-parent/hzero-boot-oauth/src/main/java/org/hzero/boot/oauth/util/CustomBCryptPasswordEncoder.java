package org.hzero.boot.oauth.util;

import java.security.SecureRandom;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * 修改自BCryptPasswordEncoder
 * 数据库oauth_client的secret存储为明文，升级之后要求为秘文存储
 * 因此如果match不到，则先加密再次进行匹配
 *
 * @author flyleft
 * @since 0.15.0
 */
public class CustomBCryptPasswordEncoder implements PasswordEncoder {

    private static final Pattern BCRYPT_PATTERN = Pattern.compile("\\A\\$2a?\\$\\d\\d\\$[./0-9A-Za-z]{53}");

    private static final int MIN_LOG_ROUNDS = 4;

    private static final int MAX_LOG_ROUNDS = 31;

    private final Logger log = LoggerFactory.getLogger(CustomBCryptPasswordEncoder.class);

    private final int strength;

    private final SecureRandom random;

    public CustomBCryptPasswordEncoder() {
        this(-1);
    }

    public CustomBCryptPasswordEncoder(int strength) {
        this(strength, null);
    }

    public CustomBCryptPasswordEncoder(int strength, SecureRandom random) {
        if (strength != -1 && (strength < MIN_LOG_ROUNDS || strength > MAX_LOG_ROUNDS)) {
            throw new IllegalArgumentException("Bad strength");
        }
        this.strength = strength;
        this.random = random;
    }

    @Override
    public String encode(CharSequence rawPassword) {
        String salt;
        if (strength > 0) {
            if (random != null) {
                salt = BCrypt.gensalt(strength, random);
            } else {
                salt = BCrypt.gensalt(strength);
            }
        } else {
            salt = BCrypt.gensalt();
        }
        return BCrypt.hashpw(rawPassword.toString(), salt);
    }

    @Override
    public boolean matches(CharSequence rawPassword, String encodedPassword) {
        if (rawPassword == null || encodedPassword == null || encodedPassword.length() == 0) {
            log.warn("Empty encoded password");
            return false;
        }
        if (BCRYPT_PATTERN.matcher(encodedPassword).matches()) {
            return BCrypt.checkpw(rawPassword.toString(), encodedPassword);
        } else {
            return rawPassword.toString().equals(encodedPassword);
        }
    }
}
