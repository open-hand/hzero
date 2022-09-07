package org.hzero.report.domain.service.impl;

import java.io.InputStream;

import org.hzero.boot.file.OnlyOfficeClient;
import org.hzero.common.HZeroService;
import org.hzero.core.base.BaseConstants;
import org.hzero.report.domain.service.IFileChangeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

/**
 * description
 *
 * @author shuangfei.zhu@hand-china.com 2020/03/20 15:46
 */
@Service
public class FileChangeServiceImpl implements IFileChangeService {

    @Autowired(required = false)
    private OnlyOfficeClient onlyOfficeClient;

    @Override
    public InputStream changeToRtf(Long tenantId, String url) {
        String[] str = url.split("\\.");
        Assert.isTrue(str.length > 0, BaseConstants.ErrorCode.DATA_INVALID);
        String suffix = str[str.length - BaseConstants.Digital.ONE].toLowerCase();
        return onlyOfficeClient.converterStreamByUrl("rtf", tenantId, HZeroService.Report.BUCKET_NAME, null, url, suffix);
    }
}
