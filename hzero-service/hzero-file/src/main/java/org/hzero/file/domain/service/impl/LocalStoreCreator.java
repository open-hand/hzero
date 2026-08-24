package org.hzero.file.domain.service.impl;

import org.hzero.starter.file.service.AbstractFileService;
import org.hzero.starter.file.service.StoreCreator;
import org.springframework.stereotype.Component;

/**
 * description
 *
 * @author shuangfei.zhu@hand-china.com 2019/12/18 19:08
 */
@Component
public class LocalStoreCreator implements StoreCreator {

    @Override
    public Integer storeType() {
        return 6;
    }

    @Override
    public AbstractFileService getFileService() {
        return new LocalFileServiceImpl();
    }
}
