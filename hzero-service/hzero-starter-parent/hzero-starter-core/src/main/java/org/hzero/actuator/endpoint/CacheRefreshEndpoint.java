package org.hzero.actuator.endpoint;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import springfox.documentation.annotations.ApiIgnore;

import io.choerodon.swagger.annotation.Permission;

import org.hzero.actuator.cache.CacheRefresher;
import org.hzero.core.util.AsyncTask;
import org.hzero.core.util.CommonExecutor;
import org.hzero.core.util.Results;

/**
 * 缓存刷新端点
 *
 * @author bojiangzhou
 */
@Controller
@ApiIgnore
public class CacheRefreshEndpoint {

    public static final String REFRESH_CACHE_ENDPOINT = "/v2/actuator/cache-refresh";

    private final List<AsyncTask<Boolean>> taskList;

    public CacheRefreshEndpoint(Optional<List<CacheRefresher>> cacheRefreshers) {
        List<CacheRefresher> refresherList = cacheRefreshers.orElse(new ArrayList<>());
        List<AsyncTask<Boolean>> taskList = refresherList.stream().map(item -> new AsyncTask<Boolean>() {
            @Override
            public String taskName() {
                return item.getClass().getName();
            }

            @Override
            public Boolean doExecute() {
                item.refreshCache();
                return true;
            }
        }).collect(Collectors.toList());
        this.taskList = Collections.unmodifiableList(taskList);
    }

    @ResponseBody
    @GetMapping(REFRESH_CACHE_ENDPOINT)
    @Permission(permissionWithin = true)
    public ResponseEntity<Void> refreshServiceCache() {
        if (CollectionUtils.isNotEmpty(taskList)) {
            CommonExecutor.batchExecuteAsync(taskList, "RefreshCache");
        }
        return Results.success();
    }
}
