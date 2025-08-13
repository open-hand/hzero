package org.hzero.platform.domain.service.datasource;

import java.io.IOException;

import org.hzero.core.util.Reflections;
import org.hzero.platform.domain.entity.Datasource;
import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * 抽象连接
 *
 * @author xiaoyu.zhao@hand-china.com
 */
public abstract class AbstractConnection<T> implements Connection {

    @Autowired
    private ObjectMapper objectMapper;

    private Class<T> clazz;

    @SuppressWarnings("unchecked")
    public AbstractConnection() {
        clazz = Reflections.getClassGenericType(this.getClass());
    }

    /**
     * 解析配置
     *
     * @param settings 配置json字符串
     * @return 配置
     */
    protected T parse(String settings) {
        try {
            return objectMapper.readValue(settings, clazz);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 序列化配置
     *
     * @param pojo 配置
     * @return 配置json字符串
     */
    protected String toJSON(T pojo) {
        try {
            return objectMapper.writeValueAsString(pojo);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 转换数据实体参数，实体类需自行实现该方法
     *
     * @param datasource 数据来源
     * @param entity 转换实体
     * @return 转换后的数据实体
     */
    protected abstract T convertEntity(Datasource datasource, T entity);

}
