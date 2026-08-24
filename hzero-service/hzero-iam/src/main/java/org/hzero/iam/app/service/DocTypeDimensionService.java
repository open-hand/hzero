package org.hzero.iam.app.service;

import org.hzero.iam.domain.entity.DocTypeDimension;

/**
 * 单据类型维度应用服务
 *
 * @author zifeng.ding@hand-china.com 2019-09-18 11:48:01
 */
public interface DocTypeDimensionService {

    /**
     * 创建单据权限维度
     *
     * @param docTypeDimension 单据权限维度
     * @return 新增结果
     */
    DocTypeDimension createDocTypeDimension(DocTypeDimension docTypeDimension);

    /**
     * 更新单据权限维度
     *
     * @param docTypeDimension 单据权限维度
     * @return 更新结果
     */
    DocTypeDimension updateDocTypeDimension(DocTypeDimension docTypeDimension);
}
