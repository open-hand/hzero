package org.hzero.starter.keyencrypt.mvc;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.hzero.starter.keyencrypt.core.Encrypt;
import org.hzero.starter.keyencrypt.core.EncryptContext;
import org.hzero.starter.keyencrypt.core.IEncryptionService;
import org.hzero.starter.keyencrypt.util.EncryptUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.util.Assert;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.mvc.method.annotation.ServletModelAttributeMethodProcessor;

import javax.servlet.ServletRequest;
import javax.servlet.ServletRequestWrapper;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author xiangyu.qi01@hand-china.com on 2020-02-09.
 */
public class EncryptServletModelAttributeMethodProcessor extends ServletModelAttributeMethodProcessor {

    @Autowired
    IEncryptionService encryptionService;

    public EncryptServletModelAttributeMethodProcessor() {
        super(false);
    }

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(Encrypt.class)
                && !BeanUtils.isSimpleProperty(parameter.getParameterType());
    }


    /**
     * This implementation downcasts {@link WebDataBinder} to
     * {@link ServletRequestDataBinder} before binding.
     */
    @Override
    protected void bindRequestParameters(WebDataBinder binder, NativeWebRequest request) {
        if (!EncryptContext.isEncrypt()) {
            super.bindRequestParameters(binder, request);
            return;
        }
        ServletRequest servletRequest = request.getNativeRequest(ServletRequest.class);
        Assert.state(servletRequest != null, "No ServletRequest");
        ServletRequestDataBinder servletBinder = (ServletRequestDataBinder) binder;

        Field[] fields = FieldUtils.getFieldsWithAnnotation(binder.getTarget().getClass(), Encrypt.class);
        if (ArrayUtils.isNotEmpty(fields)) {
            ParameterRequestWrapper requestWrapper = new ParameterRequestWrapper(servletRequest);
            String attr = HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE;
            Map<String, String> uriVars = (Map<String, String>) servletRequest.getAttribute(attr);
            for (Field field : fields) {
                Encrypt encrypt = field.getAnnotation(Encrypt.class);
                //解密
                String encryptId = merge(requestWrapper.getParameterValues(field.getName()));
                if (StringUtils.isNotEmpty(encryptId)) {
                    if (Collection.class.isAssignableFrom(field.getType()) || field.getType().isArray()) {
                        requestWrapper.addParameter(field.getName(), Arrays.stream(encryptId.split(","))
                                .map(item -> EncryptUtils.ignoreValue(encrypt, item) ? item : encryptionService.decrypt(item, encrypt.value(), encrypt.ignoreUserConflict()))
                                .collect(Collectors.joining(",")));
                    } else {
                        encryptId = EncryptUtils.ignoreValue(encrypt, encryptId) ? encryptId : encryptionService.decrypt(encryptId, encrypt.value(), encrypt.ignoreUserConflict());
                        requestWrapper.addParameter(field.getName(), encryptId);
                    }
                } else if (uriVars != null && uriVars.containsKey(field.getName())) {
                    encryptId = uriVars.get(field.getName());
                    if (encryptId != null && (Collection.class.isAssignableFrom(field.getType()) || field.getType().isArray())) {
                        uriVars.put(field.getName(), Arrays.stream(encryptId.split(","))
                                .map(item -> EncryptUtils.ignoreValue(encrypt, item) ? item : encryptionService.decrypt(item, encrypt.value(), encrypt.ignoreUserConflict()))
                                .collect(Collectors.joining(",")));
                    } else {
                        encryptId = EncryptUtils.ignoreValue(encrypt, encryptId) ? encryptId : encryptionService.decrypt(encryptId, encrypt.value(), encrypt.ignoreUserConflict());
                        uriVars.put(field.getName(), encryptId);
                    }
                }
            }
            servletBinder.bind(requestWrapper);
        } else {
            servletBinder.bind(servletRequest);
        }
    }

    private String merge(String[] parameterValues) {
        if (parameterValues == null) {
            return null;
        }
        if (parameterValues.length == 0) {
            return "";
        }
        StringBuilder sb = new StringBuilder(parameterValues[0]);
        for (int i = 1; i < parameterValues.length; i++) {
            sb.append(',').append(parameterValues[i]);
        }
        return sb.toString();
    }

    public class ParameterRequestWrapper extends ServletRequestWrapper {

        private Map<String, String[]> params = new HashMap<>();

        @SuppressWarnings("unchecked")
        public ParameterRequestWrapper(ServletRequest request) {
            // 将request交给父类，以便于调用对应方法的时候，将其输出，其实父亲类的实现方式和第一种new的方式类似
            super(request);
            //将参数表，赋予给当前的Map以便于持有request中的参数
            this.params.putAll(request.getParameterMap());
        }

        public ParameterRequestWrapper(ServletRequest request, Map<String, Object> extendParams) {
            this(request);
            addAllParameters(extendParams);//这里将扩展参数写入参数表
        }

        @Override
        public String getParameter(String name) {//重写getParameter，代表参数从当前类中的map获取
            String[] values = params.get(name);
            if (values == null || values.length == 0) {
                return null;
            }
            return values[0];
        }

        @Override
        public String[] getParameterValues(String name) {//同上
            return params.get(name);
        }

        public void addAllParameters(Map<String, Object> otherParams) {//增加多个参数
            for (Map.Entry<String, Object> entry : otherParams.entrySet()) {
                addParameter(entry.getKey(), entry.getValue());
            }
        }


        public void addParameter(String name, Object value) {//增加参数
            if (value != null) {
                if (value instanceof String[]) {
                    params.put(name, (String[]) value);
                } else if (value instanceof String) {
                    params.put(name, new String[]{(String) value});
                } else {
                    params.put(name, new String[]{String.valueOf(value)});
                }
            }
        }
    }


}
