package org.hzero.file.infra.mapper;

import java.util.Date;
import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.hzero.file.domain.entity.FileEditLog;
import io.choerodon.mybatis.common.BaseMapper;

/**
 * 文件编辑日志Mapper
 *
 * @author shuangfei.zhu@hand-china.com 2019-06-28 11:36:32
 */
public interface FileEditLogMapper extends BaseMapper<FileEditLog> {

    /**
     * 查询
     *
     * @param tenantId       租户Id
     * @param fileName       文件名
     * @param realName       提交人
     * @param editType       编辑类型
     * @param changeDateFrom 编辑时间从
     * @param changeDateTo   编辑时间至
     * @return 查询结果
     */
    List<FileEditLog> listFileEditLog(@Param("tenantId") Long tenantId,
                                      @Param("fileName") String fileName,
                                      @Param("realName") String realName,
                                      @Param("editType") String editType,
                                      @Param("changeDateFrom") Date changeDateFrom,
                                      @Param("changeDateTo") Date changeDateTo);
}
