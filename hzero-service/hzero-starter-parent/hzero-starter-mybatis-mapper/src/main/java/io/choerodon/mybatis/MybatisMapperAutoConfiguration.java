package io.choerodon.mybatis;

import io.choerodon.mybatis.code.DbType;
import io.choerodon.mybatis.constant.CommonMapperConfigConstant;
import io.choerodon.mybatis.constant.DatabaseProductName;
import io.choerodon.mybatis.domain.Config;
import io.choerodon.mybatis.handler.Boolean2IntTypeHandler;
import io.choerodon.mybatis.helper.MapperHelper;
import io.choerodon.mybatis.helper.feign.failback.LanguageRemoteServiceImpl;
import io.choerodon.mybatis.language.MultiLanguageInterceptor;
import io.choerodon.mybatis.pagehelper.Dialect;
import io.choerodon.mybatis.pagehelper.PageHelper;
import io.choerodon.mybatis.pagehelper.PageInterceptor;
import io.choerodon.mybatis.pagehelper.dialect.*;
import io.choerodon.mybatis.pagehelper.parser.ICountSqlParser;
import io.choerodon.mybatis.pagehelper.parser.IOrderByParser;
import io.choerodon.mybatis.pagehelper.parser.IPageSqlParser;
import io.choerodon.mybatis.pagehelper.parser.ParserConfiguration;
import io.choerodon.mybatis.spring.CommonMapperScannerConfigurer;
import io.choerodon.mybatis.spring.resolver.MethodArgParamResolverConfig;
import org.apache.ibatis.mapping.DatabaseIdProvider;
import org.apache.ibatis.mapping.VendorDatabaseIdProvider;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.type.JdbcType;
import org.hzero.core.redis.RedisHelper;
import org.hzero.core.util.Reflections;
import org.hzero.mybatis.config.DataSecurityProperty;
import org.hzero.mybatis.helper.DataSecurityHelper;
import org.hzero.mybatis.parser.SqlParserHelper;
import org.hzero.mybatis.security.CrossSchemaInterceptor;
import org.hzero.mybatis.security.DataSecurityInterceptor;
import org.hzero.mybatis.security.SecurityTokenInterceptor;
import org.hzero.mybatis.service.DataSecurityKeyService;
import org.hzero.mybatis.service.impl.RedisDataSecurityKeyService;
import org.hzero.mybatis.util.DatabaseUtils;
import org.mybatis.spring.mapper.MapperScannerConfigurer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;

import javax.servlet.*;
import javax.sql.DataSource;
import java.io.IOException;
import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;


@Configuration
@Import({MethodArgParamResolverConfig.class, ParserConfiguration.class})
@EnableFeignClients("io.choerodon.mybatis.helper.feign")
public class MybatisMapperAutoConfiguration implements EnvironmentAware {

    private static final Logger logger = LoggerFactory.getLogger(MybatisMapperAutoConfiguration.class);

    private String datasourceUrl;
    private String keyGenerator;
    // DataSecurity
    private boolean dataSecurityDefaultOpen;
    private DataSecurityProperty.IsolationLevel dataSecurityIsolationLevel;
    private String dataSecurityKey;
    private boolean dataSecurityAsDefaultKey;

    @Bean
    @ConditionalOnMissingBean(MapperHelper.class)
    public MapperHelper mapperHelper() {
        return new MapperHelper();
    }

    @Bean
    @ConditionalOnMissingBean(DataSecurityKeyService.class)
    public DataSecurityKeyService dataSecurityKeyService(RedisHelper redisHelper) {
        DataSecurityKeyService keyService = new RedisDataSecurityKeyService(redisHelper);
        keyService.storeSecurityKey(dataSecurityKey, dataSecurityAsDefaultKey);
        return keyService;
    }

