package org.hzero.core.captcha;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 验证码配置
 *
 * @author bojiangzhou 2018/08/08
 */
@ConfigurationProperties(prefix = CaptchaProperties.PREFIX)
public class CaptchaProperties {

    public static final String PREFIX = "hzero.captcha";

    /**
     * 启用验证码功能
     */
    private boolean enable = false;

    /**
     * 测试时禁用验证功能，不验证验证码是否正确
     */
    private boolean testDisable = false;

    /**
     * 图片验证码配置
     */
    private Image image = new Image();

    /**
     * 短信验证码配置
     */
    private Sms sms = new Sms();

    /**
     * 图片验证码
     */
    public static class Image {
        /**
         * 验证码过期时间(分)
         */
        private Integer expire = 10;
        /**
         * 图片宽度
         */
        private Integer width = 125;
        /**
         * 图片高度
         */
        private Integer height = 45;
        /**
         * 图片样式
         */
        private ImageStyle style = ImageStyle.WATER_RIPPLE;
        /**
         * 是否显示图片边框. yes/no
         */
        private YesNo border = YesNo.NO;
        /**
         * 边框颜色
         */
        private String borderColor = "black";
        /**
         * 边框厚度
         */
        private Integer borderThickness = 1;
        /**
         * 验证码字符来源，验证码值从中获取
         */
        private String charSource = "abcdefghijklmnopqrstuvwxyz1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        /**
         * 验证码字符长度
         */
        private Integer charLength = 4;
        /**
         * 文字之间的间隔. 大于0的数值
         */
        private Integer charSpace = 2;
        /**
         * 字体
         */
        private String fontNames = "Arial,Courier";
        /**
         * 字体大小
         */
        private Integer fontSize = 40;
        /**
         * 字体颜色
         */
        private String fontColor = "blue";
        /**
         * 干扰线颜色
         */
        private String noiseColor = "blue";
        /**
         * 背景颜色渐变，开始颜色
         */
        private String backgroundColorFrom = "lightGray";
        /**
         * 背景颜色渐变， 结束颜色
         */
        private String backgroundColorTo = "lightGray";

        enum YesNo {
            /**
             * yes
             */
            YES("yes"),
            /**
             * no
             */
            NO("no");

            private String value;

            YesNo(String value) {
                this.value = value;
            }

            public String value() {
                return value;
            }

        }

        enum ImageStyle {

            /**
             * 水纹
             */
            WATER_RIPPLE("com.google.code.kaptcha.impl.WaterRipple"),
            /**
             * 鱼眼
             */
            FISH_EYE("com.google.code.kaptcha.impl.FishEyeGimpy"),
            /**
             * 阴影
             */
            SHADOW("com.google.code.kaptcha.impl.ShadowGimpy");

            private String value;

            ImageStyle(String value) {
                this.value = value;
            }

            public String value() {
                return value;
            }
        }

        public Integer getExpire() {
            return expire;
        }

        public void setExpire(Integer expire) {
            this.expire = expire;
        }

        public Integer getWidth() {
            return width;
        }

        public void setWidth(Integer width) {
            this.width = width;
        }

        public Integer getHeight() {
            return height;
        }

        public void setHeight(Integer height) {
            this.height = height;
        }

        public ImageStyle getStyle() {
            return style;
        }

        public void setStyle(ImageStyle style) {
            this.style = style;
        }

        public YesNo getBorder() {
            return border;
        }

        public void setBorder(YesNo border) {
            this.border = border;
        }

        public String getBorderColor() {
            return borderColor;
        }

        public void setBorderColor(String borderColor) {
            this.borderColor = borderColor;
        }

        public Integer getBorderThickness() {
            return borderThickness;
        }

        public void setBorderThickness(Integer borderThickness) {
            this.borderThickness = borderThickness;
        }

        public String getCharSource() {
            return charSource;
        }

        public void setCharSource(String charSource) {
            this.charSource = charSource;
        }

        public Integer getCharLength() {
            return charLength;
        }

        public void setCharLength(Integer charLength) {
            this.charLength = charLength;
        }

        public Integer getCharSpace() {
            return charSpace;
        }

        public void setCharSpace(Integer charSpace) {
            this.charSpace = charSpace;
        }

        public String getFontNames() {
            return fontNames;
        }

        public void setFontNames(String fontNames) {
            this.fontNames = fontNames;
        }

        public Integer getFontSize() {
            return fontSize;
        }

        public void setFontSize(Integer fontSize) {
            this.fontSize = fontSize;
        }

        public String getFontColor() {
            return fontColor;
        }

        public void setFontColor(String fontColor) {
            this.fontColor = fontColor;
        }

        public String getNoiseColor() {
            return noiseColor;
        }

        public void setNoiseColor(String noiseColor) {
            this.noiseColor = noiseColor;
        }

        public String getBackgroundColorFrom() {
            return backgroundColorFrom;
        }

        public void setBackgroundColorFrom(String backgroundColorFrom) {
            this.backgroundColorFrom = backgroundColorFrom;
        }

        public String getBackgroundColorTo() {
            return backgroundColorTo;
        }

        public void setBackgroundColorTo(String backgroundColorTo) {
            this.backgroundColorTo = backgroundColorTo;
        }
    }

    /**
     * 短信验证码
     */
    public static class Sms {
        /**
         * 验证码过期时间(分)
         */
        private Integer expire = 5;
        /**
         * 验证码字符来源，验证码值从中获取
         */
        private String charSource = "0123456789";
        /**
         * 验证码字符长度
         */
        private Integer charLength = 6;
        /**
         * 发送验证码间隔时间(秒)
         */
        private Integer interval = 60;
        /**
         * 限制时间内发送次数上限(-1则无限制)
         */
        private Integer limitTime = -1;
        /**
         * 次数限制在多长时间内(小时)
         */
        private Integer limitInterval = 12;
        /**
         * 验证码验证次数
         */
        private Integer maxErrorTime = 5;

        public Integer getExpire() {
            return expire;
        }

        public void setExpire(Integer expire) {
            this.expire = expire;
        }

        public String getCharSource() {
            return charSource;
        }

        public void setCharSource(String charSource) {
            this.charSource = charSource;
        }

        public Integer getCharLength() {
            return charLength;
        }

        public void setCharLength(Integer charLength) {
            this.charLength = charLength;
        }

        public Integer getInterval() {
            return interval;
        }

        public void setInterval(Integer interval) {
            this.interval = interval;
        }

        public Integer getLimitTime() {
            return limitTime;
        }

        public void setLimitTime(Integer limitTime) {
            this.limitTime = limitTime;
        }

        public Integer getLimitInterval() {
            return limitInterval;
        }

        public void setLimitInterval(Integer limitInterval) {
            this.limitInterval = limitInterval;
        }

        public Integer getMaxErrorTime() {
            return maxErrorTime;
        }

        public void setMaxErrorTime(Integer maxErrorTime) {
            this.maxErrorTime = maxErrorTime;
        }
    }

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    public boolean isTestDisable() {
        return testDisable;
    }

    public void setTestDisable(boolean testDisable) {
        this.testDisable = testDisable;
    }

    public Image getImage() {
        return image;
    }

    public void setImage(Image image) {
        this.image = image;
    }

    public Sms getSms() {
        return sms;
    }

    public void setSms(Sms sms) {
        this.sms = sms;
    }

}
