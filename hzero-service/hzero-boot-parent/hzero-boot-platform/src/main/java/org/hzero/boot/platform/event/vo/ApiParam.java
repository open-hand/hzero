package org.hzero.boot.platform.event.vo;

import org.hzero.boot.platform.event.Constants;

import java.util.HashMap;

/**
 * API调用参数封装
 *
 * @author jiangzhou.bo@hand-china.com 2018/06/14 15:35
 */
public class ApiParam extends HashMap<String, Object> implements EventParam {

    private static final long serialVersionUID = 3885646838813320581L;

    public ApiParam() {
    }

    public ApiParam(int initialCapacity) {
        super(initialCapacity);
    }

    @Override
    public String getType() {
        return Constants.CallType.API;
    }
}
