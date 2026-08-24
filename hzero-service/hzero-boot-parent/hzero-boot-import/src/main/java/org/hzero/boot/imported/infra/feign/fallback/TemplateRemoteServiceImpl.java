/*
 *
 *  项目名称：choerodon-framework-parent
 *  Copyright @ 2018  Shanghai Hand Co. Ltd.
 *  All right reserved.
 *
 *
 */

package org.hzero.boot.imported.infra.feign.fallback;


import org.hzero.boot.imported.infra.feign.TemplateRemoteService;
import org.hzero.core.util.Results;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;


/**
 * Content feign fallback
 *
 * @author : shuangfei.zhu@hand-china.com
 */
@Component
public class TemplateRemoteServiceImpl implements TemplateRemoteService {

    @Override
    public ResponseEntity<String> getTemplate(Long tenantId, String templateCode) {
        return Results.error();
    }
}