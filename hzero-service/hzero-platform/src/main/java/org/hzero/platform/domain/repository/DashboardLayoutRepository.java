package org.hzero.platform.domain.repository;


import org.hzero.mybatis.base.BaseRepository;
import org.hzero.platform.domain.entity.DashboardLayout;
import org.hzero.platform.domain.vo.DashboardLayoutVO;

import java.util.List;

/**
 * 工作台配置资源库
 *
 * @author zhiying.dong@hand-china.com 2018-09-25 10:51:53
 * @author xiaoyu.zhao@hand-china.com
 */
public interface DashboardLayoutRepository extends BaseRepository<DashboardLayout> {


    /**
     * 查询获取工作台配置卡片信息
     *
     * @return List<DashboardLayoutVO>
     */
    List<DashboardLayoutVO> selectDashboardLayoutCards();

    /**
     * 查询工作台卡片配置信息
     *
     * @return List<DashboardLayoutVO>
     */
    List<DashboardLayoutVO> selectDashboardLayout();
}
