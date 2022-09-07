package org.hzero.file.app.service.impl;

import java.awt.*;
import java.io.*;
import java.math.BigInteger;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.swing.*;

import com.itextpdf.text.Image;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.hzero.common.HZeroService;
import org.hzero.core.base.BaseConstants;
import org.hzero.core.util.FilenameUtils;
import org.hzero.file.app.service.PreviewService;
import org.hzero.file.app.service.WatermarkConfigService;
import org.hzero.file.app.service.WatermarkService;
import org.hzero.file.domain.entity.WatermarkConfig;
import org.hzero.file.domain.repository.FileRepository;
import org.hzero.file.infra.constant.HfleConstant;
import org.hzero.file.infra.constant.HfleMessageConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import io.choerodon.core.exception.CommonException;

/**
 * 水印
 *
 * @author shuangfei.zhu@hand-china.com 2020/02/14 14:32
 */
@Service
public class WatermarkServiceImpl implements WatermarkService {

    @Autowired
    private WatermarkConfigService watermarkConfigService;
    @Autowired
    private PreviewService previewService;
    @Autowired
    private FileRepository fileRepository;

    private static final Long INTERVAL = -40L;

    private static final String FONT_URL = System.getProperty("user.dir") + File.separator + "fonts";

    @Override
    public void watermarkWithConfigByKey(HttpServletRequest request, HttpServletResponse response, Long tenantId, String fileKey, String watermarkCode, String context) {
        Assert.isTrue(fileKey.endsWith("pdf"), HfleMessageConstant.FILE_TYPE);
        InputStream inputStream = previewService.downloadFileByKey(request, tenantId, fileKey);
        WatermarkConfig config = watermarkConfigService.getConfig(tenantId, watermarkCode);
        // 若水印类型为图片，根据文件key获取文件url和bucket
        String contextBucket = null;
        if (StringUtils.isNotBlank(context) && config.getWatermarkType().endsWith(HfleConstant.WatermarkType.IMAGE)) {
            org.hzero.file.domain.entity.File file = this.fileRepository.selectOne(new org.hzero.file.domain.entity.File().setTenantId(tenantId).setFileKey(fileKey));
            Assert.notNull(file, BaseConstants.ErrorCode.DATA_NOT_EXISTS);
            context = file.getFileUrl();
            contextBucket = file.getBucketName();
        }
        if (StringUtils.isNotBlank(context)) {
            // 自定义水印内容，覆盖默认配置
            config.setDetail(context);
            config.setContextBucket(contextBucket);
        }
        watermark(request, response, config, FilenameUtils.getFileName(fileKey), inputStream);
    }

    @Override
    public void watermarkWithConfigByUrl(HttpServletRequest request, HttpServletResponse response, Long tenantId, String storageCode,
                                         String bucketName, String url, String watermarkCode, String context, String contextBucket) {
        Assert.isTrue(url.endsWith("pdf"), HfleMessageConstant.FILE_TYPE);
        InputStream inputStream = previewService.downloadFileByUrl(request, tenantId, storageCode, bucketName, url);
        WatermarkConfig config = watermarkConfigService.getConfig(tenantId, watermarkCode);
        if (StringUtils.isNotBlank(context)) {
            // 自定义水印内容，覆盖默认配置
            config.setDetail(context);
            config.setContextBucket(contextBucket);
        }
        watermark(request, response, config, FilenameUtils.getFileName(url), inputStream);
    }

