package org.hzero.starter.keyencrypt.mvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.choerodon.core.convertor.ApplicationContextHelper;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.hzero.starter.keyencrypt.core.Encrypt;
import org.hzero.starter.keyencrypt.core.EncryptContext;
import org.hzero.starter.keyencrypt.core.IEncryptionService;
import org.hzero.starter.keyencrypt.util.EncryptUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.core.MethodParameter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.lang.Nullable;
import org.springframework.web.accept.ContentNegotiationManager;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.servlet.mvc.method.annotation.RequestResponseBodyMethodProcessor;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author xiangyu.qi01@hand-china.com on 2020-06-12.
 */
public class EncryptRequestResponseBodyMethodProcessor extends RequestResponseBodyMethodProcessor {

    protected ObjectMapper mapper;

    protected IEncryptionService encryptionService;

    public EncryptRequestResponseBodyMethodProcessor(List<HttpMessageConverter<?>> converters) {
        super(converters);
    }

    public EncryptRequestResponseBodyMethodProcessor(List<HttpMessageConverter<?>> converters, ContentNegotiationManager manager) {
        super(converters, manager);
    }

    public EncryptRequestResponseBodyMethodProcessor(List<HttpMessageConverter<?>> converters, List<Object> requestResponseBodyAdvice) {
        super(converters, requestResponseBodyAdvice);
    }

    public EncryptRequestResponseBodyMethodProcessor(List<HttpMessageConverter<?>> converters, ContentNegotiationManager manager, List<Object> requestResponseBodyAdvice) {
        super(converters, manager, requestResponseBodyAdvice);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, @Nullable ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest, @Nullable WebDataBinderFactory binderFactory) throws Exception {
        if (!EncryptContext.isEncrypt()) {
            return super.resolveArgument(parameter, mavContainer, webRequest, binderFactory);
        }
        Encrypt encrypt = parameter.getParameterAnnotation(Encrypt.class);
        if (encrypt == null) {
            return super.resolveArgument(parameter, mavContainer, webRequest, binderFactory);
        }
        // 如果是集合类，且范型类型是基础类型的包装类型
        if (Collection.class.isAssignableFrom(parameter.getParameterType())) {
            return resolveCollectionArgument(parameter, mavContainer, webRequest, binderFactory, encrypt);
        }
        // 如果是集合类，且范型类型是基础类型的包装类型
        if (parameter.getParameterType().isArray()) {
            return resolveArrayArgument(parameter, mavContainer, webRequest, binderFactory, encrypt);
        }
        // 如果是 Map 类，且泛型类型是基础类型顶的包装类型
        if (Map.class.isAssignableFrom(parameter.getParameterType())) {
            return resolveMapArgument(parameter, mavContainer, webRequest, binderFactory, encrypt);
        }
        return super.resolveArgument(parameter, mavContainer, webRequest, binderFactory);
    }

    private Object resolveArrayArgument(MethodParameter parameter,
                                        @Nullable ModelAndViewContainer mavContainer,
                                        NativeWebRequest webRequest,
                                        @Nullable WebDataBinderFactory binderFactory,
                                        Encrypt encrypt) throws Exception {
        return encryptMultiArgument(parameter, mavContainer, webRequest, binderFactory, encrypt, parameter.getParameterType().getComponentType());
    }

