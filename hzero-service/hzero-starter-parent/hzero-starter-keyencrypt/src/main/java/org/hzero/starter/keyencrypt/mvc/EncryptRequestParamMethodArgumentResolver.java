package org.hzero.starter.keyencrypt.mvc;

import org.hzero.starter.keyencrypt.core.Encrypt;
import org.hzero.starter.keyencrypt.core.EncryptContext;
import org.hzero.starter.keyencrypt.core.IEncryptionService;
import org.hzero.starter.keyencrypt.util.EncryptUtils;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.core.MethodParameter;
import org.springframework.lang.Nullable;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.annotation.RequestParamMethodArgumentResolver;

import java.util.Collection;

/**
 * @author xiangyu.qi01@hand-china.com on 2019-12-02.
 */
public class EncryptRequestParamMethodArgumentResolver extends RequestParamMethodArgumentResolver {

    IEncryptionService encryptionService;

    public EncryptRequestParamMethodArgumentResolver(IEncryptionService encryptionService, boolean useDefaultResolution) {
        super(useDefaultResolution);
        this.encryptionService = encryptionService;
    }

    public EncryptRequestParamMethodArgumentResolver(IEncryptionService encryptionService, @Nullable ConfigurableBeanFactory beanFactory,
                                                     boolean useDefaultResolution) {
        super(beanFactory, useDefaultResolution);
        this.encryptionService = encryptionService;
    }

    @Override
    @Nullable
    protected Object resolveName(String name, MethodParameter parameter, NativeWebRequest request) throws Exception {
        Object result = super.resolveName(name, parameter, request);
        if (EncryptContext.isEncrypt() && result != null && parameter.hasParameterAnnotation(Encrypt.class)) {
            Encrypt encrypt = parameter.getParameterAnnotation(Encrypt.class);
            if (result instanceof String && !EncryptUtils.ignoreValue(encrypt, (String) result)) {
                if (isArray(parameter)) {
                    result = ((String) result).split(",");
                } else {
                    result = encryptionService.decrypt(((String) result), encrypt.value(), encrypt.ignoreUserConflict());
                    return result;
                }
            }
            if (result instanceof String[]) {
                String[] oldResult = (String[]) result;
                String[] newResult = new String[oldResult.length];
                for (int i = 0; i < oldResult.length; i++) {
                    if (EncryptUtils.ignoreValue(encrypt, oldResult[i])) {
                        newResult[i] = oldResult[i];
                    } else {
                        newResult[i] = encryptionService.decrypt(oldResult[i], encrypt.value(), encrypt.ignoreUserConflict());
                    }
                }
                return newResult;
            }
        }
        return result;
    }

    private boolean isArray(MethodParameter parameter) {
        return Collection.class.isAssignableFrom(parameter.getParameterType())
                || parameter.getParameterType().isArray();
    }
}
