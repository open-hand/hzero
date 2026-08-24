/**
 * Copyright 2012-2018 The Feign Authors
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package feign;

import static feign.FeignException.errorExecuting;
import static feign.FeignException.errorReading;
import static feign.Util.checkNotNull;
import static feign.Util.ensureClosed;

import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;
import java.util.concurrent.TimeUnit;

import javax.servlet.http.HttpServletRequest;

import feign.InvocationHandlerFactory.MethodHandler;
import feign.Request.Options;
import feign.codec.DecodeException;
import feign.codec.Decoder;
import feign.codec.ErrorDecoder;
import org.apache.commons.lang3.StringUtils;
import org.hzero.core.properties.CoreProperties;
import org.hzero.feign.init.MappingRegister;
import org.springframework.beans.BeanUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.common.exceptions.InvalidTokenException;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationDetails;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.method.HandlerMethod;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.choerodon.core.convertor.ApplicationContextHelper;
import io.choerodon.core.exception.CommonException;
import io.choerodon.core.oauth.CustomTokenConverter;
import io.choerodon.resource.filter.JwtTokenExtractor;

final class SynchronousMethodHandler implements MethodHandler {

    private static final long MAX_RESPONSE_BUFFER_SIZE = 8192L;

    private final MethodMetadata metadata;
    private final Target<?> target;
    private final Client client;
    private final Retryer retryer;
    private final List<RequestInterceptor> requestInterceptors;
    private final Logger logger;
    private final Logger.Level logLevel;
    private final RequestTemplate.Factory buildTemplateFromArgs;
    private final Options options;
    private final Decoder decoder;
    private final ErrorDecoder errorDecoder;
    private final boolean decode404;
    private final boolean closeAfterDecode;

    private SynchronousMethodHandler(Target<?> target, Client client, Retryer retryer,
                                     List<RequestInterceptor> requestInterceptors, Logger logger,
                                     Logger.Level logLevel, MethodMetadata metadata,
                                     RequestTemplate.Factory buildTemplateFromArgs, Options options,
                                     Decoder decoder, ErrorDecoder errorDecoder, boolean decode404,
                                     boolean closeAfterDecode) {
        this.target = checkNotNull(target, "target");
        this.client = checkNotNull(client, "client for %s", target);
        this.retryer = checkNotNull(retryer, "retryer for %s", target);
        this.requestInterceptors =
                checkNotNull(requestInterceptors, "requestInterceptors for %s", target);
        this.logger = checkNotNull(logger, "logger for %s", target);
        this.logLevel = checkNotNull(logLevel, "logLevel for %s", target);
        this.metadata = checkNotNull(metadata, "metadata for %s", target);
        this.buildTemplateFromArgs = checkNotNull(buildTemplateFromArgs, "metadata for %s", target);
        this.options = checkNotNull(options, "options for %s", target);
        this.errorDecoder = checkNotNull(errorDecoder, "errorDecoder for %s", target);
        this.decoder = checkNotNull(decoder, "decoder for %s", target);
        this.decode404 = decode404;
        this.closeAfterDecode = closeAfterDecode;
    }

    @Override
    public Object invoke(Object[] argv) throws Throwable {
        // 判断是否进行本地调用
        RequestTemplate requestTemplate = this.metadata.template();
        String serverName = this.target.name();
        String[] path = this.target.url().split(serverName);
        String prefix = "";
        // 若@FeignClient注解指定了path属性
        if (path.length > 1) {
            prefix = path[1];
        }
        String url = prefix + requestTemplate.url();
        String requestType = requestTemplate.method().toUpperCase();
        HandlerMethod local = MappingRegister.getMethod(requestType, url);
        if (local == null) {
            // ================ 原有逻辑, 远程调用
            RequestTemplate template = buildTemplateFromArgs.create(argv);
            Retryer retryer = this.retryer.clone();
            while (true) {
                try {
                    return executeAndDecode(template);
                } catch (RetryableException e) {
                    retryer.continueOrPropagate(e);
                    if (logLevel != Logger.Level.NONE) {
                        logger.logRetry(metadata.configKey(), logLevel);
                    }
                    continue;
                }
            }
            // ================
        } else {
            // 比对参数，处理controller方法包含request,response的情况
            Method method = local.getMethod();
            // 构建controller方法参数
            Object[] param = buildParam(method, argv);
            Object result;
            try {
                result = ReflectionUtils.invokeMethod(method, ApplicationContextHelper.getContext().getBean(String.valueOf(local.getBean())), param);
            } catch (CommonException e) {
                throw e;
            } catch (Exception e) {
                throw new CommonException(e.getMessage());
            }
            return buildResponse(result);
        }
    }

    private JwtAccessTokenConverter accessTokenConverter() {
        JwtAccessTokenConverter converter = new JwtAccessTokenConverter();
        converter.setAccessTokenConverter(new CustomTokenConverter());
        converter.setSigningKey(ApplicationContextHelper.getContext().getBean(CoreProperties.class).getOauthJwtKey());
        try {
            converter.afterPropertiesSet();
        } catch (Exception e) {
            return null;
        }
        return converter;
    }

    private Authentication authenticate(Authentication authentication) {
        if (authentication == null) {
            throw new InvalidTokenException("Invalid token (token not found)");
        } else {
            String token = (String) authentication.getPrincipal();
            DefaultTokenServices defaultTokenServices = new DefaultTokenServices();
            defaultTokenServices.setTokenStore(new JwtTokenStore(accessTokenConverter()));
            OAuth2Authentication auth = defaultTokenServices.loadAuthentication(token);
            if (auth == null) {
                throw new InvalidTokenException("Invalid token: " + token);
            } else {
                if (authentication.getDetails() instanceof OAuth2AuthenticationDetails) {
                    OAuth2AuthenticationDetails details = (OAuth2AuthenticationDetails) authentication.getDetails();
                    if (!details.equals(auth.getDetails())) {
                        details.setDecodedDetails(auth.getDetails());
                    }
                }
                auth.setDetails(authentication.getDetails());
                auth.setAuthenticated(true);
                return auth;
            }
        }
    }

    /**
     * 组装controller方法的参数
     *
     * @param method controller方法
     * @param argv   feign接口提供的参数
     * @return controller需要的参数
     */
    private Object[] buildParam(Method method, Object[] argv) {
        ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (servletRequestAttributes != null) {
            HttpServletRequest request = servletRequestAttributes.getRequest();
            // 请求头没有jwt_token，不处理用户信息
            if (StringUtils.isNotBlank(request.getHeader("jwt_token"))) {
                JwtTokenExtractor tokenExtractor = ApplicationContextHelper.getContext().getBean(JwtTokenExtractor.class);
                Authentication authentication = tokenExtractor.extract(servletRequestAttributes.getRequest());
                SecurityContextHolder.getContext().setAuthentication(authentication);
                request.setAttribute(OAuth2AuthenticationDetails.ACCESS_TOKEN_VALUE, authentication.getPrincipal());
                if (authentication instanceof AbstractAuthenticationToken) {
                    AbstractAuthenticationToken needsDetails = (AbstractAuthenticationToken) authentication;
                    needsDetails.setDetails(new OAuth2AuthenticationDetails(request));
                }
                Authentication authResult = this.authenticate(authentication);
                SecurityContextHolder.getContext().setAuthentication(authResult);
            }
        }
        Map<Object, Object> feignParam = new HashMap<>(16);
        this.metadata.indexToName().forEach((k, v) -> feignParam.put(((ArrayList) v).get(0), argv[k]));
        Parameter[] parameters = method.getParameters();
        List<Object> param = new ArrayList<>();
        for (Parameter parameter : parameters) {
            if (Objects.equals(parameter.getType().getName(), "javax.servlet.http.HttpServletRequest")) {
                ServletRequestAttributes servletRequest = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
                param.add(servletRequest == null ? null : servletRequest.getRequest());
            } else if (Objects.equals(parameter.getType().getName(), "javax.servlet.http.HttpServletResponse")) {
                ServletRequestAttributes servletRequest = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
                param.add(servletRequest == null ? null : servletRequest.getResponse());
            } else {
                // 参数在map中不存在，则该参数视为body参数
                if (feignParam.containsKey(parameter.getName())) {
                    param.add(feignParam.get(parameter.getName()));
                } else {
                    // body 参数类型转换
                    Object body = argv[this.metadata.bodyIndex()];
                    Class<?> feignClazz = body.getClass();
                    Class<?> clazz = parameter.getType();
                    if (Objects.equals(feignClazz, clazz)) {
                        param.add(body);
                    } else {
                        // controller的body参数一定要有无参构造
                        try {
                            Object object = clazz.newInstance();
                            BeanUtils.copyProperties(body, object);
                            param.add(object);
                        } catch (Exception e) {
                            param.add(body);
                        }
                    }
                }
            }
        }
        Object[] p = new Object[param.size()];
        p = param.toArray(p);
        return p;
    }

    /**
     * controller返回数据处理，类型转换等
     *
     * @param result controller返回结果
     * @return 转换的结果
     * @throws Exception 异常
     */
    private Object buildResponse(Object result) throws Exception {
        if (result == null) {
            return null;
        }
        // 返回类型为ResponseEntity<?>,直接返回，否则取body返回
        if (this.metadata.returnType() instanceof ParameterizedType) {
            // 带泛型的ResponseEntity
            ParameterizedType parameterizedType = (ParameterizedType) this.metadata.returnType();
            ResponseEntity responseEntity = (ResponseEntity) result;
            // feign的返回类型
            Type feignReturn = parameterizedType.getActualTypeArguments()[0];
            Object trueResult = responseEntity.getBody();
            Class trueReturn = trueResult.getClass();
            // 判断并组装新的responseEntity
            ResponseEntity newResponse;
            if (feignReturn instanceof Class && !feignReturn.equals(trueReturn)) {
                ObjectMapper objectMapper = ApplicationContextHelper.getContext().getBean(ObjectMapper.class);
                // 类型不同
                if (feignReturn.equals(String.class)) {
                    // 字符串
                    String object;
                    try {
                        object = objectMapper.writeValueAsString(trueResult);
                    } catch (Exception e) {
                        object = String.valueOf(trueResult);
                    }
                    newResponse = new ResponseEntity<>(object, responseEntity.getStatusCode());
                } else {
                    // 对象
                    Object object = ((Class) feignReturn).newInstance();
                    BeanUtils.copyProperties(trueResult, object);
                    newResponse = new ResponseEntity<>(object, responseEntity.getStatusCode());
                }
                // todo 其他数据类型
            } else {
                // 类型一致以及有泛型的直接返回，泛型会自己强转
                newResponse = responseEntity;
            }
            if ((parameterizedType.getRawType()).equals(result.getClass())) {
                return newResponse;
            } else {
                return newResponse.getBody();
            }
        } else {
            // 不带泛型的ResponseEntity
            if (this.metadata.returnType().equals(result.getClass())) {
                return result;
            } else {
                return ((ResponseEntity) result).getBody();
            }
        }
    }

    Object executeAndDecode(RequestTemplate template) throws Throwable {
        Request request = targetRequest(template);

        if (logLevel != Logger.Level.NONE) {
            logger.logRequest(metadata.configKey(), logLevel, request);
        }

        Response response;
        long start = System.nanoTime();
        try {
            response = client.execute(request, options);
            // ensure the request is set. TODO: remove in Feign 10
            response.toBuilder().request(request).build();
        } catch (IOException e) {
            if (logLevel != Logger.Level.NONE) {
                logger.logIOException(metadata.configKey(), logLevel, e, elapsedTime(start));
            }
            throw errorExecuting(request, e);
        }
        long elapsedTime = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - start);

        boolean shouldClose = true;
        try {
            if (logLevel != Logger.Level.NONE) {
                response =
                        logger.logAndRebufferResponse(metadata.configKey(), logLevel, response, elapsedTime);
                // ensure the request is set. TODO: remove in Feign 10
                response.toBuilder().request(request).build();
            }
            if (Response.class == metadata.returnType()) {
                if (response.body() == null) {
                    return response;
                }
                if (response.body().length() == null ||
                        response.body().length() > MAX_RESPONSE_BUFFER_SIZE) {
                    shouldClose = false;
                    return response;
                }
                // Ensure the response body is disconnected
                byte[] bodyData = Util.toByteArray(response.body().asInputStream());
                return response.toBuilder().body(bodyData).build();
            }
            if (response.status() >= 200 && response.status() < 300) {
                if (void.class == metadata.returnType()) {
                    return null;
                } else {
                    Object result = decode(response);
                    shouldClose = closeAfterDecode;
                    return result;
                }
            } else if (decode404 && response.status() == 404 && void.class != metadata.returnType()) {
                Object result = decode(response);
                shouldClose = closeAfterDecode;
                return result;
            } else {
                throw errorDecoder.decode(metadata.configKey(), response);
            }
        } catch (IOException e) {
            if (logLevel != Logger.Level.NONE) {
                logger.logIOException(metadata.configKey(), logLevel, e, elapsedTime);
            }
            throw errorReading(request, response, e);
        } finally {
            if (shouldClose) {
                ensureClosed(response.body());
            }
        }
    }

    long elapsedTime(long start) {
        return TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - start);
    }

    Request targetRequest(RequestTemplate template) {
        for (RequestInterceptor interceptor : requestInterceptors) {
            interceptor.apply(template);
        }
        return target.apply(new RequestTemplate(template));
    }

    Object decode(Response response) throws Throwable {
        try {
            return decoder.decode(response, metadata.returnType());
        } catch (FeignException e) {
            throw e;
        } catch (RuntimeException e) {
            throw new DecodeException(e.getMessage(), e);
        }
    }

    static class Factory {

        private final Client client;
        private final Retryer retryer;
        private final List<RequestInterceptor> requestInterceptors;
        private final Logger logger;
        private final Logger.Level logLevel;
        private final boolean decode404;
        private final boolean closeAfterDecode;

        Factory(Client client, Retryer retryer, List<RequestInterceptor> requestInterceptors,
                Logger logger, Logger.Level logLevel, boolean decode404, boolean closeAfterDecode) {
            this.client = checkNotNull(client, "client");
            this.retryer = checkNotNull(retryer, "retryer");
            this.requestInterceptors = checkNotNull(requestInterceptors, "requestInterceptors");
            this.logger = checkNotNull(logger, "logger");
            this.logLevel = checkNotNull(logLevel, "logLevel");
            this.decode404 = decode404;
            this.closeAfterDecode = closeAfterDecode;
        }

        public MethodHandler create(Target<?> target, MethodMetadata md,
                                    RequestTemplate.Factory buildTemplateFromArgs,
                                    Options options, Decoder decoder, ErrorDecoder errorDecoder) {
            return new SynchronousMethodHandler(target, client, retryer, requestInterceptors, logger,
                    logLevel, md, buildTemplateFromArgs, options, decoder,
                    errorDecoder, decode404, closeAfterDecode);
        }
    }
}
