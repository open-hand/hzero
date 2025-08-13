package org.hzero.config.app.service.impl;

import org.hzero.config.app.service.ConfigListener;
import org.hzero.config.app.service.FailureCounter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * @author XCXCXCXCX
 * @date 2020/4/30 3:14 下午
 */
public class DefaultConfigListener implements ConfigListener, FailureCounter {

    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultConfigListener.class);

    private RestTemplate restTemplate = new RestTemplate();

    private Set<String> keys = new HashSet<>();

    private String notifyUrl;

    private int failed;

    public DefaultConfigListener(String[] keys, String notifyUrl) {
        this.keys.addAll(Arrays.asList(keys));
        this.notifyUrl = notifyUrl;
    }

    @Override
    public boolean interest(String key) {
        return keys.contains(key);
    }

    @Override
    public void onChange(String key, String value) {
        String body = key + "=" + value;
        HttpEntity<String> requestEntity = new HttpEntity<>(body);
        try {
            ResponseEntity<String> responseEntity = restTemplate.exchange(notifyUrl, HttpMethod.POST, requestEntity, String.class);
            if (!responseEntity.getStatusCode().is2xxSuccessful()) {
                failed++;
                LOGGER.error("listener onChange() failed, cause: code={},msg={}", responseEntity.getStatusCode().value(), responseEntity.getStatusCode().getReasonPhrase());
            }
        } catch (Throwable e) {
            failed++;
            LOGGER.error("listener onChange() failed, cause: {}", e.getMessage());
        }

    }

    @Override
    public int countFailure() {
        return failed;
    }
}
