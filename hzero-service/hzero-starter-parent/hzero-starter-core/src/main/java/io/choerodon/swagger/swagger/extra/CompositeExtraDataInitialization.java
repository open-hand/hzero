package io.choerodon.swagger.swagger.extra;

import java.util.Comparator;
import java.util.List;

/**
 * 综合ExtraData初始化
 * @author XCXCXCXCX
 * @date 2019/8/26
 */
public class CompositeExtraDataInitialization implements ExtraDataInitialization {

    private final List<ExtraDataInitialization> initializationList;

    public CompositeExtraDataInitialization(List<ExtraDataInitialization> initializationList) {
        this.initializationList = initializationList;
        this.initializationList.sort(Comparator.comparingInt(ExtraDataInitialization::getOrder));
        init(ExtraDataManager.extraData);
    }

    @Override
    public void init(ExtraData extraData) {
        initializationList.forEach(initialization -> initialization.init(extraData));
    }

    @Override
    public int getOrder() {
        return Integer.MAX_VALUE;
    }
}
