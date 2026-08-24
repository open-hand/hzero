package org.hzero.boot.report.app;

import java.io.ByteArrayOutputStream;
import java.util.Map;

/**
 * description
 *
 * @author shuangfei.zhu@hand-china.com 2020/06/23 19:19
 */
public interface IReportService {

    /**
     * 生成报表
     *
     * @param params 参数
     * @return 输出流
     */
    ByteArrayOutputStream execute(Map<String, String> params);

    /**
     * 文件名
     *
     * @return 文件名
     */
    default String filename() {
        return System.currentTimeMillis() + ".xlsx";
    }
}
