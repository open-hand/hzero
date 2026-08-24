package io.choerodon.core.exception;

/**
 * @author xiangyu.qi@hand-china.com
 * @since 2018/3/27
 */
public class BaseRuntimeException extends RuntimeException implements IBaseException {

    private static final long serialVersionUID = 3225600638068413457L;
    private String code;

    private String descriptionKey;

    private Object[] parameters;

    public BaseRuntimeException(String code, Object... parameters) {
        super(code);
        this.code = code;
        this.descriptionKey = code;
        this.parameters = parameters;
    }

    /**
     * 不应该直接实例化,应该定义子类.
     *
     * @param code           异常 code,通常与模块 CODE 对应
     * @param descriptionKey 异常消息代码,系统描述中定义
     * @param parameters     如果没有参数,可以传 null
     */
    public BaseRuntimeException(String code, String descriptionKey, Object... parameters) {
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
