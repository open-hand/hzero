package org.hzero.message.app.service;

import java.util.List;

import org.hzero.message.api.dto.NoticeDTO;
import org.hzero.message.domain.entity.Notice;

import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;

/**
 * 公告基础信息应用服务
 *
 * @author runbai.chen@hand-china.com 2018-08-02 15:23:14
 */
public interface NoticeService {

    /**
     * 创建公告
     *
     * @param noticeDTO 公告
     * @return 公告
     */
    NoticeDTO createNotice(NoticeDTO noticeDTO);

    /**
     * 更新公告
     *
     * @param noticeDTO 参数
     * @return 公告
     */
    NoticeDTO updateNotice(NoticeDTO noticeDTO);

    /**
     * 删除公告
     *
     * @param organizationId 租户ID
     * @param noticeId       公告ID
     * @return 公告
     */
    Notice deleteNotice(Long organizationId, Long noticeId);

    /**
     * 发布公告
     *
     * @param organizationId 租户ID
     * @param noticeId       公告ID
     * @return 公告
     */
    Notice publicNotice(Long organizationId, Long noticeId);

    /**
     * 撤销删除公告
     *
     * @param organizationId 租户ID
     * @param noticeId       公告ID
     * @return 公告
     */
    Notice revokeNotice(Long organizationId, Long noticeId);

    /**
     * 查询公告标题
     *
     * @param category       公告类别
     * @param title          标题
     * @param organizationId 租户
     * @param pageRequest    分页请求
     * @return Page<NoticeDTO>
     */
    Page<NoticeDTO> pageNoticeTitle(String category, String title, Long organizationId, PageRequest pageRequest);

    /**
     * 预览用户公告列表
     *
     * @param noticeDTO 参数
     * @return 公告
     */
    List<NoticeDTO> listUserAnnouncement(NoticeDTO noticeDTO);

    /**
     * 查询顶部悬浮公告
     *
     * @param noticeDTO 参数
     * @return 最新的一条顶部公告
     */
    NoticeDTO topAnnouncement(NoticeDTO noticeDTO);

    /**
     * 分页查询公告
     *
     * @param pageRequest 分页
     * @param noticeDTO   参数
     * @return 公告
     */
    Page<NoticeDTO> pageNotice(PageRequest pageRequest, NoticeDTO noticeDTO);
}
