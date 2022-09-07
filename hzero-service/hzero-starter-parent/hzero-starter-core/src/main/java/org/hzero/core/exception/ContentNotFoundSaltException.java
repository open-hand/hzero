package org.hzero.core.exception;

/**
 * @author qingsheng.chen@hand-china.com
 */
public class ContentNotFoundSaltException extends RuntimeException {

    public ContentNotFoundSaltException() {
        super("Unable to find salt in content.");
    }

    public ContentNotFoundSaltException(String content, String salt) {
        super("Unable to find salt[" + salt + "] in content[" + content + "].");
    }

}
