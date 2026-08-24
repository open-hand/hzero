package org.hzero.mybatis.config;

import org.apache.ibatis.session.SqlSessionFactory;
import org.hzero.mybatis.BatchInsertHelper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 批量插入配置类
 *
 * @author min.wang01@hand-china.com 2018/08/28 16:18
 */
@Configuration
public class BatchInsertHelperConfig {

    @Value("${hzero.supporter.batch-insert.slice-size:500}")
    private int sliceSize;

    @Bean
    @ConditionalOnMissingBean
    public BatchInsertHelper batchInsertHelper(SqlSessionFactory sqlSessionFactory) {
        return new BatchInsertHelper(sqlSessionFactory, this.sliceSize);
    }
}
