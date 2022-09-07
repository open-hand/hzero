package org.hzero.oauth.config;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import org.hzero.oauth.security.config.SecurityProperties;

@Component
public class MultiLanguageConfig {
    private static final Logger LOG = LogManager.getLogger(MultiLanguageConfig.class);

    /**
     * 配置文件路径
     */
    private final static String CONFIG_FILE = "languages/languages";

    /**
     * 编码
     */
    private final static String ENCODE = "UTF-8";

    @Autowired
    private SecurityProperties securityProperties;

    /**
     * 按指定的lang获取所有国际化信息
     *
     * @param lang
     * @return String
     */
    public Map<String, String> getLanguageValue(String lang) {
        InputStream in = null;
        try {
            Map<String, String> map = new HashMap<String, String>(64);
            Properties pps = new Properties();
            in = MultiLanguageConfig.class.getClassLoader().getResourceAsStream(CONFIG_FILE + "_" + lang + ".properties");
            if (in == null) {
                in = MultiLanguageConfig.class.getClassLoader().getResourceAsStream(CONFIG_FILE + "_" + securityProperties.getDefaultLanguage() + ".properties");
            }
            Assert.notNull(in, "language file not found.");
            pps.load(new InputStreamReader(in, ENCODE));
            Enumeration<?> ele = pps.propertyNames();
            while (ele.hasMoreElements()) {
                String strKey = (String) ele.nextElement();
                String strValue = pps.getProperty(strKey);
                map.put(strKey, strValue);
            }
            return map;
        } catch (IOException e) {
            LOG.error(e);
            return new HashMap<>(2);
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    LOG.error(e);
                }
            }
        }
    }

    public String getCodeValue(String code, String lang) {
        try {
            if (lang == null || "".equals(lang)) {
                lang = securityProperties.getDefaultLanguage();
            }
            Map<String, String> map = getLanguageValue(lang);
            if (map != null && map.get(code) != null) {
                return map.get(code).toString();
            } else {
                return code;
            }
        } catch (Exception e) {
            return code;
        }
    }

    public String getCodeValue(String code, Map<String, String> map) {
        try {
            if (map != null && map.get(code) != null) {
                return map.get(code).toString();
            } else {
                return code;
            }
        } catch (Exception e) {
            return code;
        }
    }

}
