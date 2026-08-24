package org.hzero.core.util;

import java.util.Comparator;

import org.hzero.core.jackson.annotation.Sensitive;
import org.hzero.core.algorithm.structure.LinkedQueue;
import org.springframework.util.StringUtils;

/**
 * <p>
 * 敏感信息工具类
 * </p>
 *
 * @author qingsheng.chen 2018/9/17 星期一 14:04
 */
public class SensitiveUtils {
    private static final String SPLIT = ",";
    private static final String CONNECTION = "-";

    private SensitiveUtils() {
    }

    public static String generateCipherText(Sensitive sensitive, String value) {
        if (sensitive == null || !StringUtils.hasText(value)) {
            return value;
        }
        if (sensitive.cipher().length > 0) {
            return SensitiveUtils.generateCipherTextByCipher(value, sensitive.cipher(), sensitive.symbol(), sensitive.left(), sensitive.right());
        } else if (sensitive.clear().length > 0) {
            return SensitiveUtils.generateCipherTextByClear(value, sensitive.clear(), sensitive.symbol(), sensitive.left(), sensitive.right());
        } else {
            return SensitiveUtils.generateCipherTextByDirection(value, sensitive.symbol(), sensitive.left(), sensitive.right(), sensitive.reverse());
        }
    }

    public static String generateCipherTextByCipher(String clearText, String[] cipher, char symbol, int left, int right) {
        if (!StringUtils.hasText(clearText)) {
            return clearText;
        }
        return generateCipherText(clearText, Rule.analysis(cipher), symbol, false, left, right);
    }

    public static String generateCipherTextByClear(String clearText, String[] clear, char symbol, int left, int right) {
        if (!StringUtils.hasText(clearText)) {
            return clearText;
        }
        return generateCipherText(clearText, Rule.analysis(clear), symbol, true, left, right);
    }

    public static String generateCipherTextByDirection(String value, char symbol, int left, int right, boolean reverse) {
        return generateCipherText(value, Rule.EMPTY_RULE, symbol, reverse, left, right);
    }

    private static String generateCipherText(String clearText, Rule rule, char symbol, boolean reverse, int left, int right) {
        char[] clearChars = clearText.toCharArray();
        for (int i = 0; i < clearChars.length; ++i) {
            if (reverse ^ (i < left || i >= (clearChars.length - right) || rule.isIn(i))) {
                clearChars[i] = symbol;
            }
        }
        return new String(clearChars);
    }

    private static class Rule {
        private static final Rule EMPTY_RULE = new Rule();
        private LinkedQueue<Integer> indexList;
        private boolean endless;

        private Rule() {
            indexList = new LinkedQueue<>(Comparator.comparingInt(o -> o));
            endless = false;
        }

        static Rule analysis(String[] rules) {
            Rule rule = new Rule();
            if (rules == null || rules.length == 0) {
                return rule;
            }
            for (String ruleStr : rules) {
                if (!StringUtils.hasText(ruleStr)) {
                    continue;
                }
                String[] groups = ruleStr.split(SPLIT);
                for (String group : groups) {
                    if (!StringUtils.hasText(group)) {
                        continue;
                    }
                    String[] items = group.split(CONNECTION);
                    if (items.length == 1) {
                        rule.addIndex(items[0]);
                    } else {
                        rule.addIndex(items[0], items[items.length - 1]);
                    }
                    rule.endless |= group.endsWith(CONNECTION);
                }
            }
            return rule;
        }

        void addIndex(String index) {
            addIndex(Integer.parseInt(index));
        }

        void addIndex(String startIndex, String endIndex) {
            addIndex(Integer.parseInt(startIndex), Integer.parseInt(endIndex));
        }

        void addIndex(int index) {
            index -= 1;
            if (isIn(index)) {
                return;
            }
            indexList.add(index);
        }

        void addIndex(int startIndex, int endIndex) {
            for (int index = startIndex; index <= endIndex; ++index) {
                addIndex(index);
            }
        }

        boolean isIn(int index) {
            return indexList.contains(index) || (!indexList.isEmpty() && endless && indexList.getLast() < index);
        }

        @Override
        public String toString() {
            return "Rule{" +
                    "indexList=" + indexList +
                    ", endless=" + endless +
                    '}';
        }
    }
}
