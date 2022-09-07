/*
 * Copyright 1999-2018 Alibaba Group Holding Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.hzero.starter.sqlparser.util;

/**
 * format validation This class encodes/decodes hexadecimal data
 * 
 * @author Jeffrey Rodriguez
 * @version $Id: HexBin.java,v 1.4 2007/07/19 04:38:32 ofung Exp $
 */
public final class HexBin {

    static private final int    BASE_LENGTH        = 128;
    static private final int    LOOKUP_LENGTH      = 16;
    static final private byte[] HEX_NUMBER_TABLE   = new byte[BASE_LENGTH];
    static final private char[] UPPER_CHARS        = new char[LOOKUP_LENGTH];
    static final private char[] LOWER_CHARS        = new char[LOOKUP_LENGTH];

    static {
        for (int i = 0; i < BASE_LENGTH; i++) {
            HEX_NUMBER_TABLE[i] = -1;
        }
        for (int i = '9'; i >= '0'; i--) {
            HEX_NUMBER_TABLE[i] = (byte) (i - '0');
        }
        for (int i = 'F'; i >= 'A'; i--) {
            HEX_NUMBER_TABLE[i] = (byte) (i - 'A' + 10);
        }
        for (int i = 'f'; i >= 'a'; i--) {
            HEX_NUMBER_TABLE[i] = (byte) (i - 'a' + 10);
        }

        for (int i = 0; i < 10; i++) {
            UPPER_CHARS[i] = (char) ('0' + i);
            LOWER_CHARS[i] = (char) ('0' + i);
        }
        for (int i = 10; i <= 15; i++) {
            UPPER_CHARS[i] = (char) ('A' + i - 10);
            LOWER_CHARS[i] = (char) ('a' + i - 10);
        }
    }
    
    public static String encode(byte[] bytes) {
        return encode(bytes, true);
    }

    public static String encode(byte[] bytes, boolean upperCase) {
        if (bytes == null) {
            return null;
        }

        final char[] chars = upperCase ? UPPER_CHARS : LOWER_CHARS;

        char[] hex = new char[bytes.length * 2];
        for (int i = 0; i < bytes.length; i++) {
            int b = bytes[i] & 0xFF;
            hex[i * 2] = chars[b >> 4];
            hex[i * 2 + 1] = chars[b & 0xf];
        }
        return new String(hex);
    }

    /**
     * Decode hex string to a byte array
     * 
     * @param encoded encoded string
     * @return return array of byte to encode
     */
    static public byte[] decode(String encoded) {
        if (encoded == null) {
            return null;
        }

        int lengthData = encoded.length();
        if (lengthData % 2 != 0) {
            return null;
        }

        char[] binaryData = encoded.toCharArray();
        int lengthDecode = lengthData / 2;
        byte[] decodedData = new byte[lengthDecode];
        byte temp1, temp2;
        char tempChar;
        for (int i = 0; i < lengthDecode; i++) {
            tempChar = binaryData[i * 2];
            temp1 = (tempChar < BASE_LENGTH) ? HEX_NUMBER_TABLE[tempChar] : -1;
            if (temp1 == -1) {
                return null;
            }
            tempChar = binaryData[i * 2 + 1];
            temp2 = (tempChar < BASE_LENGTH) ? HEX_NUMBER_TABLE[tempChar] : -1;
            if (temp2 == -1) {
                return null;
            }

            // 处理 byte 升级情况，若temp1或temp2为正数时则需 与 一个 0xff
            if (temp1 >= 0) {
                if (temp2 >= 0) {
                    decodedData[i] = (byte) (((temp1 & 0xff) << 4) | (temp2 & 0xff));
                } else {
                    decodedData[i] = (byte) (((temp1 & 0xff) << 4) | temp2);
                }
            } else {
                if (temp2 >= 0) {
                    decodedData[i] = (byte) ((temp1 << 4) | (temp2 & 0xff));
                } else {
                    decodedData[i] = (byte) ((temp1 << 4) | temp2);
                }
            }
        }
        return decodedData;
    }
}
