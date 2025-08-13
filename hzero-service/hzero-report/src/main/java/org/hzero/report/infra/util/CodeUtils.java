package org.hzero.report.infra.util;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.EnumMap;
import java.util.Map;
import javax.imageio.ImageIO;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.oned.Code128Writer;
import com.google.zxing.oned.Code39Writer;
import com.google.zxing.oned.Code93Writer;
import com.google.zxing.qrcode.QRCodeWriter;
import org.apache.commons.lang3.StringUtils;
import org.hzero.report.infra.constant.HrptConstants;
import org.hzero.report.infra.constant.HrptMessageConstants;
import org.hzero.report.infra.enums.BarCodeType;

import io.choerodon.core.exception.CommonException;

/**
 * 生成图形码工具类
 *
 * @author fanghan.liu 2019/12/13 12:16
 */
public class CodeUtils {

    private static final String QR_CODE = "QR";
    private static final String BAR_CODE = "BAR";

    private CodeUtils() {
    }

    public static byte[] generateCode(String codeType, String text, Integer width, Integer height, String characterEncoding, String barCodeType) {
        if (StringUtils.isBlank(text)) {
            return new byte[0];
        }
        if (codeType == null) {
            codeType = QR_CODE;
        }
        switch (codeType) {
            case QR_CODE:
                if (width == null || width <= 0) {
                    width = 240;
                }
                if (height == null || height <= 0) {
                    height = 240;
                }
                return generateQrCode(text, width, height, characterEncoding);
            case BAR_CODE:
                if (width == null || width <= 0) {
                    width = 300;
                }
                if (height == null || height <= 0) {
                    height = 50;
                }
                return generateBarCode(text, width, height, characterEncoding, barCodeType);
            default:
                break;
        }
        return new byte[0];
    }

    /**
     * 生成二维码
     *
     * @param text              内容
     * @param width             宽
     * @param height            高
     * @param characterEncoding 字符编码
     * @return 二进制内容
     */
    public static byte[] generateQrCode(String text, int width, int height, String characterEncoding) {
        Map<EncodeHintType, Object> config = new EnumMap<>(EncodeHintType.class);
        config.put(EncodeHintType.CHARACTER_SET, characterEncoding);
        try (ByteArrayOutputStream stream = new ByteArrayOutputStream()) {
            BitMatrix bar = new QRCodeWriter().encode(text, BarcodeFormat.QR_CODE, width, height, config);
            BufferedImage image = removeBorder(bar);
            ImageIO.write(image, HrptConstants.ImageType.PNG, stream);
            return stream.toByteArray();
        } catch (Exception e) {
            throw new CommonException(HrptMessageConstants.ERROR_GENERATE_QRCODE, e);
        }
    }

    /**
     * 生成条形码
     *
     * @param text              内容
     * @param width             宽
     * @param height            高
     * @param characterEncoding 字符编码
     * @param barCodeType       条形码类型
     * @return 二进制内容
     */
    public static byte[] generateBarCode(String text, int width, int height, String characterEncoding, String barCodeType) {
        BarCodeType codeType = BarCodeType.value(barCodeType);
        switch (codeType) {
            case CODE_39:
                return generateBarCode39(text, width, height, characterEncoding);
            case CODE_93:
                return generateBarCode93(text, width, height, characterEncoding);
            case CODE_128:
                return generateBarCode128(text, width, height, characterEncoding);
            default:
                throw new CommonException(HrptMessageConstants.UNSUPPORTED_CODE_TYPE);
        }

    }

    /**
     * 生成Code39条形码
     *
     * @param text              内容
     * @param width             宽
     * @param height            高
     * @param characterEncoding 字符编码
     * @return 二进制内容
     */
    public static byte[] generateBarCode39(String text, int width, int height, String characterEncoding) {
        Code39Writer writer = new Code39Writer();
        Map<EncodeHintType, Object> config = new EnumMap<>(EncodeHintType.class);
        config.put(EncodeHintType.CHARACTER_SET, characterEncoding);
        try (ByteArrayOutputStream stream = new ByteArrayOutputStream()) {
            BitMatrix bar = writer.encode(text, BarcodeFormat.CODE_39, width, height, config);
            BufferedImage image = removeBorder(bar);
            ImageIO.write(image, HrptConstants.ImageType.PNG, stream);
            return stream.toByteArray();
        } catch (Exception e) {
            throw new CommonException(HrptMessageConstants.ERROR_GENERATE_BARCODE, e);
        }
    }

    /**
     * 生成Code93条形码
     *
     * @param text              内容
     * @param width             宽
     * @param height            高
     * @param characterEncoding 字符编码
     * @return 二进制内容
     */
    public static byte[] generateBarCode93(String text, int width, int height, String characterEncoding) {
        Code93Writer writer = new Code93Writer();
        Map<EncodeHintType, Object> config = new EnumMap<>(EncodeHintType.class);
        config.put(EncodeHintType.CHARACTER_SET, characterEncoding);
        try (ByteArrayOutputStream stream = new ByteArrayOutputStream()) {
            BitMatrix bar = writer.encode(text, BarcodeFormat.CODE_93, width, height, config);
            BufferedImage image = removeBorder(bar);
            ImageIO.write(image, HrptConstants.ImageType.PNG, stream);
            return stream.toByteArray();
        } catch (Exception e) {
            throw new CommonException(HrptMessageConstants.ERROR_GENERATE_BARCODE, e);
        }
    }

    /**
     * 生成Code128条形码
     *
     * @param text              内容
     * @param width             宽
     * @param height            高
     * @param characterEncoding 字符编码
     * @return 二进制内容
     */
    public static byte[] generateBarCode128(String text, int width, int height, String characterEncoding) {
        Code128Writer writer = new Code128Writer();
        Map<EncodeHintType, Object> config = new EnumMap<>(EncodeHintType.class);
        config.put(EncodeHintType.CHARACTER_SET, characterEncoding);
        try (ByteArrayOutputStream stream = new ByteArrayOutputStream()) {
            BitMatrix bar = writer.encode(text, BarcodeFormat.CODE_128, width, height, config);
            BufferedImage image = removeBorder(bar);
            ImageIO.write(image, HrptConstants.ImageType.PNG, stream);
            return stream.toByteArray();
        } catch (Exception e) {
            throw new CommonException(HrptMessageConstants.ERROR_GENERATE_BARCODE, e);
        }
    }

    /**
     * 去除白边
     */
    private static BufferedImage removeBorder(BitMatrix bitMatrix) {
        int[] rec = bitMatrix.getEnclosingRectangle();
        int resWidth = rec[2];
        int resHeight = rec[3];
        BitMatrix resMatrix = new BitMatrix(resWidth, resHeight);
        resMatrix.clear();
        for (int i = 0; i < resWidth; i++) {
            for (int j = 0; j < resHeight; j++) {
                if (bitMatrix.get(i + rec[0], j + rec[1])) {
                    resMatrix.set(i, j);
                }
            }
        }
        int w = resMatrix.getWidth();
        int h = resMatrix.getHeight();
        BufferedImage image = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        for (int x = 0; x < w; x++) {
            for (int y = 0; y < h; y++) {
                image.setRGB(x, y, resMatrix.get(x, y) ?
                        Color.BLACK.getRGB() : Color.WHITE.getRGB());
            }
        }
        return image;
    }
}
