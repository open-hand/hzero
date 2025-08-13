package org.hzero.iam.domain.repository;

import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import org.hzero.boot.oauth.domain.entity.BaseOpenApp;
import org.hzero.iam.domain.entity.OpenApp;
import org.hzero.mybatis.base.BaseRepository;

import java.util.List;

/**
 * 三方网站管理资源库
 *
 * @author jiaxu.cui@hand-china.com 2018/9/29 14:03
 */
public interface OpenAppRepository extends BaseRepository<OpenApp> {

    /**
     * 查询已启用的三方网站登录方式
     *
     * @return
     */
    List<BaseOpenApp> listLoginWayByEnabled();

    /**
     * 模糊查询三方网站
     *
     * @param pageRequest 分页参数
     * @param openApp     三方网站实体
     * @return
     */
    Page<OpenApp> pageOpenApp(PageRequest pageRequest, OpenApp openApp);

    /**
     * 获取三方登录明细，不返回app_key字段的内容，前端显示未更改
     *
     * @param openAppId 主键Id
     * @return 查询明细结果
     */
    OpenApp getOpenAppDetails(Long openAppId);
}
