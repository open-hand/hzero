package org.hzero.starter.keyencrypt.core;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.choerodon.core.exception.CommonException;
import org.hzero.core.base.TokenConstants;
import org.hzero.core.util.EncryptionUtils;
import org.hzero.core.util.TokenUtils;
import org.hzero.starter.keyencrypt.util.EncryptUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * @author xiangyu.qi01@hand-china.com on 2019-11-29.
 */
public class EncryptionService implements IEncryptionService {
    private static final Logger logger = LoggerFactory.getLogger(EncryptionService.class);
    private static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;
    private static final String SEPARATOR = ":";
    private static final String TOKEN_SKIP = TokenConstants.HEADER_BEARER.toLowerCase() + " ";
    private static final int PLAIN_PART_COUNT = 3;      // ID : NAME : ACCESS_TOKEN(10)
    private static final int ACCESS_TOKEN_START = 0;
    private static final int ACCESS_TOKEN_END = 10;

    private final EncryptProperties encryptProperties;
    private ObjectMapper mapper;
    private String cipherPrefix = "=";
    private String cipherSuffix = "=";

    public EncryptionService(EncryptProperties encryptProperties) {
        this.encryptProperties = encryptProperties;
        if (StringUtils.hasText(encryptProperties.getCipherFormat())) {
            String[] split = encryptProperties.getCipherFormat().split("\\%s");
            if (!encryptProperties.getCipherFormat().contains("%s") || split.length > 2 || split.length <= 0) {
                throw new IllegalArgumentException("The cipher text format is illegal : " + encryptProperties.getCipherFormat());
            }
            this.cipherPrefix = split[0];
            if (split.length == 2) {
                this.cipherSuffix = split[1];
            } else {
                this.cipherPrefix = "";
            }
        }
    }

    @Override
    public String decrypt(String value, String tableName, String accessToken, boolean ignoreUserConflict) {
        if (StringUtils.isEmpty(value) || "null".equals(value) || !EncryptContext.isEncrypt()) {
            return value;
        }
        Assert.notNull(tableName, "decrypt key can not be null!");
        Assert.isTrue(isCipher(value), "Is not a cipher : " + value);
        if (StringUtils.isEmpty(value)) {
            return value;
        }
        String decrypt = EncryptionUtils.AES.decryptWithUrlDecoder(unwrap(value), encryptProperties.getSecretKey());
        List<String> decryptKeyList = Arrays.asList(StringUtils.delimitedListToStringArray(decrypt, SEPARATOR));
        if (decryptKeyList.size() != PLAIN_PART_COUNT) {
            handleCheckError(value);
        }
        //校验业务key
        String encryptTableName = decryptKeyList.get(1);
        if (!checkEncryptKey(tableName, encryptTableName)) {
            handleCheckError(value);
        }
        if (!ignoreUserConflict) {
            //校验token
            String encryptToken = decryptKeyList.get(2);
            if (!checkToken(encryptToken, accessToken)) {
                handleCheckError(value);
            }
        }
        return decryptKeyList.get(0);
    }


    /**
     * 加密 id:tableName:token:userId
     *
     * @param id        Byte array to be encrypted
     * @param tableName tableName
     * @return
     */
    @Override
    public String encrypt(String id, String tableName) {
        return encrypt(id, tableName, TokenUtils.getToken());
    }

    @Override
    public String encrypt(String id, String tableName, String accessToken) {
        if (!EncryptContext.isEncrypt()) {
            return id;
        }
        List<String> encryptKeyList = new ArrayList<>(8);
        encryptKeyList.add(id);
        encryptKeyList.add(tableName);
        encryptKeyList.add(getSubToken(accessToken));
        return wrap(EncryptionUtils.AES.encryptWithUrlEncoder(StringUtils.collectionToDelimitedString(encryptKeyList, SEPARATOR), encryptProperties.getSecretKey()));
    }

