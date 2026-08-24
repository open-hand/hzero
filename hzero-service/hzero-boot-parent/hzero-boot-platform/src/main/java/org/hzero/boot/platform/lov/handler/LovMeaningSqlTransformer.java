package org.hzero.boot.platform.lov.handler;

import org.hzero.boot.platform.lov.dto.LovDTO;

/**
 * 含义查询值集SQL改写器
 *
 * @author gaokuo.dai@hand-china.com 2018年9月8日下午3:57:54
 */
public interface LovMeaningSqlTransformer {

    /**
     * 含义查询值集SQL改写

     * @param originSql 源sql语句
     * @param lov 值集配置信息
     * @return 改写后的,查询值集含义专用的sql语句
     */
    String doTransform(String originSql, LovDTO lov);

}
