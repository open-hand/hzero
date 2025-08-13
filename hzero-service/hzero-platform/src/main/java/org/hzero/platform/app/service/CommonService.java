package org.hzero.platform.app.service;

import java.util.List;
import java.util.Map;

/**
 * 通用接口 service接口
 *
 * @author xiaoyu.zhao@hand-china.com 2019/04/23 20:35
 */
public interface CommonService {

    /**
     * 查询所有国际冠码
     *
     * @return 冠码
     */
    List<Map<String, String>> listIDD();
}