    @Override
    public void preview(HttpServletRequest request, HttpServletResponse response, Long tenantId, String watermarkCode) throws DocumentException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        Document document = new Document();
        PdfWriter.getInstance(document, outputStream);
        document.open();
        document.add(new Paragraph(" "));
        document.close();
        ByteArrayInputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());
        WatermarkConfig config = watermarkConfigService.getConfig(tenantId, watermarkCode);
        watermark(request, response, config, "demo.pdf", inputStream);
    }

    private void watermark(HttpServletRequest request, HttpServletResponse response, WatermarkConfig config, String filename, InputStream inputStream) {
        switch (config.getWatermarkType()) {
            case HfleConstant.WatermarkType.IMAGE:
                watermarkImage(request, response, filename, inputStream, config);
                break;
            case HfleConstant.WatermarkType.TEXT:
                watermarkText(request, response, filename, inputStream, config);
                break;
            case HfleConstant.WatermarkType.TILE_IMAGE:
                watermarkTileImage(request, response, filename, inputStream, config);
                break;
            case HfleConstant.WatermarkType.TILE_TEXT:
                watermarkTileText(request, response, filename, inputStream, config);
                break;
            default:
                break;
        }
    }

    private void watermarkImage(HttpServletRequest request, HttpServletResponse response, String filename, InputStream inputStream, WatermarkConfig config) {
        try {
            response.setHeader("Content-Disposition", "inline;filename=" + FilenameUtils.encodeFileName(request, filename));
            response.setContentType("application/pdf");
            response.setHeader("Cache-Control", "must-revalidate, post-check=0, pre-check=0");
            response.setHeader("Pragma", "public");
            response.setDateHeader("Expires", (System.currentTimeMillis() + 1000));
            OutputStream outputStream = response.getOutputStream();
            PdfReader pdfReader = new PdfReader(inputStream);
            PdfStamper stamper = new PdfStamper(pdfReader, outputStream);
            int total = pdfReader.getNumberOfPages() + 1;
            PdfContentByte content;
            PdfGState gs = new PdfGState();
            // 设置填充字体不透明度
            gs.setFillOpacity(config.getFillOpacity().floatValue());
            // 水印图片
            InputStream im = previewService.downloadFileByUrl(request, config.getTenantId(), null,
                    StringUtils.isBlank(config.getContextBucket()) ? HZeroService.File.BUCKET_NAME : config.getContextBucket(), config.getDetail());
            Image image = Image.getInstance(IOUtils.toByteArray(im));
            // 设置坐标 绝对位置 X Y
            image.setAbsolutePosition(config.getxAxis(), config.getyAxis());
            // 设置旋转角度
            image.setRotationDegrees(config.getRotation());
            //自定义大小
            image.scaleAbsolute(config.getWeight(), config.getHeight());
            // 循环对每页插入水印
            for (int i = 1; i < total; i++) {
                // 水印的起始
                content = stamper.getOverContent(i);
                // 开始
                content.beginText();
                // 设置透明度
                content.setGState(gs);
                // 添加水印图片
                content.addImage(image);
                content.endText();
            }
            stamper.close();
            inputStream.close();
        } catch (Exception e) {
            throw new CommonException(HfleMessageConstant.PREVIEW, e);
        }
    }

    private void watermarkTileImage(HttpServletRequest request, HttpServletResponse response, String filename, InputStream inputStream, WatermarkConfig config) {
        try {
            response.setHeader("Content-Disposition", "inline;filename=" + FilenameUtils.encodeFileName(request, filename));
            response.setContentType("application/pdf");
            response.setHeader("Cache-Control", "must-revalidate, post-check=0, pre-check=0");
            response.setHeader("Pragma", "public");
            response.setDateHeader("Expires", (System.currentTimeMillis() + 1000));
            OutputStream outputStream = response.getOutputStream();
            PdfReader pdfReader = new PdfReader(inputStream);
            PdfStamper stamper = new PdfStamper(pdfReader, outputStream);
            int total = pdfReader.getNumberOfPages() + 1;
            PdfContentByte content;
            PdfGState gs = new PdfGState();
            // 设置填充字体不透明度
            gs.setFillOpacity(config.getFillOpacity().floatValue());
            // 水印图片
            InputStream im = previewService.downloadFileByUrl(request, config.getTenantId(), null,
                    StringUtils.isBlank(config.getContextBucket()) ? HZeroService.File.BUCKET_NAME : config.getContextBucket(), config.getDetail());
            Image image = Image.getInstance(IOUtils.toByteArray(im));
            // 设置旋转角度
            image.setRotationDegrees(config.getRotation());
            //自定义大小
            int h = Math.toIntExact(config.getHeight());
            int w = Math.toIntExact(config.getWeight());
            image.scaleAbsolute(w, h);
            Rectangle pageRect;
            for (int i = 1; i < total; i++) {
                pageRect = pdfReader.getPageSizeWithRotation(i);
                // 循环对每页插入水印
                // 水印的起始
                content = stamper.getOverContent(i);
                // 开始
                content.beginText();
                // 设置透明度
                content.setGState(gs);
                for (long height = INTERVAL + h; height < pageRect.getHeight() + h; height = height + h + config.getyAxis()) {
                    for (long width = INTERVAL + w; width < pageRect.getWidth() + w; width = width + w + config.getxAxis()) {
                        // 设置坐标 绝对位置 X Y
                        image.setAbsolutePosition(width, height);
                        // 添加水印图片
                        content.addImage(image);
                    }
                }
                content.endText();
            }
            stamper.close();
            inputStream.close();
        } catch (Exception e) {
            throw new CommonException(HfleMessageConstant.PREVIEW, e);
        }
    }

    private void watermarkText(HttpServletRequest request, HttpServletResponse response, String filename, InputStream inputStream, WatermarkConfig config) {
        try {
            response.setHeader("Content-Disposition", "inline;filename=" + FilenameUtils.encodeFileName(request, filename));
            response.setContentType("application/pdf");
            response.setHeader("Cache-Control", "must-revalidate, post-check=0, pre-check=0");
            response.setHeader("Pragma", "public");
            response.setDateHeader("Expires", (System.currentTimeMillis() + 1000));
            OutputStream outputStream = response.getOutputStream();
            PdfReader pdfReader = new PdfReader(inputStream);
            PdfStamper stamper = new PdfStamper(pdfReader, outputStream);
            int total = pdfReader.getNumberOfPages() + 1;
            PdfContentByte content;
            // 设置字体
            BaseFont font;
            if (StringUtils.isNotBlank(config.getFontUrl())) {
                font = BaseFont.createFont(getFontFile(request, config.getTenantId(), config.getFontUrl()), BaseFont.IDENTITY_H, BaseFont.NOT_EMBEDDED);
            } else {
                font = BaseFont.createFont();
            }
            PdfGState gs = new PdfGState();
            // 设置填充字体不透明度
            gs.setFillOpacity(config.getFillOpacity().floatValue());
            // 循环对每页插入水印
            for (int i = 1; i < total; i++) {
                // 水印的起始
                content = stamper.getOverContent(i);
                // 开始
                content.beginText();
                // 设置颜色 默认为蓝色
                content.setColorFill(getColor(config.getColor()));
                // 设置透明度
                content.setGState(gs);
                // 设置字体及字号
                content.setFontAndSize(font, config.getFontSize());
                // 设置起始位置
                content.setTextMatrix(config.getxAxis(), config.getyAxis());
                // 开始写入水印
                content.showTextAligned(config.getAlign(), config.getDetail(), config.getxAxis(), config.getyAxis(), config.getRotation());
                content.endText();
            }
            stamper.close();
            inputStream.close();
        } catch (Exception e) {
            throw new CommonException(HfleMessageConstant.PREVIEW, e);
        }
    }

    private void watermarkTileText(HttpServletRequest request, HttpServletResponse response, String filename, InputStream inputStream, WatermarkConfig config) {
        try {
            response.setHeader("Content-Disposition", "inline;filename=" + FilenameUtils.encodeFileName(request, filename));
            response.setContentType("application/pdf");
            response.setHeader("Cache-Control", "must-revalidate, post-check=0, pre-check=0");
            response.setHeader("Pragma", "public");
            response.setDateHeader("Expires", (System.currentTimeMillis() + 1000));
            OutputStream outputStream = response.getOutputStream();
            PdfReader pdfReader = new PdfReader(inputStream);
            PdfStamper stamper = new PdfStamper(pdfReader, outputStream);
            int total = pdfReader.getNumberOfPages() + 1;
            PdfContentByte content;
            // 设置字体
            BaseFont font;
            if (StringUtils.isNotBlank(config.getFontUrl())) {
                font = BaseFont.createFont(getFontFile(request, config.getTenantId(), config.getFontUrl()), BaseFont.IDENTITY_H, BaseFont.NOT_EMBEDDED);
            } else {
                font = BaseFont.createFont();
            }
            // 获取水印大小
            JLabel label = new JLabel();
            FontMetrics metrics;
            label.setText(config.getDetail());
            metrics = label.getFontMetrics(label.getFont());
            int textH = metrics.getHeight();
            int textW = metrics.stringWidth(label.getText());
            PdfGState gs = new PdfGState();
            // 设置填充字体不透明度
            gs.setFillOpacity(config.getFillOpacity().floatValue());
            Rectangle pageRect;
            // 循环对每页插入水印
            for (int i = 1; i < total; i++) {
                pageRect = pdfReader.getPageSizeWithRotation(i);
                // 水印的起始
                content = stamper.getOverContent(i);
                // 开始
                content.beginText();
                // 设置颜色 默认为蓝色
                content.setColorFill(getColor(config.getColor()));
                // 设置透明度
                content.setGState(gs);
                // 设置字体及字号
                content.setFontAndSize(font, config.getFontSize());
                // 设置起始位置
                content.setTextMatrix(config.getxAxis(), config.getyAxis());
                // 开始写入水印
                for (long height = INTERVAL + textH; height < pageRect.getHeight() + textH; height = height + textH + config.getyAxis()) {
                    for (long width = INTERVAL + textW; width < pageRect.getWidth() + textW; width = width + textW + config.getxAxis()) {
                        content.showTextAligned(config.getAlign(), config.getDetail(), width, height, config.getRotation());
                    }
                }
                content.endText();
            }
            stamper.close();
            inputStream.close();
        } catch (Exception e) {
            throw new CommonException(HfleMessageConstant.PREVIEW, e);
        }
    }

    private String getFontFile(HttpServletRequest request, Long tenantId, String fontUrl) throws IOException {
        // 判断并创建文件夹
        File dir = new File(FONT_URL);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        String filePath = FONT_URL + File.separator + FilenameUtils.getFileName(fontUrl);
        // 判断字体文件是否存在
        File file = new File(filePath);
        if (file.exists()) {
            return filePath;
        }
        InputStream inputStream = previewService.downloadFileByUrl(request, tenantId, null, HZeroService.File.BUCKET_NAME, fontUrl);
        // 字体文件写入本地
        writeToLocal(filePath, inputStream);
        return filePath;
    }


    /**
     * 将InputStream写入本地文件
     *
     * @param destination 写入本地目录
     * @param input       输入流
     * @throws IOException IOException
     */
    private static void writeToLocal(String destination, InputStream input) throws IOException {
        int index;
        byte[] bytes = new byte[1024];
        FileOutputStream downloadFile = new FileOutputStream(destination);
        while ((index = input.read(bytes)) != -1) {
            downloadFile.write(bytes, 0, index);
            downloadFile.flush();
        }
        downloadFile.close();
        input.close();
    }

    /**
     * 获取字体颜色
     */
    private BaseColor getColor(String color) {
        String red = color.substring(1, 3);
        String green = color.substring(3, 5);
        String blue = color.substring(5, 7);
        return new BaseColor(decodeHex(red), decodeHex(green), decodeHex(blue));
    }

    /**
     * 16进制转10进制
     */
    public int decodeHex(String hex) {
        BigInteger bigint = new BigInteger(hex, 16);
        return bigint.intValue();
    }
}
