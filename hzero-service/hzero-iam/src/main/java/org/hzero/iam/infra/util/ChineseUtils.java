package org.hzero.iam.infra.util;

import java.util.regex.Pattern;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.HanyuPinyinVCharType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.hzero.core.base.BaseConstants;

/**
 * 汉语工具
 *
 * @author bojiangzhou 2019/03/04
 */
public class ChineseUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(ChineseUtils.class);

    private static final HanyuPinyinOutputFormat PINYIN_FORMAT;

    private static final Pattern CHAR = Pattern.compile("[a-z-A-Z]*");
    private static final Pattern CHINESE = Pattern.compile("\\u4E00-\\u9FA5]*");

    static {
        PINYIN_FORMAT = new HanyuPinyinOutputFormat();
        PINYIN_FORMAT.setCaseType(HanyuPinyinCaseType.LOWERCASE);
        PINYIN_FORMAT.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
        PINYIN_FORMAT.setVCharType(HanyuPinyinVCharType.WITH_V);
    }

    /**
     * 提取首字母大写，中文则提取首字母，英文则提取单词首字母
     * 支持带空格，其它不支持
     * 
     * @param str 字符串
     * @return 大写首字母
     */
    public static String extractCapitalInitial(String str) {
        String[] arr = StringUtils.splitByWholeSeparator(StringUtils.trim(str), BaseConstants.Symbol.SPACE);
        if (arr == null) {
            return null;
        }
        StringBuilder initial = new StringBuilder();
        for (String s : arr) {
            if (CHAR.matcher(s).matches()) {
                initial.append(s.charAt(0));
            }
            else if (CHINESE.matcher(s).matches()) {
                try {
                    for (char c : s.toCharArray()) {
                        String[] pinyin = PinyinHelper.toHanyuPinyinStringArray(c, PINYIN_FORMAT);
                        if (pinyin != null && pinyin.length > 0) {
                            initial.append(pinyin[0].charAt(0));
                        }
                    }
                } catch (BadHanyuPinyinOutputFormatCombination e) {
                    LOGGER.warn("parse pinyin error. {}", e.getMessage());
                }
            }
        }

        return initial.toString().toUpperCase();
    }


}
