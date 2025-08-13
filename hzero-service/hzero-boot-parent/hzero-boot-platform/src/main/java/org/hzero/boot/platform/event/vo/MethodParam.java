package org.hzero.boot.platform.event.vo;

import org.hzero.boot.platform.event.Constants;

import java.util.HashMap;

/**
 * 方法调用参数封装
 *
 * @author jiangzhou.bo@hand-china.com 2018/06/15 11:03
 */
public class MethodParam extends HashMap<String, Object> implements EventParam {

    private static final long serialVersionUID = 3394870011732948094L;

    public MethodParam() {
    }

    public MethodParam(int initialCapacity) {
        super(initialCapacity);
    }

    @Override
    public String getType() {
        return Constants.CallType.METHOD;
    }
}
