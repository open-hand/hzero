package org.hzero.message.domain.repository;

import java.util.List;

import org.hzero.message.api.dto.NoticeDTO;
import org.hzero.message.domain.entity.Notice;
import org.hzero.mybatis.base.BaseRepository;

import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;

/**
 * 公告基础信息资源库
 *
 * @author runbai.chen@hand-china.com 2018-08-02 15:23:14
 */
public interface NoticeRepository extends BaseRepository<Notice> {

    /**
     * 分页查询公告/通知
     *
     * @param pageRequest 分页
     * @param noticeDTO   参数
     * @return 公告
     */
    Page<NoticeDTO> pageNotice(PageRequest pageRequest, NoticeDTO noticeDTO);

    /**
     * 分页查询用户公告列表
     *
     * @param pageRequest 分页
     * @param noticeDTO   参数
     * @return 公告
     */
    Page<NoticeDTO> pageUserAnnouncement(PageRequest pageRequest, NoticeDTO noticeDTO);

    /**
     * 查询用户公告列表
     *
     * @param noticeDTO 参数
     * @return 公告
     */
    List<NoticeDTO> listUserAnnouncement(NoticeDTO noticeDTO);

    /**
     * 查询明细
     *
     * @param tenantId 租户ID
     * @param noticeId 公告ID
     * @return 公告
     */
    NoticeDTO detailNotice(Long tenantId, Long noticeId);

    /**
     * 查询标题、发布人、发布时间信息
     *
     * @param noticeCategoryCode 公告类别
     * @param lang               语言
     * @param title              标题
     * @param tenantId           租户ID
     * @param pageRequest        Page
     * @return Page
     */
    Page<NoticeDTO> pageNoticeTitle(String noticeCategoryCode, String lang, String title, Long tenantId, PageRequest pageRequest);

    /**
     * 查询内容
     *
     * @param tenantId 租户Id
     * @param noticeId 主键
     * @return 公告
     */
    NoticeDTO selectNoticeBody(Long tenantId, Long noticeId);

    /**
     * 公告基础信息明细列表（查询所有明细信息）
     *
     * @param noticeDTO   查询条件
     * @param pageRequest 分页参数
     * @return 分页之后的返回参数
     */
    Page<NoticeDTO> selectNoticeWithDetails(NoticeDTO noticeDTO, PageRequest pageRequest);

    /**
     * 公告/通知 明细
     *
     * @param tenantId 租户Id
     * @param noticeId 公告Id
     * @return 公告信息
     */
    NoticeDTO detailAnnouncement(Long tenantId, Long noticeId);
}
