package org.hzero.starter.keyencrypt.mvc;

import org.hzero.starter.keyencrypt.core.Encrypt;
import org.hzero.starter.keyencrypt.core.EncryptContext;
import org.hzero.starter.keyencrypt.core.IEncryptionService;
import org.hzero.starter.keyencrypt.util.EncryptUtils;
import org.springframework.core.MethodParameter;
import org.springframework.lang.Nullable;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.servlet.mvc.method.annotation.PathVariableMethodArgumentResolver;

/**
 * @author xiangyu.qi01@hand-china.com on 2019-12-02.
 */
public class EncryptPathVariableMethodArgumentResolver extends PathVariableMethodArgumentResolver {

    IEncryptionService encryptionService;


    public EncryptPathVariableMethodArgumentResolver(IEncryptionService encryptionService) {
        super();
        this.encryptionService = encryptionService;
    }

    @Override
    @SuppressWarnings("unchecked")
    @Nullable
    protected Object resolveName(String name, MethodParameter parameter, NativeWebRequest request) throws Exception {
        Object value = super.resolveName(name, parameter, request);
        Encrypt encrypt = parameter.getParameterAnnotation(Encrypt.class);
        if (EncryptContext.isEncrypt()
                && value != null
                && value instanceof String
                && parameter.hasParameterAnnotation(Encrypt.class)
                && !EncryptUtils.ignoreValue(encrypt, (String) value)) {
            value = encryptionService.decrypt((String) value, encrypt.value(), encrypt.ignoreUserConflict());
        }
        return value;
    }

}
