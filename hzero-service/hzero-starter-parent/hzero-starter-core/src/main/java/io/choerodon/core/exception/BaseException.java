package io.choerodon.core.exception;


/**
 * 基础异常类.
 *
 * @author njq.niu@hand-china.com
 * @since 2016年1月28日
 */
public abstract class BaseException extends Exception implements IBaseException {

    private static final long serialVersionUID = 1L;

    // e.g. ORDER_FROZEN
    private String code;

    private String descriptionKey;

    private Object[] parameters;

    /**
     * 不应该直接实例化,应该定义子类.
     *
     * @param code           异常 code,通常与模块 CODE 对应
     * @param descriptionKey 异常消息代码,系统描述中定义
     * @param parameters     如果没有参数,可以传 null
     */
    public BaseException(String code, String descriptionKey, Object... parameters) {
        super(descriptionKey);
        this.code = code;
        this.descriptionKey = descriptionKey;
        this.parameters = parameters;
    }

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public String getDescriptionKey() {
        return descriptionKey;
    }

    @Override
    public Object[] getParameters() {
        return parameters;
    }

    @Override
    public void setCode(String code) {
        this.code = code;
    }

    @Override
    public void setDescriptionKey(String descriptionKey) {
        this.descriptionKey = descriptionKey;
    }

    @Override
    public void setParameters(Object[] parameters) {
        this.parameters = parameters;
    }

}
