package org.hzero.export.annotation;

/**
 * 默认空批注
 *
 * @author XCXCXCXCX
 * @date 2020/5/11 4:27 下午
 */
public class EmptyComment implements Comment {

    @Override
    public CommentProperty getComment() {
        return null;
    }
}
