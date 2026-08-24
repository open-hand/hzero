/**
 * 
 */
package org.hzero.platform.infra.convertor;

import org.hzero.platform.api.dto.LovValueDTO;
import org.hzero.platform.domain.entity.LovValue;

/**
 * 值集值转化器
 *
 * @author gaokuo.dai@hand-china.com 2018年6月27日下午4:21:42
 */
public class LovValueConvertor {

    public static LovValueDTO entityToDto(LovValue entity) {
        return LovValueDTO.build(entity.getValue(), entity.getMeaning(), entity.getDescription(), entity.getTag(), 
                        entity.getParentValue(), entity.getOrderSeq());
    }

}
