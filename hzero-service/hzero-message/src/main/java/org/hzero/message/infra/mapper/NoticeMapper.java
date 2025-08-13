package org.hzero.message.infra.mapper;

import java.util.Date;
import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.hzero.message.api.dto.NoticeDTO;
import org.hzero.message.domain.entity.Notice;

import io.choerodon.mybatis.common.BaseMapper;

/**
 * 公告基础信息Mapper
 *
 * @author runbai.chen@hand-china.com 2018-08-02 15:23:14
 */
public interface NoticeMapper extends BaseMapper<Notice> {

    /**
     * 查询列表
     *
     * @param noticeDTO 参数
     * @return 公告
     */
    List<NoticeDTO> selectNotice(NoticeDTO noticeDTO);

    /**
     * 查询用户公告列表
     * <p>
     * 当前用户的租户有对应组织就能看到组织级公告
     * 当前用户的租户有对应角色就能看到角色级公告
     *
     * @param noticeDTO 参数
     * @return 公告
     */
    List<NoticeDTO> listUserAnnouncement(NoticeDTO noticeDTO);

    /**
     * 查询公告标题、发布人、发布时间
     *
     * @param title              标题
     * @param lang               语言
     * @param noticeCategoryCode 公告类别
     * @param now                当前时间
     * @param tenantId           租户ID
     * @return List
     */
    List<NoticeDTO> selectNoticeTitle(@Param("title") String title,
                                      @Param("lang") String lang,
                                      @Param("noticeCategoryCode") String noticeCategoryCode,
                                      @Param("now") Date now,
                                      @Param("tenantId") Long tenantId);

    /**
     * 查询公告/通知内容
     *
     * @param tenantId 租户ID
     * @param noticeId 公告ID
     * @return 公告/通知
     */
    NoticeDTO selectNoticeBody(@Param("tenantId") Long tenantId, @Param("noticeId") Long noticeId);

    /**
     * 查询公告/通知 明细及内容
     *
     * @param tenantId 租户ID
     * @param noticeId 主键
     * @return 内容
     */
    NoticeDTO selectNoticeById(@Param("tenantId") Long tenantId,
                               @Param("noticeId") Long noticeId);
}
