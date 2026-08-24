package io.choerodon.swagger.swagger.extra;

/**
 * ExtraData初始化处理
 * @author XCXCXCXCX
 * @date 2019/8/26
 */
public interface ExtraDataInitialization {

    void init(ExtraData extraData);

    int getOrder();

    default ExtraData getExtraData(){
        return ExtraDataManager.extraData;
    }
}
