package org.hzero.iam.infra.mapper;

import io.choerodon.mybatis.common.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.hzero.boot.oauth.domain.entity.BaseOpenApp;
import org.hzero.iam.domain.entity.OpenApp;

import java.util.List;

/**
 * 三方网站管理Mapper
 *
 * @author jiaxu.cui@hand-china.com 2018/9/29 14:05
 */
public interface OpenAppMapper extends BaseMapper<OpenApp> {

    /**
     * 查询已启用的三方网站登录方式
     *
     * @return
     */
    List<BaseOpenApp> listLoginWayByEnabled();

    /**
     * 查询三方网站登录信息列表
     *
     * @param openApp 查询条件
     * @return List<OpenApp>
     */
    List<OpenApp> selectOpenAppList(OpenApp openApp);

    /**
     * 查询三方登录明细
     *
     * @param openAppId 主键Id
     * @return 查询明细结果
     */
    OpenApp selectOpenAppDetails(@Param("openAppId") Long openAppId);
}