    private Object encryptMultiArgument(MethodParameter parameter, @Nullable ModelAndViewContainer mavContainer, NativeWebRequest webRequest, @Nullable WebDataBinderFactory binderFactory, Encrypt encrypt, Class clz) throws Exception {
        if (isSimple(clz)) {
            //重写request的body,解密
            HttpServletRequest httpServletRequest = webRequest.getNativeRequest(HttpServletRequest.class);
            HttpServletResponse httpServletResponse = webRequest.getNativeRequest(HttpServletResponse.class);
            String body = getBodyFromRequest(httpServletRequest);
            if (StringUtils.isBlank(body)) {
                WrapperWebRequest wrapperWebRequest = new WrapperWebRequest(new ModifyBodyHttpServletRequestWrapper
                        (httpServletRequest, ""), httpServletResponse);
                return super.resolveArgument(parameter, mavContainer, wrapperWebRequest, binderFactory);
            }
            List<Object> encryptList = getMapper().readValue(body, List.class);
            List<Object> decryptList = Collections.emptyList();
            if (!CollectionUtils.isEmpty(encryptList)) {
                decryptList = encryptList.stream().map(t ->
                        (t instanceof String && !EncryptUtils.ignoreValue(encrypt, (String) t)) ? encryptionService.decrypt((String) t, encrypt.value(), encrypt.ignoreUserConflict()) : t)
                        .collect(Collectors.toList());
            }
            WrapperWebRequest wrapperWebRequest = new WrapperWebRequest(new ModifyBodyHttpServletRequestWrapper
                    (httpServletRequest, getMapper().writeValueAsString(decryptList)), httpServletResponse);
            return super.resolveArgument(parameter, mavContainer, wrapperWebRequest, binderFactory);
        }
        return super.resolveArgument(parameter, mavContainer, webRequest, binderFactory);
    }

    private Object resolveMapArgument(MethodParameter parameter,
                                      @Nullable ModelAndViewContainer mavContainer,
                                      NativeWebRequest webRequest,
                                      @Nullable WebDataBinderFactory binderFactory,
                                      Encrypt encrypt) throws Exception {
        Type type = parameter.getGenericParameterType();
        if (type instanceof ParameterizedType) {
            Type[] params = ((ParameterizedType) type).getActualTypeArguments();
            if (params.length != 2) {
                return super.resolveArgument(parameter, mavContainer, webRequest, binderFactory);
            }
            boolean decryptKey = isSimple((Class) params[0]);
            boolean decryptValue = isSimple((Class) params[1]);
            if (decryptKey || decryptValue) {
                //重写request的body,解密
                HttpServletRequest httpServletRequest = webRequest.getNativeRequest(HttpServletRequest.class);
                HttpServletResponse httpServletResponse = webRequest.getNativeRequest(HttpServletResponse.class);
                String body = getBodyFromRequest(httpServletRequest);
                if (StringUtils.isBlank(body)) {
                    WrapperWebRequest wrapperWebRequest = new WrapperWebRequest(new ModifyBodyHttpServletRequestWrapper
                            (httpServletRequest, body), httpServletResponse);
                    return super.resolveArgument(parameter, mavContainer, wrapperWebRequest, binderFactory);
                }
                Map<Object, Object> encryptMap = getMapper().readValue(body, Map.class);
                if (MapUtils.isEmpty(encryptMap)) {
                    WrapperWebRequest wrapperWebRequest = new WrapperWebRequest(new ModifyBodyHttpServletRequestWrapper
                            (httpServletRequest, body), httpServletResponse);
                    return super.resolveArgument(parameter, mavContainer, wrapperWebRequest, binderFactory);
                }
                Map<Object, Object> decryptMap = new HashMap<>(encryptMap.size());
                encryptMap.forEach((k, v) -> {
                    if (decryptKey
                            && k instanceof String
                            && !EncryptUtils.ignoreValue(encrypt, (String) k)
                            && encryptionService.isCipher((String) k)) {
                        k = encryptionService.decrypt((String) k, encrypt.value(), encrypt.ignoreUserConflict());
                    }
                    if (decryptValue
                            && v instanceof String
                            && !EncryptUtils.ignoreValue(encrypt, (String) v)
                            && encryptionService.isCipher((String) v)) {
                        v = encryptionService.decrypt((String) v, encrypt.value(), encrypt.ignoreUserConflict());
                    }
                    decryptMap.put(k, v);
                });
                WrapperWebRequest wrapperWebRequest = new WrapperWebRequest(new ModifyBodyHttpServletRequestWrapper
                        (httpServletRequest, getMapper().writeValueAsString(decryptMap)), httpServletResponse);
                return super.resolveArgument(parameter, mavContainer, wrapperWebRequest, binderFactory);
            }
        }
        return super.resolveArgument(parameter, mavContainer, webRequest, binderFactory);
    }

