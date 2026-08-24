package org.hzero.admin.app.service;

import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import org.hzero.admin.domain.entity.HService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 应用服务应用服务
 *
 * @author zhiying.dong@hand-china.com 2018-12-14 10:35:51
 */
public interface HServiceService {

    /**
     * 创建服务
     *
     * @param service 应用服务实体
     * @return HService
     */
    HService createService(HService service);

    /**
     * 分页查询服务
     *
     * @param service     应用服务实体
     * @param pageRequest 分页参数
     * @return Page<HService>
     */
    Page<HService> pageService(HService service, PageRequest pageRequest);

    /**
     * 查询服务列表
     *
     * @param service 应用服务实体
     * @return List<HService>
     */
    List<HService> selectServices(HService service);

    /**
     * 修改服务信息
     *
     * @param service 应用服务实体
     * @return HService
     */
    HService updateService(HService service);

    /**
     * 删除应用服务信息
     *
     * @param hService 应用服务实体
     */
    void removeService(HService hService);

    /**
     * 下载服务版本信息
     *
     * @param service  条件
     * @param request  request
     * @param response response
     */
    void downloadYml(HService service, HttpServletRequest request, HttpServletResponse response);

    HService selectServiceDetails(Long serviceId);
}
