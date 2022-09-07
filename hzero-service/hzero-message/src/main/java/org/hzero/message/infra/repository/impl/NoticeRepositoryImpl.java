package org.hzero.message.infra.repository.impl;

import java.util.Date;
import java.util.List;

import org.hzero.core.convert.CommonConverter;
import org.hzero.message.api.dto.NoticeDTO;
import org.hzero.message.domain.entity.Notice;
import org.hzero.message.domain.entity.NoticeContent;
import org.hzero.message.domain.repository.NoticeContentRepository;
import org.hzero.message.domain.repository.NoticeRepository;
import org.hzero.message.infra.mapper.NoticeMapper;
import org.hzero.mybatis.base.impl.BaseRepositoryImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.PageHelper;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;

/**
 * 公告基础信息 资源库实现
 *
 * @author runbai.chen@hand-china.com 2018-08-02 15:23:14
 */
@Component
public class NoticeRepositoryImpl extends BaseRepositoryImpl<Notice> implements NoticeRepository {

    @Autowired
    private NoticeMapper noticeMapper;
    @Autowired
    private NoticeContentRepository noticeContentRepository;

    @Override
    public Page<NoticeDTO> pageNotice(PageRequest pageRequest, NoticeDTO noticeDTO) {
        return PageHelper.doPageAndSort(pageRequest, () -> noticeMapper.selectNotice(noticeDTO));
    }

    @Override
    public Page<NoticeDTO> pageUserAnnouncement(PageRequest pageRequest, NoticeDTO noticeDTO) {
        return PageHelper.doPageAndSort(pageRequest, () -> noticeMapper.listUserAnnouncement(noticeDTO));
    }

    @Override
    public List<NoticeDTO> listUserAnnouncement(NoticeDTO noticeDTO) {
        return noticeMapper.listUserAnnouncement(noticeDTO);
    }

    @Override
    public NoticeDTO detailNotice(Long tenantId, Long noticeId) {
        Notice notice = selectOne(new Notice().setTenantId(tenantId).setNoticeId(noticeId));
        if (notice == null) {
            return null;
        }
        NoticeContent noticeContent = noticeContentRepository.selectOne(new NoticeContent().setNoticeId(noticeId).setTenantId(tenantId));
        NoticeDTO noticeDTO = CommonConverter.beanConvert(NoticeDTO.class, notice);
        noticeDTO.setNoticeContent(noticeContent);
        return noticeDTO;
    }

    @Override
    public Page<NoticeDTO> pageNoticeTitle(String noticeCategoryCode, String lang, String title, Long tenantId, PageRequest pageRequest) {
        return PageHelper.doPage(pageRequest, () -> noticeMapper.selectNoticeTitle(title, lang, noticeCategoryCode, new Date(), tenantId));
    }

    @Override
    public NoticeDTO selectNoticeBody(Long tenantId, Long id) {
        return noticeMapper.selectNoticeBody(tenantId, id);
    }

    @Override
    public Page<NoticeDTO> selectNoticeWithDetails(NoticeDTO noticeDTO, PageRequest pageRequest) {
        Page<NoticeDTO> pageList = PageHelper.doPageAndSort(pageRequest, () -> noticeMapper.selectNotice(noticeDTO));
        List<NoticeDTO> resultList = pageList.getContent();
        // 将明细信息填入返回结果集中
        resultList.forEach(tempNoticeDTO -> {
            NoticeContent noticeContent = noticeContentRepository.selectOne(
                    new NoticeContent().setNoticeId(tempNoticeDTO.getNoticeId()).setTenantId(tempNoticeDTO.getTenantId()));
            tempNoticeDTO.setNoticeContent(noticeContent);
        });
        pageList.setContent(resultList);
        return pageList;
    }

    @Override
    public NoticeDTO detailAnnouncement(Long tenantId, Long noticeId) {
        return noticeMapper.selectNoticeById(tenantId, noticeId);
    }
}
