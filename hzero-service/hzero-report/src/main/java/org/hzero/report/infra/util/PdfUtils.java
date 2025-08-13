package org.hzero.report.infra.util;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

import org.hzero.report.infra.constant.HrptConstants;
import org.hzero.report.infra.factory.Base64ImgReplacedElementFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xhtmlrenderer.pdf.ITextFontResolver;
import org.xhtmlrenderer.pdf.ITextRenderer;

import com.itextpdf.text.pdf.BaseFont;

/**
 * PDF处理工具类
 *
 * @author xianzhi.chen@hand-china.com 2018年12月14日下午5:54:12
 */
public class PdfUtils {

    private PdfUtils() {
    }

    protected static final Logger logger = LoggerFactory.getLogger(PdfUtils.class);

    /**
     * 获取带简单样式的HTML的TABLE标签
     */
    public static String getStyleHtml(String html) {
        return html;
    }

    /**
     * Html转PDF
     */
    public static void htmlToPdf(String html, OutputStream os) throws IOException {
        try {
            ITextRenderer renderer = new ITextRenderer();
            renderer.setDocumentFromString(html);
            ITextFontResolver fontResolver = renderer.getFontResolver();
            // 获取字体文件路径
            fontResolver.addFont(getFontPath(), BaseFont.IDENTITY_H, BaseFont.NOT_EMBEDDED);
            renderer.layout();
            renderer.createPDF(os);
            os.flush();
        } catch (Exception e) {
            logger.error("Html To Pdf Failed", e);
        } finally {
            if (os != null) {
                os.close();
            }
        }
    }

    public static void labelHtmlToPdf(String html, OutputStream os) {
        try {
            html = html.replace("&nbsp;", "");
            ITextRenderer renderer = new ITextRenderer();
            // 图片base64支持，把图片转换为itext自己的图片对象
            renderer.getSharedContext().setReplacedElementFactory(new Base64ImgReplacedElementFactory());
            renderer.getSharedContext().getTextRenderer().setSmoothingThreshold(0);
            renderer.setDocumentFromString(html);
            // 解决中文显示问题
            ITextFontResolver fontResolver = renderer.getFontResolver();
            fontResolver.addFont(getFontPath(), BaseFont.IDENTITY_H, BaseFont.NOT_EMBEDDED);
            // 图片相对路径问题
            renderer.layout();
            renderer.createPDF(os);
            os.close();
        } catch (Exception e) {
            logger.error("Html To Pdf Failed", e);
        }
    }

    /**
     * 获取字体的绝对路径
     */
    private static String getFontPath() {
        return System.getProperty("user.dir") + File.separator + HrptConstants.CONFIG_PATH + File.separator + HrptConstants.TTF_NAME;
    }

}
