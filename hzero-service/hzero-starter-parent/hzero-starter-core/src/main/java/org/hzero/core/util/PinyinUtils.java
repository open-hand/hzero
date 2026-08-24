package org.hzero.core.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

/**
 * 
 * 拼音处理工具类
 * 
 * @author xianzhi.chen@hand-china.com 2019年4月18日下午2:17:04
 */
public class PinyinUtils {

    private static HanyuPinyinOutputFormat format = new HanyuPinyinOutputFormat();

    private PinyinUtils() {}

    private static HanyuPinyinOutputFormat getFormat() {
        format.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
        return format;
    }

    /**
     * 
     * 获取拼音全拼
     * 
     * @param name 中文
     * @return 拼音字符串
     */
    public static String getPinyin(String name) {
        name = name.trim().replace(" ", "");
        List<String> standardList = new ArrayList<>();
        int n = name.length();
        for (int i = 0; i < n; i++) {
            List<String> l = getCharacterPinYinSet(name.charAt(i));
            int num = l.size();
            if (i == 0) {
                for (int k = 0; k < num; k++) {
                    standardList.add(l.get(k));
                }
            } else {
                List<String> elseList = new ArrayList<>();
                for (int k = 0; k < num; k++) {
                    List<String> list = new ArrayList<>(standardList);
                    for (int m = 0; m < list.size(); m++) {
                        list.set(m, list.get(m) + l.get(k));
                    }
                    elseList.addAll(list);
                }
                standardList = elseList;

            }

        }
        StringBuilder returnString = new StringBuilder();
        returnString.append("|");
        for (int i = 0; i < standardList.size(); i++) {
            returnString.append(standardList.get(i)).append("|");
        }

        return returnString.toString();
    }

    /**
     * 
     * 获取拼音首字母
     * 
     * @param name 中文
     * @return 拼音首字母字符串
     */
    public static String getPinyinCapital(String name) {
        name = name.trim().replace(" ", "");
        List<String> standardList = new ArrayList<>();
        int n = name.length();
        for (int i = 0; i < n; i++) {
            List<String> l = getCharacterPinYinTitleSet(name.charAt(i));

            int num = l.size();

            if (i == 0) {
                for (int k = 0; k < num; k++) {
                    standardList.add(l.get(k));
                }
            } else {
                List<String> elseList = new ArrayList<>();
                for (int k = 0; k < num; k++) {
                    List<String> list = new ArrayList<>(standardList);
                    for (int m = 0; m < list.size(); m++) {
                        list.set(m, list.get(m) + l.get(k));
                    }
                    elseList.addAll(list);
                }
                standardList = elseList;

            }

        }
        StringBuilder returnString = new StringBuilder();
        returnString.append("|");
        for (int i = 0; i < standardList.size(); i++) {
            returnString.append(standardList.get(i)).append("|");
        }
        return returnString.toString();
    }

    /**
     * 转换单个中文字符为多音字数组
     */
    private static List<String> getCharacterPinYinSet(char c) {
        String[] pinyin = null;
        try {
            pinyin = PinyinHelper.toHanyuPinyinStringArray(c, getFormat());
        } catch (BadHanyuPinyinOutputFormatCombination e) {
            e.printStackTrace();
        }
        if (null == pinyin) {
            pinyin = new String[1];
            pinyin[0] = String.valueOf(c);
        }

        Set<String> set = new HashSet<>(Arrays.asList(pinyin));
        return new ArrayList<>(set);
    }

    /**
     * 转换单个中文字符为常用拼音
     */
    private static List<String> getCharacterPinYinTitleSet(char c) {
        String[] pinyin = null;
        try {
            pinyin = PinyinHelper.toHanyuPinyinStringArray(c, getFormat());
        } catch (BadHanyuPinyinOutputFormatCombination e) {
            e.printStackTrace();
        }
        if (null == pinyin) {
            pinyin = new String[1];
            pinyin[0] = String.valueOf(c);
        }
        String[] p = new String[pinyin.length];
        for (int i = 0; i < pinyin.length; i++) {
            p[i] = pinyin[i].substring(0, 1);
        }
        Set<String> set = new HashSet<>(Arrays.asList(p));
        return new ArrayList<>(set);
    }

}
