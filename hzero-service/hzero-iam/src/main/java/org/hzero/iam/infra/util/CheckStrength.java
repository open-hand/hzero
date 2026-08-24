/*
 * Copyright (C) 2014 venshine.cn@gmail.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package org.hzero.iam.infra.util;

/**
 * 检测密码强度
 *
 * @author venshine
 */
public class CheckStrength {

    public enum LEVEL {
        EASY,
        MEDIUM,
        STRONG,
        VERY_STRONG,
        EXTREMELY_STRONG
    }

    private static final int NUM = 1;
    private static final int SMALL_LETTER = 2;
    private static final int CAPITAL_LETTER = 3;
    private static final int OTHER_CHAR = 4;

    /**
     * Simple password dictionary
     */
    private final static String[] DICTIONARY = {"password", "abc123", "iloveyou", "adobe123", "123123", "sunshine",
            "1314520", "a1b2c3", "123qwe", "aaa111", "qweasd", "admin", "passwd"};

    /**
     * Check character's type, includes num, capital letter, small letter and other character.
     */
    private static int checkCharacterType(char c) {
        if (c >= 48 && c <= 57) {
            return NUM;
        }
        if (c >= 65 && c <= 90) {
            return CAPITAL_LETTER;
        }
        if (c >= 97 && c <= 122) {
            return SMALL_LETTER;
        }
        return OTHER_CHAR;
    }

    /**
     * Count password's number by different type
     */
    private static int countLetter(String passwd, int type) {
        int count = 0;
        if (null != passwd && passwd.length() > 0) {
            for (char c : passwd.toCharArray()) {
                if (checkCharacterType(c) == type) {
                    count++;
                }
            }
        }
        return count;
    }

    /**
     * Check password's strength
     * @return strength level
     */
    public static int checkPasswordStrength(String passwd) {
        if (StringUtils.equalsNull(passwd)) {
            throw new IllegalArgumentException("password is empty");
        }
        int len = passwd.length();
        int level = 0;

        // increase points
        int numLetter = countLetter(passwd, NUM);
        int smallLetter = countLetter(passwd, SMALL_LETTER);
        int capitalLetter = countLetter(passwd, CAPITAL_LETTER);
        int otherChar = countLetter(passwd, OTHER_CHAR);
        if (numLetter > 0) {
            level++;
        }
        if (smallLetter > 0) {
            level++;
        }
        if (len > 4 && capitalLetter > 0) {
            level++;
        }
        if (len > 6 && otherChar > 0) {
            level++;
        }

        if (len > 4 && numLetter > 0 && smallLetter > 0
                        || numLetter > 0 && capitalLetter > 0
                        || numLetter > 0 && otherChar > 0
                        || smallLetter > 0 && capitalLetter > 0
                        || smallLetter > 0 && otherChar > 0
                        || capitalLetter > 0 && otherChar > 0) {
            level++;
        }

        if (len > 6 && numLetter > 0 && smallLetter > 0
                        && capitalLetter > 0
                        || numLetter > 0 && smallLetter > 0 && otherChar > 0
                        || numLetter > 0 && capitalLetter > 0 && otherChar > 0
                        || smallLetter > 0 && capitalLetter > 0 && otherChar > 0) {
            level++;
        }

        if (len > 8 && numLetter > 0 && smallLetter > 0 && capitalLetter > 0 && otherChar > 0) {
            level++;
        }

        if (len > 6 && numLetter >= 3 && smallLetter >= 3
                        || numLetter >= 3 && capitalLetter >= 3
                        || numLetter >= 3 && otherChar >= 2
                        || smallLetter >= 3 && capitalLetter >= 3
                        || smallLetter >= 3 && otherChar >= 2
                        || capitalLetter >= 3 && otherChar >= 2) {
            level++;
        }

        if (len > 8 && numLetter >= 2 && smallLetter >= 2 && capitalLetter >= 2
                        || numLetter >= 2 && smallLetter >= 2 && otherChar >= 2
                        || numLetter >= 2 && capitalLetter >= 2 && otherChar >= 2
                        || smallLetter >= 2 && capitalLetter >= 2 && otherChar >= 2) {
            level++;
        }

        if (len > 10 && numLetter >= 2 && smallLetter >= 2 && capitalLetter >= 2 && otherChar >= 2) {
            level++;
        }

        if (otherChar >= 3) {
            level++;
        }
        if (otherChar >= 6) {
            level++;
        }

        if (len > 12) {
            level++;
            if (len >= 16) {
                level++;
            }
        }

        // decrease points
        if ("abcdefghijklmnopqrstuvwxyz".indexOf(passwd) > 0 || "ABCDEFGHIJKLMNOPQRSTUVWXYZ".indexOf(passwd) > 0) {
            level--;
        }
        if ("qwertyuiop".indexOf(passwd) > 0 || "asdfghjkl".indexOf(passwd) > 0 || "zxcvbnm".indexOf(passwd) > 0) {
            level--;
        }
        if (StringUtils.isNumeric(passwd) && ("01234567890".indexOf(passwd) > 0 || "09876543210".indexOf(passwd) > 0)) {
            level--;
        }

        if (numLetter == len || smallLetter == len
                        || capitalLetter == len) {
            level--;
        }

        if (len % 2 == 0) { // aaabbb
            String part1 = passwd.substring(0, len / 2);
            String part2 = passwd.substring(len / 2);
            if (part1.equals(part2)) {
                level--;
            }
            if (StringUtils.isCharEqual(part1) && StringUtils.isCharEqual(part2)) {
                level--;
            }
        }
        if (len % 3 == 0) { // ababab
            String part1 = passwd.substring(0, len / 3);
            String part2 = passwd.substring(len / 3, len / 3 * 2);
            String part3 = passwd.substring(len / 3 * 2);
            if (part1.equals(part2) && part2.equals(part3)) {
                level--;
            }
        }

        if (StringUtils.isNumeric(passwd) && len >= 6 && len < 10) { // 19881010 or 881010
            int year = 0;
            if (len == 8 || len == 6) {
                year = Integer.parseInt(passwd.substring(0, len - 4));
            }
            int size = StringUtils.sizeOfInt(year);
            int month = Integer.parseInt(passwd.substring(size, size + 2));
            int day = Integer.parseInt(passwd.substring(size + 2, len));
            if (year >= 1950 && year < 2050 && month >= 1 && month <= 12 && day >= 1 && day <= 31) {
                level--;
            }
        }

        for (String s : DICTIONARY) {
            if (passwd.equals(s) || s.contains(passwd)) {
                level--;
                break;
            }
        }

        if (len <= 6) {
            level--;
            if (len <= 4) {
                level--;
                if (len <= 3) {
                    level = 0;
                }
            }
        }

        if (StringUtils.isCharEqual(passwd)) {
            level = 0;
        }

        if (level < 0) {
            level = 0;
        }

        return level;
    }

