package org.hzero.scheduler.infra.util;

import java.io.IOException;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.hzero.core.base.BaseConstants;
import org.hzero.scheduler.infra.constant.HsdrConstant;
import org.hzero.scheduler.infra.constant.HsdrErrorCode;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.choerodon.core.convertor.ApplicationContextHelper;
import io.choerodon.core.exception.CommonException;

/**
 * 判断字符串是否为json格式
 *
 * @author shuangfei.zhu@hand-china.com 2018/09/14 14:47
 */
public class ValidUtils {

    private ValidUtils() {
    }

    /**
     * 校验json字符串
     */
    public static void isJsonValid(String jsonString) {
        if (StringUtils.isBlank(jsonString)) {
            return;
        }
        try {
            ObjectMapper mapper = ApplicationContextHelper.getContext().getBean(ObjectMapper.class);
            mapper.readTree(jsonString);
        } catch (IOException e) {
            throw new CommonException(HsdrErrorCode.WRONG_FORMAT, e);
        }
    }

    /**
     * 校验执行器地址
     */
    public static boolean checkAddressList(String addressList) {
        List<String> list = AddressUtils.getAddressList(addressList);
        if (CollectionUtils.isEmpty(list)) {
            return true;
        }
        for (String item : list) {
            if (item.contains(HsdrConstant.COLON)) {
                // ip端口号形式
                String[] address = item.split(HsdrConstant.COLON);
                if (address.length != BaseConstants.Digital.TWO) {
                    return false;
                }
                if (!checkAddress(address[0]) || !checkPort(address[1])) {
                    return false;
                }
            } else {
                // 域名形式
                if (!checkUrl(item)) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * 校验域名
     */
    private static boolean checkUrl(String s) {
        return s.matches("^([A-Z]|[a-z]|[0-9]|[`~!@#$%^&*()+=|{}':;',\\\\[\\\\].<>/?~！@#￥%……&*（）――+|{}【】‘；：”“'。，、？]){6,20}$");
    }

    /**
     * 校验地址是否为ip+端口号  允许有contextPath 例如/hrpt
     */
    public static boolean isIpAndPort(String s) {
        if (s.contains(BaseConstants.Symbol.SLASH)) {
            s = s.substring(0, s.indexOf(BaseConstants.Symbol.SLASH));
        }
        String[] arr = s.split(BaseConstants.Symbol.COLON);
        if (arr.length != BaseConstants.Digital.TWO) {
            return false;
        }
        return checkAddress(arr[0]) && checkPort(arr[1]);
    }

    /**
     * 校验ip
     */
    public static boolean checkAddress(String s) {
        return s.matches("((25[0-5]|2[0-4]\\d|((1\\d{2})|([1-9]?\\d)))\\.){3}(25[0-5]|2[0-4]\\d|((1\\d{2})|([1-9]?\\d)))");
    }

    /**
     * 校验端口号
     */
    private static boolean checkPort(String s) {
        int port = Integer.parseInt(s);
        return port >= 1 && port <= 65535;
    }
}
