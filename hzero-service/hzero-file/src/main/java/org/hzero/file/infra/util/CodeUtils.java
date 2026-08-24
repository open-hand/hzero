package org.hzero.file.infra.util;

import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.hzero.file.infra.constant.HfleConstant;
import org.hzero.file.infra.constant.HfleMessageConstant;

import io.choerodon.core.exception.CommonException;

/**
 * decode工具
 *
 * @author shuangfei.zhu@hand-china.com 2019/03/15 9:34
 */
public class CodeUtils {

    private CodeUtils() {
    }

    /**
     * 校验文件名是否存在特殊字符
     *
     * @param fileName 文件名
     */
    public static void checkFileName(String fileName) {
        try {
            String name = URLDecoder.decode(fileName, HfleConstant.DEFAULT_CHARACTER_SET);
            if (!Objects.equals(name, fileName)) {
                throw new CommonException(HfleMessageConstant.FILE_NAME_ERROR);
            }
        } catch (Exception e) {
            throw new CommonException(HfleMessageConstant.FILE_NAME_ERROR, e);
        }
    }

    public static String decode(String str) {
        try {
            return URLDecoder.decode(str, HfleConstant.DEFAULT_CHARACTER_SET);
        } catch (Exception e) {
            return str;
        }
    }

    public static List<String> decode(List<String> str) {
        List<String> result = new ArrayList<>();
        str.forEach(item -> {
            try {
                result.add(URLDecoder.decode(item, HfleConstant.DEFAULT_CHARACTER_SET));
            } catch (Exception e) {
                result.add(item);
            }
        });
        return result;
    }
}
