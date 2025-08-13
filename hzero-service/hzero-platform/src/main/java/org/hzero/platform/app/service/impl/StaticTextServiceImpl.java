package org.hzero.platform.app.service.impl;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.hzero.core.base.BaseConstants;
import org.hzero.core.util.EscapeUtils;
import org.hzero.platform.app.service.StaticTextService;
import org.hzero.platform.domain.entity.StaticText;
import org.hzero.platform.domain.entity.StaticTextValue;
import org.hzero.platform.domain.repository.StaticTextRepository;
import org.hzero.platform.domain.repository.StaticTextValueRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;


/**
 * 平台静态信息应用服务默认实现
 *
 * @author fatao.liu@hand-china.com 2018-07-23 14:19:42
 */
@Service
public class StaticTextServiceImpl implements StaticTextService {

    @Autowired
    private StaticTextRepository staticTextRepository;
    @Autowired
    private StaticTextValueRepository staticTextValueRepository;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public StaticText createText(StaticText staticText) {
        // 防范XSS攻击
        staticText.setText(EscapeUtils.preventScript(staticText.getText()));
        // 初始化部分数据并校验
        staticText.validate(staticTextRepository);

        staticTextRepository.insertSelective(staticText);

        StaticTextValue textValue = staticText.generateStaticTextValue();
        Assert.isTrue(StringUtils.isNotEmpty(textValue.getText()), BaseConstants.ErrorCode.DATA_INVALID);
        staticTextValueRepository.insert(textValue);

        staticTextRepository.clearCache(staticText);
        return staticText;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public StaticText updateText(StaticText staticText) {
        // 防范XSS攻击
        staticText.setText(EscapeUtils.preventScript(staticText.getText()));
        staticText.validate(staticTextRepository);

        staticTextRepository.updateByPrimaryKey(staticText);

        StaticTextValue textValue = staticText.generateStaticTextValue();
        StaticTextValue param = new StaticTextValue();
        param.setTextId(staticText.getTextId());
        param.setLang(staticText.getLang());

        StaticTextValue dbTextValue = staticTextValueRepository.selectOne(param);
        if (dbTextValue != null) {
            textValue.setTextValueId(dbTextValue.getTextValueId());
            staticTextValueRepository.updateByPrimaryKey(textValue);
        } else {
            staticTextValueRepository.insert(textValue);
        }
        staticTextRepository.clearCache(staticText);
        return staticText;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchDelete(List<StaticText> texts) {
        Long[] textIds = texts.stream()
                .map(StaticText::getTextId)
                .collect(Collectors.toList())
                .toArray(ArrayUtils.EMPTY_LONG_OBJECT_ARRAY);

        Set<Long> allIds = new HashSet<>(8);
        if (ArrayUtils.isNotEmpty(textIds)) {
            for (Long textId : textIds) {
                allIds.add(textId);
                allIds.addAll(staticTextRepository.getAllChildTextId(textId));
            }
        }

        StaticTextValue staticTextValue = new StaticTextValue();
        for (Long textId : allIds) {
            //清除缓存
            staticTextRepository.clearCache(staticTextRepository.selectByPrimaryKey(textId));
            // delete value
            staticTextValue.setTextId(textId);
            staticTextValueRepository.delete(staticTextValue);

            // delete text
            staticTextRepository.deleteByPrimaryKey(textId);
        }
    }

    @Override
    public StaticTextValue getTextValueById(Long textId) {
        return staticTextValueRepository.selectTextValueById(textId);
    }

}