    /**
     * Get password strength level, includes easy, midium, strong, very strong, extremely strong
     */
    public static LEVEL getPasswordLevel(String passwd) {
        int level = checkPasswordStrength(passwd);
        switch (level) {
            case 0:
            case 1:
            case 2:
            case 3:
                return LEVEL.EASY;
            case 4:
            case 5:
            case 6:
                return LEVEL.MEDIUM;
            case 7:
            case 8:
            case 9:
                //return LEVEL.STRONG;
            case 10:
            case 11:
            case 12:
                //return LEVEL.VERY_STRONG;
            default:
                //return LEVEL.EXTREMELY_STRONG;
                return LEVEL.STRONG;
        }
    }

    public static class StringUtils {

        private final static int[] SIZE_TABLE =
                        {9, 99, 999, 9999, 99999, 999999, 9999999, 99999999, 999999999, Integer.MAX_VALUE};

        /**
         * calculate the size of an integer number
         */
        public static int sizeOfInt(int x) {
            for (int i = 0;; i++) {
                if (x <= SIZE_TABLE[i]) {
                    return i + 1;
                }
            }
        }

        /**
         * Judge whether each character of the string equals
         */
        public static boolean isCharEqual(String str) {
            return str.replace(str.charAt(0), ' ').trim().length() == 0;
        }

        /**
         * Determines if the string is a digit
         */
        public static boolean isNumeric(String str) {
            for (int i = str.length(); --i >= 0;) {
                if (!Character.isDigit(str.charAt(i))) {
                    return false;
                }
            }
            return true;
        }

        /**
         * Judge whether the string is whitespace, empty ("") or null.
         */
        public static boolean equalsNull(String str) {
            int strLen;
            if (str == null || (strLen = str.length()) == 0 || "null".equalsIgnoreCase(str)) {
                return true;
            }
            for (int i = 0; i < strLen; i++) {
                if (!Character.isWhitespace(str.charAt(i))) {
                    return false;
                }
            }
            return true;
        }

    }

}
