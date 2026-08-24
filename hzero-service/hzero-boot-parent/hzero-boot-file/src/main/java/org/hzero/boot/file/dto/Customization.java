package org.hzero.boot.file.dto;

/**
 * description
 *
 * @author shuangfei.zhu@hand-china.com 2020/09/11 14:11
 */
public class Customization {

    private String logoImage;
    private String logoLink;
    private String preloaderLogoImage;

    public String getLogoImage() {
        return logoImage;
    }

    public Customization setLogoImage(String logoImage) {
        this.logoImage = logoImage;
        return this;
    }

    public String getLogoLink() {
        return logoLink;
    }

    public Customization setLogoLink(String logoLink) {
        this.logoLink = logoLink;
        return this;
    }

    public String getPreloaderLogoImage() {
        return preloaderLogoImage;
    }

    public Customization setPreloaderLogoImage(String preloaderLogoImage) {
        this.preloaderLogoImage = preloaderLogoImage;
        return this;
    }

    @Override
    public String toString() {
        return "Customization{" +
                "logoImage='" + logoImage + '\'' +
                ", logoLink='" + logoLink + '\'' +
                ", preloaderLogoImage='" + preloaderLogoImage + '\'' +
                '}';
    }
}