    private Object resolveCollectionArgument(MethodParameter parameter,
                                             @Nullable ModelAndViewContainer mavContainer,
                                             NativeWebRequest webRequest,
                                             @Nullable WebDataBinderFactory binderFactory,
                                             Encrypt encrypt) throws Exception {
        Type type = parameter.getGenericParameterType();
        if (type instanceof ParameterizedType) {
            Type[] params = ((ParameterizedType) type).getActualTypeArguments();
            if (params.length > 1) {
                return super.resolveArgument(parameter, mavContainer, webRequest, binderFactory);
            }
            return encryptMultiArgument(parameter, mavContainer, webRequest, binderFactory, encrypt, (Class) params[0]);
        }
        return super.resolveArgument(parameter, mavContainer, webRequest, binderFactory);
    }


    public void setEncryptionService(IEncryptionService encryptionService) {
        this.encryptionService = encryptionService;
    }

    /**
     * 延迟加载
     *
     * @return
     */
    public ObjectMapper getMapper() {
        if (mapper != null) {
            return mapper;
        }
        synchronized (this) {
            if (mapper != null) {
                return mapper;
            }
            mapper = ApplicationContextHelper.getContext().getBean(ObjectMapper.class);
        }
        return mapper;
    }

    protected String getBodyFromRequest(HttpServletRequest request) {

        if (request == null) {
            return null;
        }

        StringBuilder sb = new StringBuilder();
        InputStream inputStream = null;
        BufferedReader reader = null;
        try {
            inputStream = request.getInputStream();
            reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
            String line = "";
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
        } catch (IOException e) {
            logger.error("Error read body form request.", e);
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    //
                }
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    //
                }
            }
        }
        return sb.toString();
    }


    protected boolean isSimple(Class clz) {
        return BeanUtils.isSimpleProperty(clz) || Object.class.equals(clz);
    }

    public class WrapperWebRequest extends ServletWebRequest {

        public WrapperWebRequest(HttpServletRequest request) {
            super(request);
        }

        public WrapperWebRequest(HttpServletRequest request, HttpServletResponse response) {
            super(request, response);
        }
    }

    /**
     * 重写request，获取body数据的时候读取新的body
     **/
    public class ModifyBodyHttpServletRequestWrapper extends HttpServletRequestWrapper {

        // 重新赋值的body数据
        private String bodyJsonStr;

        public ModifyBodyHttpServletRequestWrapper(HttpServletRequest request, String bodyJsonStr) {
            super(request);
            this.bodyJsonStr = bodyJsonStr;
        }

        @Override
        public ServletInputStream getInputStream() throws IOException {
            if (StringUtils.isEmpty(bodyJsonStr)) {
                bodyJsonStr = "";
            }
            // 必须指定utf-8编码，否则json请求数据中如果包含中文，会出现异常
            final ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bodyJsonStr.getBytes(StandardCharsets.UTF_8));
            ServletInputStream servletInputStream = new ServletInputStream() {
                @Override
                public boolean isFinished() {
                    return false;
                }

                @Override
                public boolean isReady() {
                    return false;
                }

                @Override
                public void setReadListener(ReadListener readListener) {
                }

                @Override
                public int read() throws IOException {
                    return byteArrayInputStream.read();
                }
            };
            return servletInputStream;
        }

        @Override
        public BufferedReader getReader() throws IOException {
            return new BufferedReader(new InputStreamReader(this.getInputStream()));
        }

        public String getBodyJsonStr() {
            return bodyJsonStr;
        }

        public void setBodyJsonStr(String bodyJsonStr) {
            this.bodyJsonStr = bodyJsonStr;
        }
    }
}
