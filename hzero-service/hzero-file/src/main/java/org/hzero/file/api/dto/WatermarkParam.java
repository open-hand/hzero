package org.hzero.file.api.dto;

/**
 * 水印参数
 *
 * @author shuangfei.zhu@hand-china.com 2019/12/18 10:36
 */
public class WatermarkParam {

    /**
     * 透明度
     */
    private Float fillOpacity = 0.4F;
    /**
     * 颜色
     */
    private Integer red = 0;
    private Integer green = 0;
    private Integer blue = 255;
    /**
     * 字体大小
     */
    private Float size = 60F;
    /**
     * 坐标
     */
    private Float x = 300F;
    private Float y = 500F;
    /**
     * 对齐方式  0左对齐 1居中 2右对齐
     */
    private Integer align = 1;
    /**
     * 水印文本
     */
    private String text = "";
    /**
     * 角度
     */
    private Float rotation = 45F;

    public Float getFillOpacity() {
        return fillOpacity;
    }

    public WatermarkParam setFillOpacity(Float fillOpacity) {
        this.fillOpacity = fillOpacity;
        return this;
    }

    public Integer getRed() {
        return red;
    }

    public WatermarkParam setRed(Integer red) {
        this.red = red;
        return this;
    }

    public Integer getGreen() {
        return green;
    }

    public WatermarkParam setGreen(Integer green) {
        this.green = green;
        return this;
    }

    public Integer getBlue() {
        return blue;
    }

    public WatermarkParam setBlue(Integer blue) {
        this.blue = blue;
        return this;
    }

    public Float getSize() {
        return size;
    }

    public WatermarkParam setSize(Float size) {
        this.size = size;
        return this;
    }

    public Float getX() {
        return x;
    }

    public WatermarkParam setX(Float x) {
        this.x = x;
        return this;
    }

    public Float getY() {
        return y;
    }

    public WatermarkParam setY(Float y) {
        this.y = y;
        return this;
    }

    public Integer getAlign() {
        return align;
    }

    public WatermarkParam setAlign(Integer align) {
        this.align = align;
        return this;
    }

    public String getText() {
        return text;
    }

    public WatermarkParam setText(String text) {
        this.text = text;
        return this;
    }

    public Float getRotation() {
        return rotation;
    }

    public WatermarkParam setRotation(Float rotation) {
        this.rotation = rotation;
        return this;
    }
}
