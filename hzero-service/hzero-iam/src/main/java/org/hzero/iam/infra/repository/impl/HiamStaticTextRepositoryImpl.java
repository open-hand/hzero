package org.hzero.iam.infra.repository.impl;

import java.util.Date;

import org.hzero.iam.domain.repository.HiamStaticTextRepository;
import org.hzero.iam.infra.mapper.HiamStaticTextMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 静态文本远程资源库默认实现
 *
 * @author bojiangzhou 2018/07/01
 */
@Component
public class HiamStaticTextRepositoryImpl implements HiamStaticTextRepository {

    @Autowired
    private HiamStaticTextMapper staticTextMapper;

    @Override
    public int countValidRegisterProtocol(Long textId) {
        return staticTextMapper.countValidRegisterProtocol(textId, new Date());
    }
}
