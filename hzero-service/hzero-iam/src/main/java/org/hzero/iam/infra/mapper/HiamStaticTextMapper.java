package org.hzero.iam.infra.mapper;

import java.util.Date;

import org.apache.ibatis.annotations.Param;

/**
 * 静态文本Mapper
 *
 * @author bojiangzhou 2018/07/01
 */
public interface HiamStaticTextMapper {

    /**
     * 查询有效的注册协议
     * 
     * @param textId 协议ID
     * @param now 当前时间
     */
    int countValidRegisterProtocol(@Param("textId") Long textId, @Param("now") Date now);

}
