package org.hzero.report.infra.init;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.CollectionUtils;
import org.hzero.core.base.BaseConstants;
import org.hzero.report.infra.config.ReportConfig;
import org.hzero.report.infra.constant.HrptConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

/**
 * description
 *
 * @author shuangfei.zhu@hand-china.com 2019/04/10 14:32
 */
@Component
public class ReportServiceInit implements InitializingBean {

    private static final Logger logger = LoggerFactory.getLogger(ReportServiceInit.class);

    @Autowired
    private ReportConfig config;

    private static final Map<String, String> MAP = new HashMap<>(16);

    @Override
    public void afterPropertiesSet() {
        // 服务启动后替换字体配置文件的字体路径
        String root = System.getProperty("user.dir");
        InputStream defaultFont = getClass().getClassLoader().getResourceAsStream("font/" + HrptConstants.TTF_NAME);
        writeToLocal(root + File.separator + HrptConstants.CONFIG_PATH, HrptConstants.TTF_NAME, defaultFont);
        // 自己添加的服务字体
        List<String> srcFontList = config.getSrcFontPaths();
        if (CollectionUtils.isNotEmpty(srcFontList)) {
            for (String item : srcFontList) {
                String[] src = item.split(HrptConstants.FontPath.SEPARATOR);
                if (src.length != BaseConstants.Digital.TWO) {
                    logger.error("error font config");
                    continue;
                }
                String path = src[1];
                String filename = path.substring(path.lastIndexOf('/') + 1);
                MAP.put(filename, src[0]);
                InputStream font = getClass().getClassLoader().getResourceAsStream(path);
                writeToLocal(root + File.separator + HrptConstants.CONFIG_PATH, filename, font);
            }
        }
        InputStream cfg = getClass().getClassLoader().getResourceAsStream(HrptConstants.CFG_NAME);
        if (cfg != null) {
            InputStream is = autoReplace(cfg, buildFontXml(root + File.separator + HrptConstants.CONFIG_PATH));
            writeToLocal(root + File.separator + HrptConstants.CONFIG_PATH, HrptConstants.CFG_NAME, is);
        }
    }

    /**
     * 组装字体配置
     *
     * @param fontPath 字体文件路径
     * @return 字体配置
     */
    private String buildFontXml(String fontPath) {
        StringBuilder sb = new StringBuilder();
        sb.append(getFontXml(HrptConstants.FontPath.DEFAULT_FONT_NAME, fontPath + File.separator + HrptConstants.TTF_NAME));
        sb.append(getFontXml(HrptConstants.FontPath.DEFAULT_FONT_NAME_EN, fontPath + File.separator + HrptConstants.TTF_NAME));
        // 处理服务字体
        File[] fonts = new File(fontPath).listFiles();
        if (fonts != null) {
            for (File font : fonts) {
                if (!font.isFile()) {
                    continue;
                }
                String fontName = font.getName();
                if (HrptConstants.TTF_NAME.equals(fontName) || HrptConstants.CFG_NAME.equals(fontName)) {
                    continue;
                }
                sb.append(getFontXml(MAP.getOrDefault(fontName, fontName.split("\\.")[0]), fontPath + File.separator + fontName));
            }
        }
        // 处理系统字体文件
        List<String> paths = config.getFontPaths();
        if (CollectionUtils.isNotEmpty(paths)) {
            for (String item : paths) {
                String[] path = item.split(HrptConstants.FontPath.SEPARATOR);
                if (path.length != BaseConstants.Digital.TWO) {
                    logger.error("error font config");
                    continue;
                }
                sb.append(getFontXml(path[0], path[1]));
            }
        }
        return sb.toString();
    }

    /**
     * 生成替换后的xdo.cfg
     *
     * @param in      输入流
     * @param fontXml 字体配置
     * @return 完整的xdo.cfg
     */
    private InputStream autoReplace(InputStream in, String fontXml) {
        byte[] fileContext = new byte[1024];
        String str = "";
        try {
            in.read(fileContext);
            str = new String(fileContext, StandardCharsets.UTF_8);
            str = str.replace(HrptConstants.FontPath.FONT_XML, fontXml);

        } catch (IOException e) {
            logger.error(e.toString());
        } finally {
            try {
                in.close();
            } catch (IOException e) {
                logger.error(e.toString());
            }
        }
        return new ByteArrayInputStream(str.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * 保存文件到本地
     *
     * @param path     路径
     * @param fileName 文件名
     * @param input    输入流
     */
    private static void writeToLocal(String path, String fileName, InputStream input) {
        try {
            int index;
            byte[] bytes = new byte[1024];
            // 创建目录
            File fileIo = new File(path);
            if (!fileIo.exists()) {
                boolean flag = fileIo.mkdirs();
                Assert.isTrue(flag, BaseConstants.ErrorCode.ERROR);
            }
            FileOutputStream downloadFile = new FileOutputStream(path + File.separator + fileName);
            while ((index = input.read(bytes)) != -1) {
                downloadFile.write(bytes, 0, index);
                downloadFile.flush();
            }
            downloadFile.close();
            input.close();
        } catch (Exception e) {
            logger.error(e.toString());
        }
    }

    private String getFontXml(String fontName, String fontPath) {
        return HrptConstants.FontPath.XML.replace(HrptConstants.FontPath.FONT_NAME, fontName).replace(HrptConstants.FontPath.FONT_PATH, fontPath);
    }
}