    /**
     * 配置扫描包路径
     *
     * @return MapperScannerConfigurer
     */
    @Bean
    @Primary
    public MapperScannerConfigurer mapperScannerConfigurer(MapperHelper mapperHelper) {
        CommonMapperScannerConfigurer configurer = new CommonMapperScannerConfigurer(mapperHelper);
        configurer.setBasePackage("*.**.mapper");
        Config config = configurer.getMapperHelper().getConfig();
        config.setSeqFormat("{3}_s.nextval");
        DbType dbType = DbType.MYSQL;
        if (this.datasourceUrl.startsWith(CommonMapperConfigConstant.DB_URL_PREFIX_H2)) {
            dbType = DbType.H2;
        } else if (this.datasourceUrl.startsWith(CommonMapperConfigConstant.DB_URL_PREFIX_ORACLE)) {
            dbType = DbType.ORACLE;
        } else if (this.datasourceUrl.startsWith(CommonMapperConfigConstant.DB_URL_PREFIX_SQLSERVER)) {
            dbType = DbType.SQLSERVER;
        } else if (this.datasourceUrl.startsWith(CommonMapperConfigConstant.DB_URL_PREFIX_SAP)) {
            dbType = DbType.HANA;
        } else if (this.datasourceUrl.startsWith(CommonMapperConfigConstant.DB_URL_PREFIX_ZENITH)) {
            dbType = DbType.ORACLE;
        }
        config.setDbType(dbType);
        if (keyGenerator == null) {
            config.setBefore(dbType.isSupportSequence());
            config.setIdentity(dbType.getIdentity());
        } else {
            config.setBefore(true);
            config.setIdentity(keyGenerator);
        }
        config.setSafeDelete(true);

        // 设置 DbType
        Field field = Reflections.getField(DatabaseUtils.class, "dbType");
        try {
            assert field != null;
            field.set(DatabaseUtils.class, dbType);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        return configurer;
    }

    /**
     * 配置支持的数据库方言以及分页、排序插件
     *
     * @param dataSource        dataSource
     * @param sqlSessionFactory sqlSessionFactory
     * @return Dialect
     * @throws SQLException SQLException
     */
    @Bean
    public Dialect dialect(DataSource dataSource,
                           SqlSessionFactory sqlSessionFactory,
                           ICountSqlParser countSqlParser,
                           IOrderByParser orderByParser,
                           IPageSqlParser pageSqlParser,
                           DataSecurityKeyService dataSecurityKeyService) throws SQLException {
        Dialect dialect = null;
        try (Connection connection = dataSource.getConnection()) {
            String productName = connection.getMetaData().getDatabaseProductName();
            if (DatabaseProductName.SQL_SERVER.value().equals(productName)) {
                dialect = new SqlServerDialect(countSqlParser, pageSqlParser);
            } else if (DatabaseProductName.ORACLE.value().equals(productName)
                    || DatabaseProductName.GAUSS.value().equals(productName)) {
                dialect = new OracleDialect(countSqlParser);
            } else if (DatabaseProductName.MYSQL.value().equals(productName)
                    || DatabaseProductName.HDB.value().equals(productName)) {
                dialect = new MySqlDialect(countSqlParser);
            } else if (DatabaseProductName.POSTGRE.value().equals(productName)) {
                dialect = new PostgreSqlDialect(countSqlParser);
            } else {
                logger.warn("未知数据库类型，默认使用MySQL方言。");
                dialect = new MySqlDialect(countSqlParser);
            }
            DialectHelper.setDialect(dialect);
            PageInterceptor pageInterceptor = new PageInterceptor(dialect, orderByParser);
            pageInterceptor.setProperties(new Properties());
            sqlSessionFactory.getConfiguration().addInterceptor(pageInterceptor);

            sqlSessionFactory.getConfiguration().addInterceptor(new MultiLanguageInterceptor());
            sqlSessionFactory.getConfiguration().addInterceptor(new SecurityTokenInterceptor());
            DataSecurityInterceptor.setDataSecurityKeyService(dataSecurityKeyService);
            sqlSessionFactory.getConfiguration().addInterceptor(new DataSecurityInterceptor(dataSecurityDefaultOpen, dataSecurityIsolationLevel));
            sqlSessionFactory.getConfiguration().addInterceptor(new CrossSchemaInterceptor());
            sqlSessionFactory.getConfiguration().getTypeHandlerRegistry().register(Boolean2IntTypeHandler.class);

            //配置JdbcTypeForNull, oracle数据库必须配置，解决插入null的时候报错问题
            sqlSessionFactory.getConfiguration().setJdbcTypeForNull(JdbcType.NULL);
        } catch (SQLException e) {
            logger.info("[sql exception]" + e);
            throw e;
        }
        return dialect;
    }


    /**
     * 自动识别使用的数据库类型
     * 在mapper.xml中databaseId的值就是跟这里对应，
     * 如果没有databaseId选择则说明该sql适用所有数据库
     *
     * @return
     */
    @Bean
    public DatabaseIdProvider getDatabaseIdProvider() {
        DatabaseIdProvider databaseIdProvider = new VendorDatabaseIdProvider();
        Properties properties = new Properties();
        properties.setProperty("Oracle", "oracle");
        properties.setProperty("MySQL", "mysql");
        properties.setProperty("DB2", "db2");
        properties.setProperty("Derby", "derby");
        properties.setProperty("H2", "h2");
        properties.setProperty("HSQL", "hsql");
        properties.setProperty("Informix", "informix");
        properties.setProperty("MS-SQL", "ms-sql");
        properties.setProperty("PostgreSQL", "postgresql");
        properties.setProperty("Sybase", "sybase");
        properties.setProperty("Hana", "hana");
        properties.setProperty("Gauss", "gauss");
        databaseIdProvider.setProperties(properties);
        return databaseIdProvider;
    }

    @Bean
    public MybatisInterceptorAutoClearFilter mybatisInterceptorAutoClearFilter() {
        return new MybatisInterceptorAutoClearFilter();
    }

    public static class MybatisInterceptorAutoClearFilter implements Filter {

        @Override
        public void init(FilterConfig filterConfig) throws ServletException {
            // do nothing
        }

        @Override
        public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
            try {
                chain.doFilter(request, response);
            } finally {
                // 清除 Sql 拦截设置
                SqlParserHelper.clear();
                // 清除 数据加解密设置
                DataSecurityHelper.clear();
                // 清除 分页拦截器设置
                PageHelper.clearPage();
                PageHelper.clearSort();
            }
        }

        @Override
        public void destroy() {
            // do nothing
        }
    }


    @Override
    public void setEnvironment(Environment environment) {
        datasourceUrl = environment.getProperty("spring.datasource.url", "");
        keyGenerator = environment.getProperty("mybatis.configuration.key-generator");
        dataSecurityDefaultOpen = Boolean.parseBoolean(environment.getProperty("hzero.mybatis-mapper.data-security.default-open"));
        dataSecurityIsolationLevel = DataSecurityProperty.IsolationLevel.parser(environment.getProperty("hzero.mybatis-mapper.data-security.isolation-level"));
        dataSecurityKey = environment.getProperty("hzero.mybatis-mapper.data-security.security-key");
        dataSecurityAsDefaultKey = Boolean.parseBoolean(environment.getProperty("hzero.mybatis-mapper.data-security.as-default-key"));
    }

    @Bean
    @ConditionalOnMissingBean(LanguageRemoteServiceImpl.class)
    public LanguageRemoteServiceImpl languageRemoteService() {
        return new LanguageRemoteServiceImpl();
    }

    @Bean
    public MybatisMapperAutoConfiguration mybatisMapperAutoConfiguration() {
        return new MybatisMapperAutoConfiguration();
    }
}