package org.hzero.export.annotation;

/**
 * 默认空批注
 *
 * @author XCXCXCXCX 2020/5/11 4:27
 */
public class EmptyComment implements Comment {

    @Override
    public CommentProperty getComment() {
        return null;
    }
}
