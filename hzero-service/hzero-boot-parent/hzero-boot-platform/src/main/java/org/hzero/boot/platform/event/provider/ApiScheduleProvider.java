package org.hzero.boot.platform.event.provider;

import org.hzero.boot.platform.event.Constants;
import org.hzero.boot.platform.event.helper.AsyncScheduleHelper;
import org.hzero.boot.platform.event.helper.RequestHelper;
import org.hzero.boot.platform.event.helper.RuleMatcher;
import org.hzero.boot.platform.event.vo.ApiParam;
import org.hzero.boot.platform.event.vo.EventParam;
import org.hzero.boot.platform.event.vo.EventRuleVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;

/**
 * API提供器
 *
 * @author bergturing 2020/08/11 10:59
 */
@Component
public class ApiScheduleProvider extends AbstractScheduleProvider {
    /**
     * 日志对象
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(ApiScheduleProvider.class);

    /**
     * 网络请求Helper
     */
    private final RequestHelper requestHelper;

    /**
     * 异步调用Helper
     */
    private final AsyncScheduleHelper asyncScheduleHelper;

    @Autowired
    public ApiScheduleProvider(RuleMatcher ruleMatcher,
                               RequestHelper requestHelper, AsyncScheduleHelper asyncScheduleHelper) {
        super(ruleMatcher);
        this.requestHelper = requestHelper;
        this.asyncScheduleHelper = asyncScheduleHelper;
    }

    @Override
    protected Object doSchedule(@Nonnull EventRuleVO eventRuleVO, EventParam eventParam) {
        String apiUrl = eventRuleVO.getApiUrl();
        HttpMethod apiMethod = eventRuleVO.httpMethod();

        ApiParam apiParam = this.getApiArgs(eventParam);
        // 同步调用
        if (eventRuleVO.syncCall()) {
            LOGGER.debug(">>>>> 同步API调用");
            ResponseEntity<Object> responseEntity = this.requestHelper.request(apiUrl, apiMethod, apiParam);
            if (eventRuleVO.enableResult()) {
                return responseEntity.getBody();
            }
        } else {
            // 异步调用
            LOGGER.debug(">>>>> 异步API调用");
            this.asyncScheduleHelper.asyncApiSchedule(apiUrl, apiMethod, apiParam, requestHelper);
        }

        return null;
    }

    @Override
    public String supportType() {
        return Constants.CallType.API;
    }

    private ApiParam getApiArgs(EventParam eventParam) {
        ApiParam apiParam = null;
        if (eventParam instanceof ApiParam) {
            apiParam = (ApiParam) eventParam;
            return apiParam;
        }

        return new ApiParam();
    }
}