    /**
     * Custom decrypt for EncryptedJSON class
     *
     * @param parser       JSON parser being used by Jackson
     * @param deserializer Base deserializer being used by
     * @param context      Context for the process of deserialization a single root-level value
     * @return Decrypted object
     */
    @Override
    public Object decrypt(final JsonParser parser, final JsonDeserializer<?> deserializer, final DeserializationContext context, final JavaType javaType, Encrypt encrypt) {
        try {
            if (javaType.isCollectionLikeType() || javaType.isArrayType()) {
                ArrayNode node = parser.getCodec().readTree(parser);
                List<Object> objectList = new ArrayList<>(node.size());
                for (JsonNode jsonNode : node) {
                    if (EncryptUtils.ignoreValue(encrypt, jsonNode.textValue())) {
                        objectList.add(jsonNode.textValue());
                    } else if (BeanUtils.isSimpleValueType(javaType.getContentType().getRawClass())) {
                        objectList.add(decrypt(jsonNode.textValue(), encrypt.value(), encrypt.ignoreUserConflict()));
                    } else {
                        logger.error("@Encrypt annotation is only allowed to add to simple attributes(including its array or collection), current : {} > {}", javaType.getRawClass(), javaType.getContentType().getRawClass());
                        objectList.add(mapper.readValue(jsonNode.traverse(), javaType.getContentType().getRawClass()));
                    }
                }
                return mapper.readValue(mapper.writeValueAsString(objectList), javaType);
            }
            if (javaType.isMapLikeType() && javaType.getKeyType().isFinal()) {
                ObjectNode mapValue = parser.getCodec().readTree(parser);
                Map<Object, Object> value = new HashMap<>(mapValue.size());
                Iterator<String> stringIterator = mapValue.fieldNames();
                while (stringIterator.hasNext()) {
                    String key = stringIterator.next();
                    JsonNode valueNode = mapValue.get(key);
                    if (!EncryptUtils.ignoreValue(encrypt, key) && isCipher(key)) {
                        key = decrypt(key, encrypt.value(), encrypt.ignoreUserConflict());
                    }
                    if (valueNode.isTextual() && !EncryptUtils.ignoreValue(encrypt, valueNode.textValue()) && isCipher(valueNode.textValue())) {
                        value.put(key, decrypt(valueNode.textValue(), encrypt.value(), encrypt.ignoreUserConflict()));
                    } else {
                        value.put(key, valueNode);
                    }
                }
                return mapper.readValue(mapper.writeValueAsString(value), javaType);
            }
            if (StringUtils.isEmpty(parser.getValueAsString())) {
                return null;
            }
            String decrypt = parser.getValueAsString();
            if (!EncryptUtils.ignoreValue(encrypt, decrypt)) {
                decrypt = decrypt(parser.getValueAsString(), encrypt.value(), encrypt.ignoreUserConflict());
            }
            if (deserializer != null) {
                return deserializer.deserialize(mapper.getFactory()
                        .createParser(decrypt.getBytes(DEFAULT_CHARSET)), context);
            }
            return mapper.readValue(decrypt, javaType);
        } catch (Exception e) {
            throw new CommonException("Unable to decrypt document", e);
        }
    }

    private String wrap(String content) {
        return this.cipherPrefix + content + this.cipherSuffix;
    }

    private String unwrap(String content) {
        return content.substring(cipherPrefix.length(), content.length() - cipherSuffix.length());
    }

    @Override
    public boolean isCipher(String content) {
        return content.startsWith(cipherPrefix) && content.endsWith(cipherSuffix);
    }

    @Override
    public void setObjectMapper(ObjectMapper objectMapper) {
        this.mapper = objectMapper;
    }

    protected boolean checkEncryptKey(String tableName, String encryptTableName) {
        if (tableName.equalsIgnoreCase(encryptTableName)
                || IGNORE_DECRYPT.equals(encryptTableName)) {
            return true;
        }
        if (IGNORE_DECRYPT.equals(tableName)) {
            return true;
        }
        return false;
    }

    protected boolean checkToken(String encryptToken, String accessToken) {
        String token = getSubToken(accessToken);
        return token.equals(encryptToken);
    }

    protected String getSubToken(String token) {
        if (StringUtils.hasText(token)) {
            if (token.toLowerCase().startsWith(TOKEN_SKIP)) {
                token = token.substring(TOKEN_SKIP.length());
            }
            token = org.apache.commons.lang3.StringUtils.substring(token.replace("-", ""), ACCESS_TOKEN_START, ACCESS_TOKEN_END);
        } else {
            token = NO_TOKEN;
        }
        return token;
    }

    protected void handleCheckError(String value) {
        throw new CommonException("decrypt error ! decrypt key = " + value);
    }
}
