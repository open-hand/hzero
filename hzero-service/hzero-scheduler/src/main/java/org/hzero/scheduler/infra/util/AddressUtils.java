package org.hzero.scheduler.infra.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.hzero.scheduler.infra.constant.HsdrConstant;

/**
 * description
 *
 * @author shuangfei.zhu@hand-china.com 2020/02/24 10:44
 */
public class AddressUtils {

    private AddressUtils() {
    }

    public static String getAddressString(List<String> addressList) {
        StringBuilder sb = new StringBuilder();
        if (CollectionUtils.isEmpty(addressList)) {
            return StringUtils.EMPTY;
        }
        addressList.forEach(item -> sb.append(item).append(HsdrConstant.COMMA));
        return sb.substring(0, sb.length() - 1);
    }

    public static List<String> getAddressList(String addressStr) {
        return StringUtils.isNotBlank(addressStr) ? Arrays.asList(addressStr.split(HsdrConstant.COMMA)) : new ArrayList<>();
    }
}
