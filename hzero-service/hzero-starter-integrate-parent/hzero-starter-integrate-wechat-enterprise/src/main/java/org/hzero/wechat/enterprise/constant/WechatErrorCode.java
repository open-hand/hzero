package org.hzero.wechat.enterprise.constant;

import org.hzero.core.base.BaseConstants;

/**
 * description
 *
 * @author zifeng.ding@hand-china.com 2019/12/26 19:02
 */
public interface WechatErrorCode {

    /**
     * 成功
     */
    Long SUCCESS = 0L;

    Long MOBILE_EXISTS = 60104L;
    /**
     * 用户ID已经存在
     */
    Long USERID_EXISTS = 60102L;
    /**
     * 部门ID已经存在
     */
    Long DEPT_EXISTS = 60008L;

    Long DEPT_NOT_FOUND = 60003L;
}
