package org.hzero.admin.api.dto;

import java.util.Collection;
import java.util.List;

/**
 * 基础的访问统计数据传输对象
 *
 * @author bergturing on 2020-5-7.
 */
public abstract class BaseAccessStatisticsDTO<D> {
    /**
     * 日期信息
     */
    private List<String> dates;

    /**
     * 实体列表数据(API即为API列表/服务即为服务列表)
     */
    private Collection<String> entities;

    /**
     * 实体详细信息
     */
    private Collection<D> details;

    protected BaseAccessStatisticsDTO(List<String> dates, Collection<String> entities, Collection<D> details) {
        this.dates = dates;
        this.entities = entities;
        this.details = details;
    }

    public List<String> getDates() {
        return dates;
    }

    public Collection<String> getEntities() {
        return entities;
    }

    public Collection<D> getDetails() {
        return details;
    }

    @Override
    public String toString() {
        return "BaseAccessStatisticsDTO{" +
                "dates=" + dates +
                ", entities=" + entities +
                ", details=" + details +
                '}';
    }
}
