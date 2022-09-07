package org.hzero.boot.platform.form.result;

import org.hzero.boot.platform.form.constant.ErrorType;
import org.hzero.boot.platform.form.constant.FormConstants;

import java.util.ArrayList;
import java.util.List;

/**
 * 校验结果信息封装
 *
 * @author liufanghan 2019/11/20 10:49
 */
public class ConstraintsCheckResult {

    /**
     * 校验结果 true - 成功 false - 失败
     */
    private boolean success;

    /**
     * 校验结果详情，存放校验失败的值相关的错误信息
     */
    private List<String> resultInfo;

    public ConstraintsCheckResult(int length) {
        success = true;
        resultInfo = new ArrayList<>(length);
    }

    public ConstraintsCheckResult() {
        success = true;
    }

    public void error(String result, ErrorType errorType) {
        switch (errorType) {
            case CONFIG_VALUE_MISMATCH:
                resultInfo.add(String.format(FormConstants.ErrorMessage.ERROR_VALUE_MISMATCH, result));
                break;
            case MODIFY_NOT_ALLOW:
                resultInfo.add(String.format(FormConstants.ErrorMessage.ERROR_VALUE_MODIFY, result));
                break;
            case INVALID_REGULAR_EXPRESSION:
                resultInfo.add(String.format(FormConstants.ErrorMessage.INVALID_REGULAR_EXPRESSION, result));
                break;
            default:
                break;
        }
        success = false;
    }

    public boolean isSuccess() {
        return success;
    }

    public List<String> getResultInfo() {
        return resultInfo;
    }
}
