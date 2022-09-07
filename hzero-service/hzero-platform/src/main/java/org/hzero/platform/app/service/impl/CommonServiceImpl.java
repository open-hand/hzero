package org.hzero.platform.app.service.impl;

import org.hzero.boot.platform.lov.adapter.LovAdapter;
import org.hzero.boot.platform.lov.dto.LovValueDTO;
import org.hzero.common.HZeroConstant;
import org.hzero.core.base.BaseConstants;
import org.hzero.platform.app.service.CommonService;
import org.hzero.platform.infra.constant.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 通用接口 Service实现类
 *
 * @author xiaoyu.zhao@hand-china.com 2019/04/23 20:37
 */
@Service
public class CommonServiceImpl implements CommonService {
    @Autowired
    private LovAdapter lovAdapter;

    @Override
    public List<Map<String, String>> listIDD() {
        List<Map<String, String>> result = new ArrayList<>();
        List<LovValueDTO> lov = lovAdapter.queryLovValue(Constants.IDD_LOV_CODE, HZeroConstant.SITE_TENANT_ID);
        lov = lov.stream().sorted(Comparator.comparing(LovValueDTO::getOrderSeq)).collect(Collectors.toList());
        lov.forEach( item -> {
            Map<String, String> map = new HashMap<>(BaseConstants.Digital.FOUR);
            map.put("internationalTelCode", item.getValue());
            map.put("internationalTelMeaning", item.getMeaning());
            result.add(map);
        });
        return result;
    }
}
