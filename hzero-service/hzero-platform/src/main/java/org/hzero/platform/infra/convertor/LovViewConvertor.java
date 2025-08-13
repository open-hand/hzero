package org.hzero.platform.infra.convertor;

import java.util.List;

import org.hzero.core.base.BaseConstants;
import org.hzero.platform.domain.entity.Lov;
import org.hzero.platform.domain.entity.LovViewHeader;
import org.hzero.platform.domain.entity.LovViewLine;
import org.hzero.platform.domain.vo.LovViewVO;
import org.springframework.util.Assert;

/**
 * 值集视图转换器
 *
 * @author gaokuo.dai@hand-china.com 2018年6月19日上午11:42:51
 */
public class LovViewConvertor {

    /**
     * 组装Lov视图DTO
     *
     * @param lovViewHeader Lov视图头
     * @param lovViewLines  Lov视图行列表
     * @param lov           Lov
     * @return Lov视图DTO
     */
    public static LovViewVO assembleLovViewDTO(LovViewHeader lovViewHeader, List<LovViewLine> lovViewLines, Lov lov) {
        Assert.notNull(lovViewHeader, BaseConstants.ErrorCode.DATA_INVALID);
        Assert.notNull(lov, BaseConstants.ErrorCode.DATA_INVALID);
        LovViewVO lovViewDto = LovViewVO.build(lovViewHeader.getViewCode(), lovViewHeader.getViewName(), lov.getLovCode(), lov.getLovName(), lov.getLovTypeCode(), lovViewHeader.getTenantId(),
                lovViewHeader.getValueField(), lovViewHeader.getDisplayField(), lovViewHeader.getTitle(),
                lovViewHeader.getWidth(), lovViewHeader.getHeight(), lovViewHeader.getPageSize(),
                lovViewHeader.getDelayLoadFlag(), lov.convertTrueUrl(), lovViewHeader.getChildrenFieldName());
        if (lovViewLines != null) {
            lovViewDto.convertLovViewLines(lovViewLines);
        }
        return lovViewDto;
    }
}
