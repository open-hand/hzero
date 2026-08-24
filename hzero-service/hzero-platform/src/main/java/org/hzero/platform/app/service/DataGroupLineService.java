package org.hzero.platform.app.service;

import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import org.hzero.platform.domain.entity.DataGroupLine;

import java.util.List;

/**
 * 数据组行定义应用服务
 *
 * @author jianbo.li@hand-china.com 2019-07-17 15:48:48
 */
public interface DataGroupLineService {

    /**
     * 分页查询数据组行
     * @param pageRequest
     * @param dataGroupLine
     * @return
     */
    Page<DataGroupLine> pageDataGroupLine(PageRequest pageRequest, DataGroupLine dataGroupLine);


    /**
     * 新建数据组行
     * 行的租户id取自头的租户id
     * @param dataGroupLineList
     * @return
     */
    List<DataGroupLine> createDataGroupLine(List<DataGroupLine> dataGroupLineList);

    /**
     * 删除数据行
     * @param dataGroupLineList
     */
    void deleteDataGroupLine(List<DataGroupLine> dataGroupLineList);

}
